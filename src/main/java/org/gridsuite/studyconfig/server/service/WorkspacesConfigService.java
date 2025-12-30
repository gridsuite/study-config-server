/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.workspace.PanelInfos;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceMetadata;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspacesConfigInfos;
import org.gridsuite.studyconfig.server.entities.workspace.PanelEntity;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspacesConfigEntity;
import org.gridsuite.studyconfig.server.repositories.WorkspacesConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspacesConfigService {

    private final WorkspacesConfigRepository workspacesConfigRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:default-workspaces-config.json")
    private Resource defaultWorkspacesConfigResource;

    private static final String WORKSPACES_CONFIG_NOT_FOUND = "WorkspacesConfig not found with id: ";
    private static final String WORKSPACE_NOT_FOUND = "Workspace not found with id: ";

    private UUID createWorkspacesConfig(WorkspacesConfigInfos dto) {
        return workspacesConfigRepository.save(new WorkspacesConfigEntity(dto)).getId();
    }

    @Transactional
    public void deleteWorkspacesConfig(UUID id) {
        if (!workspacesConfigRepository.existsById(id)) {
            throw new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + id);
        }
        workspacesConfigRepository.deleteById(id);
    }

    @Transactional
    public UUID duplicateWorkspacesConfig(UUID id) {
        WorkspacesConfigInfos dto = findWorkspacesConfig(id).toDto();
        // Clear all IDs to ensure new entities are created
        WorkspacesConfigInfos dtoWithoutIds = new WorkspacesConfigInfos(
            null,
            dto.workspaces().stream()
                .map(w -> new WorkspaceInfos(
                    null,
                    w.name(),
                    w.panels().stream()
                        .map(p -> clonePanelWithId(p, null))
                        .toList()
                ))
                .toList()
        );
        return workspacesConfigRepository.save(new WorkspacesConfigEntity(dtoWithoutIds)).getId();
    }

    private PanelInfos clonePanelWithId(PanelInfos panel, UUID id) {
        PanelEntity entity = PanelEntity.toEntity(panel);
        entity.setId(id);
        return entity.toDto();
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMetadata> getWorkspacesMetadata(UUID configId) {
        WorkspacesConfigEntity entity = findWorkspacesConfig(configId);
        return entity.getWorkspaces().stream()
            .map(workspace -> new WorkspaceMetadata(
                workspace.getId(),
                workspace.getName(),
                workspace.getPanels().size()
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceInfos getWorkspace(UUID configId, UUID workspaceId) {
        return findWorkspace(configId, workspaceId).toDto();
    }

    @Transactional
    public void renameWorkspace(UUID configId, UUID workspaceId, String name) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        workspace.setName(name);
    }

    @Transactional(readOnly = true)
    public List<PanelInfos> getPanels(UUID configId, UUID workspaceId, List<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        return workspace.getPanels().stream()
            .filter(p -> panelIds == null || panelIds.isEmpty() || panelIds.contains(p.getId()))
            .map(PanelEntity::toDto)
            .toList();
    }

    @Transactional
    public void createOrUpdatePanels(UUID configId, UUID workspaceId, List<PanelInfos> panels) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);

        for (PanelInfos panelDto : panels) {
            if (panelDto.getId() != null) {  // Update existing panel
                PanelEntity existingPanel = workspace.getPanel(panelDto.getId());
                if (existingPanel != null) {
                    existingPanel.update(panelDto);
                    continue;
                }
            }
            // Create new panel (either no ID provided or ID doesn't exist)
            workspace.getPanels().add(PanelEntity.toEntity(panelDto));
        }
    }

    @Transactional
    public void deletePanels(UUID configId, UUID workspaceId, List<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        workspace.getPanels().removeIf(p -> panelIds.contains(p.getId()));
    }

    public UUID createDefaultWorkspacesConfig() {
        try {
            WorkspacesConfigInfos defaultConfig = readDefaultWorkspacesConfig();
            return createWorkspacesConfig(defaultConfig);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read default workspaces config", e);
        }
    }

    private WorkspacesConfigInfos readDefaultWorkspacesConfig() throws IOException {
        try (InputStream inputStream = defaultWorkspacesConfigResource.getInputStream()) {
            WorkspacesConfigInfos config = objectMapper.readValue(inputStream, WorkspacesConfigInfos.class);
            // Generate UUIDs for panels that don't have them
            return new WorkspacesConfigInfos(
                config.id(),
                config.workspaces().stream()
                    .map(workspace -> new WorkspaceInfos(
                        workspace.id(),
                        workspace.name(),
                        workspace.panels().stream()
                            .map(panel -> panel.getId() == null ? clonePanelWithId(panel, UUID.randomUUID()) : panel)
                            .toList()
                    ))
                    .toList()
            );
        }
    }

    private WorkspacesConfigEntity findWorkspacesConfig(UUID configId) {
        return workspacesConfigRepository.findById(configId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + configId));
    }

    private WorkspaceEntity findWorkspace(UUID configId, UUID workspaceId) {
        WorkspacesConfigEntity config = findWorkspacesConfig(configId);
        return config.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));
    }
}
