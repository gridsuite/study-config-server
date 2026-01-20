/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.workspace.*;
import org.gridsuite.studyconfig.server.repositories.WorkspacesConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkspacesConfigService {

    private final WorkspacesConfigRepository workspacesConfigRepository;
    private final SingleLineDiagramService singleLineDiagramService;
    private final ObjectMapper objectMapper;

    @Value("classpath:default-workspaces-config.json")
    private Resource defaultWorkspacesConfigResource;

    private static final String WORKSPACES_CONFIG_NOT_FOUND = "WorkspacesConfig not found with id: ";
    private static final String WORKSPACE_NOT_FOUND = "Workspace not found with id: ";

    @Transactional
    public void deleteWorkspacesConfig(UUID id) {
        WorkspacesConfigEntity entity = findWorkspacesConfig(id);
        List<UUID> nadConfigUuids = entity.getWorkspaces().stream()
            .flatMap(workspace -> getNadConfigUuids(workspace).stream())
            .toList();
        workspacesConfigRepository.delete(entity);
        deleteNadConfigs(nadConfigUuids);
    }

    @Transactional
    public UUID duplicateWorkspacesConfig(UUID id) {
        WorkspacesConfigEntity duplicated = findWorkspacesConfig(id).duplicate();
        duplicated.getWorkspaces().forEach(this::duplicateNadConfigs);
        return workspacesConfigRepository.save(duplicated).getId();
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMetadata> getWorkspacesMetadata(UUID configId) {
        WorkspacesConfigEntity entity = findWorkspacesConfig(configId);
        return entity.getWorkspaces().stream()
            .map(WorkspaceEntity::toMetadata)
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
    public List<PanelInfos> getPanels(UUID configId, UUID workspaceId, Set<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        return workspace.getPanels().stream()
            .filter(p -> panelIds == null || panelIds.contains(p.getId()))
            .map(PanelEntity::toDto)
            .toList();
    }

    @Transactional
    public void createOrUpdatePanels(UUID configId, UUID workspaceId, List<PanelInfos> panels) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        panels.forEach(panelDto ->
            workspace.getPanel(panelDto.getId())
                .ifPresentOrElse(
                    panel -> panel.update(panelDto),
                    () -> workspace.getPanels().add(PanelEntity.toEntity(panelDto))
                )
        );
    }

    @Transactional
    public void deletePanels(UUID configId, UUID workspaceId, Set<UUID> panelIds) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        boolean deleteAll = panelIds == null || panelIds.isEmpty();

        List<UUID> currrentNadConfigUuids = workspace.getNadPanels().stream()
            .filter(nadPanel -> deleteAll || panelIds.contains(nadPanel.getId()))
            .map(NADPanelEntity::getCurrentNadConfigUuid)
            .filter(Objects::nonNull)
            .toList();

        deleteNadConfigs(currrentNadConfigUuids);

        if (deleteAll) {
            workspace.getPanels().clear();
        } else {
            workspace.getPanels().removeIf(p -> panelIds.contains(p.getId()));
        }
    }

    @Transactional
    public UUID createDefaultWorkspacesConfig() {
        try (InputStream inputStream = defaultWorkspacesConfigResource.getInputStream()) {
            WorkspacesConfigInfos defaultConfig = objectMapper.readValue(inputStream, WorkspacesConfigInfos.class);
            return workspacesConfigRepository.save(new WorkspacesConfigEntity(defaultConfig)).getId();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read default workspaces config", e);
        }
    }

    @Transactional
    public UUID saveNadConfig(UUID configId, UUID workspaceId, UUID panelId, Map<String, Object> nadConfigData) {
        UUID nadConfigUuid = singleLineDiagramService.createOrUpdateNadConfig(nadConfigData);
        NADPanelEntity nadPanel = findNadPanel(configId, workspaceId, panelId);
        nadPanel.setCurrentNadConfigUuid(nadConfigUuid);
        return nadConfigUuid;
    }

    @Transactional
    public void deleteNadConfig(UUID configId, UUID workspaceId, UUID panelId) {
        NADPanelEntity nadPanel = findNadPanel(configId, workspaceId, panelId);
        UUID nadConfigUuid = nadPanel.getCurrentNadConfigUuid();
        if (nadConfigUuid == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No NAD config found for panel: " + panelId);
        }
        singleLineDiagramService.deleteNadConfig(nadConfigUuid);
        nadPanel.setCurrentNadConfigUuid(null);
    }

    private WorkspacesConfigEntity findWorkspacesConfig(UUID configId) {
        return workspacesConfigRepository.findById(configId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, WORKSPACES_CONFIG_NOT_FOUND + configId));
    }

    private WorkspaceEntity findWorkspace(UUID configId, UUID workspaceId) {
        WorkspacesConfigEntity config = findWorkspacesConfig(configId);
        return config.getWorkspace(workspaceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, WORKSPACE_NOT_FOUND + workspaceId));
    }

    private NADPanelEntity findNadPanel(UUID configId, UUID workspaceId, UUID panelId) {
        WorkspaceEntity workspace = findWorkspace(configId, workspaceId);
        PanelEntity panel = workspace.getPanel(panelId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Panel not found: " + panelId));
        if (!panel.isNad()) {
            throw new IllegalArgumentException("Panel is not a NAD panel: " + panelId);
        }
        return (NADPanelEntity) panel;
    }

    private void duplicateNadConfigs(WorkspaceEntity workspace) {
        workspace.getNadPanels().forEach(nadPanel -> {
            UUID nadConfigUuid = nadPanel.getCurrentNadConfigUuid();
            if (nadConfigUuid != null) {
                UUID newConfigUuid = singleLineDiagramService.duplicateNadConfig(nadConfigUuid);
                nadPanel.setCurrentNadConfigUuid(newConfigUuid);
            }
        });
    }

    private List<UUID> getNadConfigUuids(WorkspaceEntity workspace) {
        return workspace.getNadPanels().stream()
            .map(NADPanelEntity::getCurrentNadConfigUuid)
            .filter(Objects::nonNull)
            .toList();
    }

    private void deleteNadConfigs(List<UUID> nadConfigUuids) {
        if (!nadConfigUuids.isEmpty()) {
            singleLineDiagramService.deleteNadConfigs(nadConfigUuids);
        }
    }
}
