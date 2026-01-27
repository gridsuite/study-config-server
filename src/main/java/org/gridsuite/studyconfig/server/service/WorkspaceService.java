/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.gridsuite.studyconfig.server.repositories.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceNADConfigService workspaceNADConfigService;

    @Transactional(readOnly = true)
    public Optional<WorkspaceEntity> getWorkspace(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId);
    }

    @Transactional
    public WorkspaceEntity duplicateWorkspace(UUID sourceWorkspaceId) {
        Optional<WorkspaceEntity> sourceWorkspace = workspaceRepository.findById(sourceWorkspaceId);
        if (sourceWorkspace.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + sourceWorkspaceId);
        }

        WorkspaceEntity workspace = sourceWorkspace.get().duplicate();
        workspaceNADConfigService.duplicateNadConfigs(workspace);

        return workspaceRepository.save(workspace);
    }

    @Transactional
    public void replaceWorkspace(UUID workspaceId, UUID sourceWorkspaceId) {
        WorkspaceEntity existingWorkspace = workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));
        WorkspaceEntity sourceWorkspace = workspaceRepository.findById(sourceWorkspaceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source workspace not found: " + sourceWorkspaceId));

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
        Optional<WorkspaceEntity> workspace = workspaceRepository.findById(workspaceId);
        if (workspace.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId);
        }

        workspaceNADConfigService.deleteNadConfigs(workspace.get().getNadPanels().stream());
        workspaceRepository.deleteById(workspaceId);
    }
}
