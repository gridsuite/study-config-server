/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.gridsuite.studyconfig.server.repositories.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceNADConfigService workspaceNADConfigService;

    public WorkspaceService(WorkspaceNADConfigService workspaceNADConfigService,
                            WorkspaceRepository workspaceRepository) {
        this.workspaceNADConfigService = workspaceNADConfigService;
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional(readOnly = true)
    public WorkspaceInfos getWorkspace(UUID workspaceId) {
        return getWorkspaceEntity(workspaceId).toDto();
    }

    private WorkspaceEntity getWorkspaceEntity(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));
    }

    @Transactional
    public UUID duplicateWorkspace(UUID sourceWorkspaceId) {
        WorkspaceEntity workspace = getWorkspaceEntity(sourceWorkspaceId).duplicate();
        workspaceNADConfigService.duplicateNadConfigs(workspace);

        return workspaceRepository.save(workspace).getId();
    }

    @Transactional
    public void replaceWorkspace(UUID workspaceId, UUID sourceWorkspaceId) {
        WorkspaceEntity existingWorkspace = getWorkspaceEntity(workspaceId);
        WorkspaceEntity sourceWorkspace = getWorkspaceEntity(sourceWorkspaceId);

        // Delete old NAD configs
        workspaceNADConfigService.deleteNadConfigs(existingWorkspace.getNadPanels().stream());

        // Duplicate source workspace
        WorkspaceEntity duplicated = sourceWorkspace.duplicate();
        workspaceNADConfigService.duplicateNadConfigs(duplicated);

        // Replace panels
        existingWorkspace.setName(duplicated.getName());
        existingWorkspace.getPanels().clear();
        existingWorkspace.getPanels().addAll(duplicated.getPanels());

        workspaceRepository.save(existingWorkspace);
    }

    @Transactional
    public void deleteWorkspace(UUID workspaceId) {
        workspaceNADConfigService.deleteNadConfigs(getWorkspaceEntity(workspaceId).getNadPanels().stream());
        workspaceRepository.deleteById(workspaceId);
    }
}
