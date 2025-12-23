/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.workspace.PanelType;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspacesConfigEntity;
import org.gridsuite.studyconfig.server.mapper.WorkspaceMapper;
import org.gridsuite.studyconfig.server.repositories.WorkspacesConfigRepository;
import org.gridsuite.studyconfig.server.service.WorkspacesConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkspacesConfigControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WorkspacesConfigRepository workspacesConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspacesConfigService workspacesConfigService;

    @Test
    void testGetWorkspacesMetadata() throws Exception {
        WorkspacesConfigEntity config = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );

        MvcResult mockMvcResult = mockMvc.perform(get("/v1/workspaces-configs/{id}/workspaces", config.getId()))
            .andExpect(status().isOk())
            .andReturn();

        List<WorkspaceMetadata> result = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, WorkspaceMetadata.class)
        );
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Workspace 1");
        assertThat(result.get(1).name()).isEqualTo("Workspace 2");
    }

    @Test
    void testDeleteWorkspacesConfig() throws Exception {
        WorkspacesConfigEntity config = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );

        mockMvc.perform(delete("/v1/workspaces-configs/{id}", config.getId()))
            .andExpect(status().isNoContent());

        assertTrue(workspacesConfigRepository.findById(config.getId()).isEmpty());
    }

    @Test
    void testGetWorkspace() throws Exception {
        WorkspacesConfigEntity config = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = config.getWorkspaces().get(0).getId();

        MvcResult mockMvcResult = mockMvc.perform(get("/v1/workspaces-configs/{id}/workspaces/{workspaceId}", config.getId(), workspaceId))
            .andExpect(status().isOk())
            .andReturn();

        WorkspaceInfos result = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            WorkspaceInfos.class
        );
        assertThat(result.name()).isEqualTo("Workspace 1");
        assertThat(result.panels()).isEmpty();
    }

    @Test
    void testRenameWorkspace() throws Exception {
        WorkspacesConfigEntity config = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = config.getWorkspaces().get(0).getId();

        mockMvc.perform(put("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/name", config.getId(), workspaceId)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Renamed Workspace"))
            .andExpect(status().isNoContent());

        WorkspaceInfos workspace = workspacesConfigService.getWorkspace(config.getId(), workspaceId);
        assertThat(workspace.name()).isEqualTo("Renamed Workspace");
    }

    @Test
    void testDuplicateWorkspacesConfig() throws Exception {
        WorkspacesConfigEntity originalWorkspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );

        MvcResult mockMvcResult = mockMvc.perform(post("/v1/workspaces-configs")
                .param("duplicateFrom", originalWorkspacesConfig.getId().toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedWorkspacesConfigUuid = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            UUID.class
        );

        List<WorkspaceMetadata> duplicatedMetadata = workspacesConfigService.getWorkspacesMetadata(duplicatedWorkspacesConfigUuid);
        List<WorkspaceMetadata> originalMetadata = workspacesConfigService.getWorkspacesMetadata(originalWorkspacesConfig.getId());

        assertThat(duplicatedMetadata).hasSize(originalMetadata.size());
        assertThat(duplicatedWorkspacesConfigUuid).isNotEqualTo(originalWorkspacesConfig.getId());
    }

    @Test
    void testCreateDefaultWorkspacesConfig() throws Exception {
        MvcResult mockMvcResult = mockMvc.perform(post("/v1/workspaces-configs/default"))
            .andExpect(status().isCreated())
            .andReturn();

        UUID workspacesConfigUuid = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            UUID.class
        );

        List<WorkspaceMetadata> workspacesMetadata = workspacesConfigService.getWorkspacesMetadata(workspacesConfigUuid);
        assertThat(workspacesMetadata).hasSize(3);
        // First workspace should have Tree and Node Editor panels
        UUID firstWorkspaceId = workspacesMetadata.get(0).id();
        List<PanelInfos> firstWorkspacePanels = workspacesConfigService.getPanels(workspacesConfigUuid, firstWorkspaceId, List.of());
        assertThat(firstWorkspacePanels).hasSize(2);
    }

    @Test
    void testCreatePanel() throws Exception {
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();

        PanelInfos newPanel = createPanel("TREE");

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(newPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(newPanel.getId()));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.getType()).isEqualTo(PanelType.TREE);
    }

    @Test
    void testUpdatePanel() throws Exception {
        WorkspacesConfigInfos workspacesConfigInfos = createWorkspacesConfigWithPanel();
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(workspacesConfigInfos)
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();
        UUID panelId = workspacesConfig.getWorkspaces().get(0).getPanels().get(0).getId();

        PanelInfos updatedPanel = new PanelInfos();
        updatedPanel.setId(panelId);
        updatedPanel.setType(PanelType.TREE);
        updatedPanel.setTitle("Tree Panel");
        updatedPanel.setPosition(new PanelPositionInfos(0.5, 0.5));
        updatedPanel.setSize(new PanelSizeInfos(0.3, 0.3));
        updatedPanel.setOrderIndex(0);
        updatedPanel.setMinimized(true);
        updatedPanel.setMaximized(false);
        updatedPanel.setPinned(false);
        updatedPanel.setClosed(false);

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.getPosition().x()).isEqualTo(0.5);
        assertThat(panelToCheck.isMinimized()).isTrue();
    }

    @Test
    void testDeletePanel() throws Exception {
        WorkspacesConfigInfos workspacesConfigInfos = createWorkspacesConfigWithPanel();
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(workspacesConfigInfos)
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();
        UUID panelId = workspacesConfig.getWorkspaces().get(0).getPanels().get(0).getId();

        mockMvc.perform(delete("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelId))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, null);
        assertThat(panels).isEmpty();
    }

    @Test
    void testUpdatePanelWithRestorePositionAndSize() throws Exception {
        WorkspacesConfigInfos workspacesConfigInfos = createWorkspacesConfigWithPanel();
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(workspacesConfigInfos)
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();
        UUID panelId = workspacesConfig.getWorkspaces().get(0).getPanels().get(0).getId();

        PanelInfos updatedPanel = new PanelInfos();
        updatedPanel.setId(panelId);
        updatedPanel.setType(PanelType.TREE);
        updatedPanel.setTitle("Tree Panel");
        updatedPanel.setPosition(new PanelPositionInfos(0.5, 0.5));
        updatedPanel.setSize(new PanelSizeInfos(0.3, 0.3));
        updatedPanel.setOrderIndex(0);
        updatedPanel.setMinimized(false);
        updatedPanel.setMaximized(true);
        updatedPanel.setPinned(true);
        updatedPanel.setClosed(false);
        updatedPanel.setRestorePosition(new PanelPositionInfos(0.2, 0.2));
        updatedPanel.setRestoreSize(new PanelSizeInfos(0.6, 0.6));

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.getRestorePosition()).isNotNull();
        assertThat(panelToCheck.getRestorePosition().x()).isEqualTo(0.2);
        assertThat(panelToCheck.getRestoreSize()).isNotNull();
        assertThat(panelToCheck.getRestoreSize().width()).isEqualTo(0.6);
        assertThat(panelToCheck.isMaximized()).isTrue();
        assertThat(panelToCheck.isPinned()).isTrue();
    }

    // Metadata tests
    @Test
    void testPanelWithSLDMetadata() throws Exception {
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();

        SLDPanelInfos sldPanel = new SLDPanelInfos();
        sldPanel.setId(UUID.randomUUID());
        sldPanel.setType(PanelType.SLD_VOLTAGE_LEVEL);
        sldPanel.setTitle("SLD with Metadata");
        sldPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        sldPanel.setSize(new PanelSizeInfos(0.5, 1.0));
        sldPanel.setOrderIndex(0);
        sldPanel.setMinimized(false);
        sldPanel.setMaximized(false);
        sldPanel.setPinned(false);
        sldPanel.setClosed(false);
        sldPanel.setDiagramId("voltage-level-123");
        sldPanel.setParentNadPanelId(null);
        sldPanel.setNavigationHistory(List.of("diagram1", "diagram2", "diagram3"));

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(sldPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(sldPanel.getId()));
        PanelInfos retrievedPanel = panels.isEmpty() ? null : panels.get(0);
        assertThat(retrievedPanel).isNotNull();
        assertThat(retrievedPanel).isInstanceOf(SLDPanelInfos.class);

        SLDPanelInfos retrievedSldPanel = (SLDPanelInfos) retrievedPanel;
        assertThat(retrievedSldPanel.getDiagramId()).isEqualTo("voltage-level-123");
        assertThat(retrievedSldPanel.getNavigationHistory()).containsExactly("diagram1", "diagram2", "diagram3");
    }

    @Test
    void testPanelWithNADMetadata() throws Exception {
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();

        UUID nadConfigUuid = UUID.randomUUID();
        UUID filterUuid = UUID.randomUUID();
        UUID currentFilterUuid = UUID.randomUUID();
        UUID savedWorkspaceConfigUuid = UUID.randomUUID();

        NADPanelInfos nadPanel = new NADPanelInfos();
        nadPanel.setId(UUID.randomUUID());
        nadPanel.setType(PanelType.NAD);
        nadPanel.setTitle("NAD with Metadata");
        nadPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        nadPanel.setSize(new PanelSizeInfos(0.5, 1.0));
        nadPanel.setOrderIndex(0);
        nadPanel.setMinimized(false);
        nadPanel.setMaximized(false);
        nadPanel.setPinned(false);
        nadPanel.setClosed(false);
        nadPanel.setNadConfigUuid(nadConfigUuid);
        nadPanel.setFilterUuid(filterUuid);
        nadPanel.setCurrentFilterUuid(currentFilterUuid);
        nadPanel.setSavedWorkspaceConfigUuid(savedWorkspaceConfigUuid);
        nadPanel.setVoltageLevelToOmitIds(List.of("voltage1", "voltage2"));
        nadPanel.setNavigationHistory(List.of("nav1", "nav2", "nav3"));

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(nadPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(nadPanel.getId()));
        PanelInfos retrievedPanel = panels.isEmpty() ? null : panels.get(0);
        assertThat(retrievedPanel).isNotNull();
        assertThat(retrievedPanel).isInstanceOf(NADPanelInfos.class);

        NADPanelInfos retrievedNadPanel = (NADPanelInfos) retrievedPanel;
        assertThat(retrievedNadPanel.getNadConfigUuid()).isEqualTo(nadConfigUuid);
        assertThat(retrievedNadPanel.getFilterUuid()).isEqualTo(filterUuid);
        assertThat(retrievedNadPanel.getCurrentFilterUuid()).isEqualTo(currentFilterUuid);
        assertThat(retrievedNadPanel.getSavedWorkspaceConfigUuid()).isEqualTo(savedWorkspaceConfigUuid);
        assertThat(retrievedNadPanel.getVoltageLevelToOmitIds()).containsExactly("voltage1", "voltage2");
        assertThat(retrievedNadPanel.getNavigationHistory()).containsExactly("nav1", "nav2", "nav3");
    }

    // Helper methods
    private WorkspacesConfigInfos createWorkspacesConfig() {
        return new WorkspacesConfigInfos(
            null,
            List.of(
                createWorkspace("Workspace 1"),
                createWorkspace("Workspace 2")
            )
        );
    }

    private WorkspacesConfigInfos createWorkspacesConfigWithPanel() {
        PanelInfos panel = createPanel("TREE");
        WorkspaceInfos workspace = new WorkspaceInfos(
            null,
            "Workspace with Panel",
            List.of(panel)
        );
        return new WorkspacesConfigInfos(null, List.of(workspace));
    }

    private WorkspaceInfos createWorkspace(String name) {
        return new WorkspaceInfos(
            null,
            name,
            new ArrayList<>()
        );
    }

    private PanelInfos createPanel(String type) {
        PanelType panelType = PanelType.valueOf(type);
        PanelInfos panel = new PanelInfos();
        panel.setId(UUID.randomUUID());
        panel.setType(panelType);
        panel.setTitle(type + " Panel");
        panel.setPosition(new PanelPositionInfos(0.0, 0.0));
        panel.setSize(new PanelSizeInfos(0.5, 1.0));
        panel.setOrderIndex(0);
        panel.setMinimized(false);
        panel.setMaximized(false);
        panel.setPinned(false);
        panel.setClosed(false);
        return panel;
    }
}
