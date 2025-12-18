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
import org.gridsuite.studyconfig.server.mapper.WorkspaceMapper;
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
        WorkspacesConfigEntity entity = WorkspaceMapper.toEntity(dto);
        return workspacesConfigRepository.save(entity).getId();
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
        WorkspacesConfigEntity source = workspacesConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + id));

        WorkspacesConfigInfos dto = WorkspaceMapper.toDto(source);
        // Clear all IDs to ensure new entities are created
        WorkspacesConfigInfos dtoWithoutIds = new WorkspacesConfigInfos(
            null,
            dto.workspaces().stream()
                .map(w -> new WorkspaceInfos(
                    null,
                    w.name(),
                    w.panels().stream()
                        .map(p -> new PanelInfos(
                            null,
                            p.type(),
                            p.title(),
                            p.position(),
                            p.size(),

                            p.orderIndex(),
                            p.isMinimized(),
                            p.isMaximized(),
                            p.isPinned(),
                            p.isClosed(),
                            p.restorePosition(),
                            p.restoreSize(),
                            p.metadata()
                        ))
                        .toList()
                ))
                .toList()
        );
        WorkspacesConfigEntity entity = WorkspaceMapper.toEntity(dtoWithoutIds);
        return workspacesConfigRepository.save(entity).getId();
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMetadata> getWorkspacesMetadata(UUID configId) {
        WorkspacesConfigEntity entity = workspacesConfigRepository.findById(configId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + configId));
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
        WorkspacesConfigEntity config = workspacesConfigRepository.findById(configId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + configId));

        WorkspaceEntity workspace = config.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));

        return WorkspaceMapper.toWorkspaceDto(workspace);
    }

    @Transactional
    public void renameWorkspace(UUID configId, UUID workspaceId, String name) {
        WorkspacesConfigEntity config = workspacesConfigRepository.findById(configId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + configId));

        WorkspaceEntity workspace = config.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));

        workspace.setName(name);
    }

    @Transactional(readOnly = true)
    public List<PanelInfos> getPanels(UUID configId, UUID workspaceId, List<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        return workspace.getPanels().stream()
            .filter(p -> panelIds == null || panelIds.isEmpty() || panelIds.contains(p.getId()))
            .map(WorkspaceMapper::toPanelDto)
            .toList();
    }

    @Transactional
    public void createOrUpdatePanels(UUID configId, UUID workspaceId, List<PanelInfos> panels) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);

        for (PanelInfos panelDto : panels) {
            if (panelDto.id() != null) {
                // Update existing panel
                PanelEntity existingPanel = workspace.getPanels().stream()
                    .filter(p -> p.getId().equals(panelDto.id()))
                    .findFirst()
                    .orElse(null);

                if (existingPanel != null) {
                    updatePanelEntity(existingPanel, panelDto);
                } else {
                    // Panel ID provided but doesn't exist - create it with the given ID
                    PanelEntity newPanel = WorkspaceMapper.toPanelEntity(panelDto);
                    workspace.getPanels().add(newPanel);
                }
            } else {
                // Create new panel
                PanelEntity newPanel = WorkspaceMapper.toPanelEntity(panelDto);
                workspace.getPanels().add(newPanel);
            }
        }
    }

    @Transactional
    public void deletePanels(UUID configId, UUID workspaceId, List<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        workspace.getPanels().removeIf(p -> panelIds.contains(p.getId()));
    }

    private void updatePanelEntity(PanelEntity panel, PanelInfos dto) {
        panel.setType(dto.type());
        panel.setTitle(dto.title());
        panel.setPositionX(dto.position().x());
        panel.setPositionY(dto.position().y());
        panel.setSizeWidth(dto.size().width());
        panel.setSizeHeight(dto.size().height());
        panel.setOrderIndex(dto.orderIndex());
        panel.setMinimized(dto.isMinimized());
        panel.setMaximized(dto.isMaximized());
        panel.setPinned(dto.isPinned());
        panel.setClosed(dto.isClosed());

        if (dto.restorePosition() != null) {
            panel.setRestorePositionX(dto.restorePosition().x());
            panel.setRestorePositionY(dto.restorePosition().y());
        } else {
            panel.setRestorePositionX(null);
            panel.setRestorePositionY(null);
        }

        if (dto.restoreSize() != null) {
            panel.setRestoreSizeWidth(dto.restoreSize().width());
            panel.setRestoreSizeHeight(dto.restoreSize().height());
        } else {
            panel.setRestoreSizeWidth(null);
            panel.setRestoreSizeHeight(null);
        }

        // Update metadata
        if (dto.metadata() != null) {
            PanelInfos newDto = new PanelInfos(
                panel.getId(),
                dto.type(),
                dto.title(),
                dto.position(),
                dto.size(),
                dto.orderIndex(),
                dto.isMinimized(),
                dto.isMaximized(),
                dto.isPinned(),
                dto.isClosed(),
                dto.restorePosition(),
                dto.restoreSize(),
                dto.metadata()
            );
            PanelEntity tempPanel = WorkspaceMapper.toPanelEntity(newDto);
            panel.setMetadata(tempPanel.getMetadata());
        } else {
            panel.setMetadata(null);
        }
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
                            .map(panel -> panel.id() == null
                                ? new PanelInfos(
                                    UUID.randomUUID(),
                                    panel.type(),
                                    panel.title(),
                                    panel.position(),
                                    panel.size(),

                                    panel.orderIndex(),
                                    panel.isMinimized(),
                                    panel.isMaximized(),
                                    panel.isPinned(),
                                    panel.isClosed(),
                                    panel.restorePosition(),
                                    panel.restoreSize(),
                                    panel.metadata()
                                )
                                : panel
                            )
                            .toList()
                    ))
                    .toList()
            );
        }
    }

    private WorkspaceEntity findWorkspace(UUID configId, UUID workspaceId) {
        WorkspacesConfigEntity config = workspacesConfigRepository.findById(configId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACES_CONFIG_NOT_FOUND + configId));

        return config.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));
    }
}
