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
import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.workspace.*;
import org.gridsuite.studyconfig.server.mapper.WorkspaceMapper;
import org.gridsuite.studyconfig.server.repositories.WorkspaceCollectionRepository;
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
public class WorkspaceCollectionService {

    private final WorkspaceCollectionRepository workspaceCollectionRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:default-workspace-collection.json")
    private Resource defaultWorkspaceCollectionResource;

    private static final String WORKSPACE_COLLECTION_NOT_FOUND = "WorkspaceCollection not found with id: ";
    private static final String WORKSPACE_NOT_FOUND = "Workspace not found with id: ";
    private static final String PANEL_NOT_FOUND = "Panel not found with id: ";

    @Transactional(readOnly = true)
    public WorkspaceCollectionInfos getWorkspaceCollection(UUID id) {
        WorkspaceCollectionEntity entity = workspaceCollectionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + id));
        return WorkspaceMapper.toDto(entity);
    }

    public UUID createWorkspaceCollection(WorkspaceCollectionInfos dto) {
        WorkspaceCollectionEntity entity = WorkspaceMapper.toEntity(dto);
        return workspaceCollectionRepository.save(entity).getId();
    }

    @Transactional
    public void updateWorkspaceCollection(UUID id, WorkspaceCollectionInfos dto) {
        WorkspaceCollectionEntity entity = workspaceCollectionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + id));

        entity.getWorkspaces().clear();
        if (dto.workspaces() != null) {
            entity.getWorkspaces().addAll(dto.workspaces().stream()
                .map(WorkspaceMapper::toWorkspaceEntity)
                .toList());
        }
    }

    @Transactional
    public void deleteWorkspaceCollection(UUID id) {
        if (!workspaceCollectionRepository.existsById(id)) {
            throw new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + id);
        }
        workspaceCollectionRepository.deleteById(id);
    }

    @Transactional
    public UUID duplicateWorkspaceCollection(UUID id) {
        WorkspaceCollectionEntity source = workspaceCollectionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + id));

        WorkspaceCollectionInfos dto = WorkspaceMapper.toDto(source);
        // Clear all IDs to ensure new entities are created
        WorkspaceCollectionInfos dtoWithoutIds = new WorkspaceCollectionInfos(
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
                            p.zIndex(),
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
        WorkspaceCollectionEntity entity = WorkspaceMapper.toEntity(dtoWithoutIds);
        return workspaceCollectionRepository.save(entity).getId();
    }

    @Transactional(readOnly = true)
    public List<WorkspaceInfos> getWorkspaces(UUID collectionId) {
        WorkspaceCollectionEntity entity = workspaceCollectionRepository.findById(collectionId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + collectionId));
        return entity.getWorkspaces().stream()
            .map(WorkspaceMapper::toWorkspaceDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceInfos getWorkspace(UUID collectionId, UUID workspaceId) {
        WorkspaceCollectionEntity collection = workspaceCollectionRepository.findById(collectionId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + collectionId));

        WorkspaceEntity workspace = collection.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));

        return WorkspaceMapper.toWorkspaceDto(workspace);
    }

    @Transactional
    public void updateWorkspace(UUID collectionId, UUID workspaceId, WorkspaceInfos dto) {
        WorkspaceCollectionEntity collection = workspaceCollectionRepository.findById(collectionId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + collectionId));

        WorkspaceEntity workspace = collection.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));

        workspace.setName(dto.name());

        workspace.getPanels().clear();
        if (dto.panels() != null) {
            workspace.getPanels().addAll(dto.panels().stream()
                .map(WorkspaceMapper::toPanelEntity)
                .toList());
        }
    }

    @Transactional(readOnly = true)
    public List<PanelInfos> getPanels(UUID collectionId, UUID workspaceId, List<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(collectionId, workspaceId);
        return workspace.getPanels().stream()
            .filter(p -> panelIds == null || panelIds.isEmpty() || panelIds.contains(p.getId()))
            .map(WorkspaceMapper::toPanelDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public PanelInfos getPanel(UUID collectionId, UUID workspaceId, UUID panelId) {
        WorkspaceEntity workspace = findWorkspace(collectionId, workspaceId);
        PanelEntity panel = workspace.getPanels().stream()
            .filter(p -> p.getId().equals(panelId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(PANEL_NOT_FOUND + panelId));

        return WorkspaceMapper.toPanelDto(panel);
    }

    @Transactional
    public void createOrUpdatePanels(UUID collectionId, UUID workspaceId, List<PanelInfos> panels) {
        WorkspaceEntity workspace = findWorkspace(collectionId, workspaceId);

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
    public void deletePanels(UUID collectionId, UUID workspaceId, List<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(collectionId, workspaceId);
        workspace.getPanels().removeIf(p -> panelIds.contains(p.getId()));
    }

    private void updatePanelEntity(PanelEntity panel, PanelInfos dto) {
        panel.setType(dto.type());
        panel.setTitle(dto.title());
        panel.setPositionX(dto.position().x());
        panel.setPositionY(dto.position().y());
        panel.setSizeWidth(dto.size().width());
        panel.setSizeHeight(dto.size().height());
        panel.setZIndex(dto.zIndex());
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
                dto.zIndex(),
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

    public UUID createDefaultWorkspaceCollection() {
        try {
            WorkspaceCollectionInfos defaultCollection = readDefaultWorkspaceCollection();
            return createWorkspaceCollection(defaultCollection);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read default workspace collection", e);
        }
    }

    private WorkspaceCollectionInfos readDefaultWorkspaceCollection() throws IOException {
        try (InputStream inputStream = defaultWorkspaceCollectionResource.getInputStream()) {
            WorkspaceCollectionInfos collection = objectMapper.readValue(inputStream, WorkspaceCollectionInfos.class);
            // Generate UUIDs for panels that don't have them
            return new WorkspaceCollectionInfos(
                collection.id(),
                collection.workspaces().stream()
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
                                    panel.zIndex(),
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

    private WorkspaceEntity findWorkspace(UUID collectionId, UUID workspaceId) {
        WorkspaceCollectionEntity collection = workspaceCollectionRepository.findById(collectionId)
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_COLLECTION_NOT_FOUND + collectionId));

        return collection.getWorkspaces().stream()
            .filter(w -> w.getId().equals(workspaceId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(WORKSPACE_NOT_FOUND + workspaceId));
    }
}
