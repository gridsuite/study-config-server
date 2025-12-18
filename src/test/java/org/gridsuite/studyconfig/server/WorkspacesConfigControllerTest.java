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

        MvcResult mockMvcResult = mockMvc.perform(get("/v1/workspaces-configs/{id}/workspaces/metadata", config.getId()))
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
        UUID panelId = newPanel.id() != null ? newPanel.id() : UUID.randomUUID();
        PanelInfos panelWithId = new PanelInfos(panelId, newPanel.type(), newPanel.title(), newPanel.position(),
            newPanel.size(), newPanel.orderIndex(), newPanel.isMinimized(), newPanel.isMaximized(),
            newPanel.isPinned(), newPanel.isClosed(), newPanel.restorePosition(), newPanel.restoreSize(), newPanel.metadata());

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelWithId))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.type()).isEqualTo(PanelType.TREE);
    }

    @Test
    void testUpdatePanel() throws Exception {
        WorkspacesConfigInfos workspacesConfigInfos = createWorkspacesConfigWithPanel();
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(workspacesConfigInfos)
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();
        UUID panelId = workspacesConfig.getWorkspaces().get(0).getPanels().get(0).getId();

        PanelInfos updatedPanel = new PanelInfos(
            panelId,
            PanelType.TREE,
            "Tree Panel",
            new PanelPositionInfos(0.5, 0.5),
            new PanelSizeInfos(0.3, 0.3),
            0,
            true,
            false,
            false,
            false,
            null,
            null,
            null
        );

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.position().x()).isEqualTo(0.5);
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

        PanelInfos updatedPanel = new PanelInfos(
            panelId,
            PanelType.TREE,
            "Tree Panel",
            new PanelPositionInfos(0.5, 0.5),
            new PanelSizeInfos(0.3, 0.3),

            0,
            false,
            true,
            true,
            false,
            new PanelPositionInfos(0.2, 0.2),
            new PanelSizeInfos(0.6, 0.6),
            null
        );

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.restorePosition()).isNotNull();
        assertThat(panelToCheck.restorePosition().x()).isEqualTo(0.2);
        assertThat(panelToCheck.restoreSize()).isNotNull();
        assertThat(panelToCheck.restoreSize().width()).isEqualTo(0.6);
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

        SLDPanelMetadataInfos sldMetadata = new SLDPanelMetadataInfos(
            "voltage-level-123",
            null,
            List.of("diagram1", "diagram2", "diagram3")
        );

        PanelInfos sldPanel = new PanelInfos(
            null,
            PanelType.SLD_VOLTAGE_LEVEL,
            "SLD with Metadata",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            0,
            false,
            false,
            false,
            false,
            null,
            null,
            sldMetadata
        );
        UUID panelId = UUID.randomUUID();
        PanelInfos sldPanelWithId = new PanelInfos(panelId, sldPanel.type(), sldPanel.title(), sldPanel.position(),
            sldPanel.size(), sldPanel.orderIndex(), sldPanel.isMinimized(), sldPanel.isMaximized(),
            sldPanel.isPinned(), sldPanel.isClosed(), sldPanel.restorePosition(), sldPanel.restoreSize(), sldPanel.metadata());

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(sldPanelWithId))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos retrievedPanel = panels.isEmpty() ? null : panels.get(0);
        assertThat(retrievedPanel.metadata()).isNotNull();
        assertThat(retrievedPanel.metadata()).isInstanceOf(SLDPanelMetadataInfos.class);

        SLDPanelMetadataInfos retrievedMetadata = (SLDPanelMetadataInfos) retrievedPanel.metadata();
        assertThat(retrievedMetadata.diagramId()).isEqualTo("voltage-level-123");
        assertThat(retrievedMetadata.sldNavigationHistory()).containsExactlyElementsOf(sldMetadata.sldNavigationHistory());
    }

    @Test
    void testPanelWithNADMetadata() throws Exception {
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();

        NADPanelMetadataInfos nadMetadata = new NADPanelMetadataInfos(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of("voltage1", "voltage2"),
            List.of("nav1", "nav2", "nav3")
        );

        PanelInfos nadPanel = new PanelInfos(
            null,
            PanelType.NAD,
            "NAD with Metadata",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            0,
            false,
            false,
            false,
            false,
            null,
            null,
            nadMetadata
        );
        UUID panelId = UUID.randomUUID();
        PanelInfos nadPanelWithId = new PanelInfos(panelId, nadPanel.type(), nadPanel.title(), nadPanel.position(),
            nadPanel.size(), nadPanel.orderIndex(), nadPanel.isMinimized(), nadPanel.isMaximized(),
            nadPanel.isPinned(), nadPanel.isClosed(), nadPanel.restorePosition(), nadPanel.restoreSize(), nadPanel.metadata());

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(nadPanelWithId))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos retrievedPanel = panels.isEmpty() ? null : panels.get(0);
        assertThat(retrievedPanel.metadata()).isNotNull();
        assertThat(retrievedPanel.metadata()).isInstanceOf(NADPanelMetadataInfos.class);

        NADPanelMetadataInfos retrievedMetadata = (NADPanelMetadataInfos) retrievedPanel.metadata();
        assertThat(retrievedMetadata.nadConfigUuid()).isEqualTo(nadMetadata.nadConfigUuid());
        assertThat(retrievedMetadata.filterUuid()).isEqualTo(nadMetadata.filterUuid());
        assertThat(retrievedMetadata.currentFilterUuid()).isEqualTo(nadMetadata.currentFilterUuid());
        assertThat(retrievedMetadata.savedWorkspaceConfigUuid()).isEqualTo(nadMetadata.savedWorkspaceConfigUuid());
        assertThat(retrievedMetadata.voltageLevelToOmitIds()).containsExactlyElementsOf(nadMetadata.voltageLevelToOmitIds());
        assertThat(retrievedMetadata.nadNavigationHistory()).containsExactlyElementsOf(nadMetadata.nadNavigationHistory());
    }

    @Test
    void testRemovePanelMetadata() throws Exception {
        // Create a panel with metadata
        WorkspacesConfigEntity workspacesConfig = workspacesConfigRepository.save(
            WorkspaceMapper.toEntity(createWorkspacesConfig())
        );
        UUID workspaceId = workspacesConfig.getWorkspaces().get(0).getId();

        SLDPanelMetadataInfos sldMetadata = new SLDPanelMetadataInfos("diagram-id", null, List.of("nav1"));
        PanelInfos panelWithMetadata = new PanelInfos(
            null,
            PanelType.SLD_SUBSTATION,
            "SLD Panel",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            0,
            false,
            false,
            false,
            false,
            null,
            null,
            sldMetadata
        );
        UUID panelId = UUID.randomUUID();
        PanelInfos panelWithId = new PanelInfos(panelId, panelWithMetadata.type(), panelWithMetadata.title(), panelWithMetadata.position(),
            panelWithMetadata.size(), panelWithMetadata.orderIndex(), panelWithMetadata.isMinimized(), panelWithMetadata.isMaximized(),
            panelWithMetadata.isPinned(), panelWithMetadata.isClosed(), panelWithMetadata.restorePosition(),
            panelWithMetadata.restoreSize(), panelWithMetadata.metadata());

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelWithId))))
            .andExpect(status().isNoContent());

        // Update to remove metadata
        PanelInfos updatedPanel = new PanelInfos(
            panelId,
            PanelType.TREE,
            "Panel without Metadata",
            new PanelPositionInfos(0.5, 0.5),
            new PanelSizeInfos(0.3, 0.3),
            2,

            false,
            false,
            false,
            false,
            null,
            null,
            null
        );

        mockMvc.perform(post("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels",
                workspacesConfig.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(workspacesConfig.getId(), workspaceId, List.of(panelId));
        PanelInfos panelToCheck = panels.isEmpty() ? null : panels.get(0);
        assertThat(panelToCheck.metadata()).isNull();
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
        return new PanelInfos(
            UUID.randomUUID(),
            panelType,
            type + " Panel",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            0,
            false,
            false,
            false,
            false,
            null,
            null,
            null
        );
    }
}
