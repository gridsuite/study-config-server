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
import org.gridsuite.studyconfig.server.repositories.WorkspacesConfigRepository;
import org.gridsuite.studyconfig.server.service.SingleLineDiagramService;
import org.gridsuite.studyconfig.server.service.WorkspacesConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkspacesConfigControllerTest {

    private static final String WORKSPACE_1 = "Workspace 1";
    private static final String WORKSPACE_2 = "Workspace 2";
    private static final String PANELS_PATH = "/v1/workspaces-configs/{id}/workspaces/{workspaceId}/panels";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspacesConfigRepository workspacesConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspacesConfigService workspacesConfigService;

    @MockBean
    private SingleLineDiagramService singleLineDiagramService;

    @AfterEach
    void tearDown() {
        workspacesConfigRepository.deleteAll();
    }

    @Test
    void testCreateDefaultWorkspacesConfig() throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/workspaces-configs/default"))
            .andExpect(status().isCreated())
            .andReturn();

        UUID configId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        List<WorkspaceMetadata> metadata = workspacesConfigService.getWorkspacesMetadata(configId);

        assertThat(metadata).hasSize(3);
        assertThat(workspacesConfigService.getPanels(configId, metadata.get(0).id(), null)).hasSize(2);
    }

    @Test
    void testGetWorkspacesMetadata() throws Exception {
        UUID configId = createConfig();

        MvcResult result = mockMvc.perform(get("/v1/workspaces-configs/{id}/workspaces", configId))
            .andExpect(status().isOk())
            .andReturn();

        List<WorkspaceMetadata> metadata = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, WorkspaceMetadata.class)
        );

        assertThat(metadata).hasSize(2);
        assertThat(metadata.get(0).name()).isEqualTo(WORKSPACE_1);
        assertThat(metadata.get(1).name()).isEqualTo(WORKSPACE_2);
    }

    @Test
    void testDeleteWorkspacesConfig() throws Exception {
        UUID configId = createConfig();

        mockMvc.perform(delete("/v1/workspaces-configs/{id}", configId))
            .andExpect(status().isNoContent());

        assertTrue(workspacesConfigRepository.findById(configId).isEmpty());
    }

    @Test
    void testGetWorkspace() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        MvcResult result = mockMvc.perform(get("/v1/workspaces-configs/{id}/workspaces/{workspaceId}", configId, workspaceId))
            .andExpect(status().isOk())
            .andReturn();

        WorkspaceInfos workspace = objectMapper.readValue(result.getResponse().getContentAsString(), WorkspaceInfos.class);
        assertThat(workspace.name()).isEqualTo(WORKSPACE_1);
    }

    @Test
    void testRenameWorkspace() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        mockMvc.perform(put("/v1/workspaces-configs/{id}/workspaces/{workspaceId}/name", configId, workspaceId)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Renamed"))
            .andExpect(status().isNoContent());

        assertThat(workspacesConfigService.getWorkspace(configId, workspaceId).name()).isEqualTo("Renamed");
    }

    @Test
    void testDuplicateWorkspacesConfig() throws Exception {
        UUID originalConfigId = createConfigWithNadAndSld();
        UUID nadConfigId = UUID.randomUUID();

        when(singleLineDiagramService.duplicateNadConfig(any())).thenReturn(nadConfigId);

        MvcResult result = mockMvc.perform(post("/v1/workspaces-configs")
                .param("duplicateFrom", originalConfigId.toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedConfigId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);

        assertThat(duplicatedConfigId).isNotEqualTo(originalConfigId);
        assertThat(workspacesConfigService.getWorkspacesMetadata(duplicatedConfigId)).hasSize(1);
    }

    @Test
    void testCreateAndUpdatePanels() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        PanelInfos panel = createPanel(PanelType.TREE, "Tree Panel");

        mockMvc.perform(post(PANELS_PATH, configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceId, Set.of(panel.getId()));
        assertThat(panels).hasSize(1);
        assertThat(panels.get(0).getType()).isEqualTo(PanelType.TREE);

        panel.setTitle("Updated Title");
        mockMvc.perform(post(PANELS_PATH, configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel))))
            .andExpect(status().isNoContent());

        panels = workspacesConfigService.getPanels(configId, workspaceId, Set.of(panel.getId()));
        assertThat(panels.get(0).getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void testDeletePanels() throws Exception {
        UUID configId = createConfigWithPanel();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();
        UUID panelId = workspacesConfigService.getPanels(configId, workspaceId, null).get(0).getId();

        mockMvc.perform(delete(PANELS_PATH, configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelId))))
            .andExpect(status().isNoContent());

        assertThat(workspacesConfigService.getPanels(configId, workspaceId, null)).isEmpty();
    }

    @Test
    void testSldParentRemapping() throws Exception {
        UUID configId = createConfigWithNadAndSld();
        UUID nadConfigId = UUID.randomUUID();

        when(singleLineDiagramService.duplicateNadConfig(any())).thenReturn(nadConfigId);

        MvcResult result = mockMvc.perform(post("/v1/workspaces-configs")
                .param("duplicateFrom", configId.toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedConfigId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        UUID duplicatedWorkspaceId = workspacesConfigService.getWorkspacesMetadata(duplicatedConfigId).get(0).id();
        List<PanelInfos> duplicatedPanels = workspacesConfigService.getPanels(duplicatedConfigId, duplicatedWorkspaceId, null);

        NADPanelInfos nadPanel = (NADPanelInfos) duplicatedPanels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow();

        SLDPanelInfos sldPanel = (SLDPanelInfos) duplicatedPanels.stream()
            .filter(p -> p.getType() == PanelType.SLD_VOLTAGE_LEVEL)
            .findFirst().orElseThrow();

        assertThat(sldPanel.getParentNadPanelId()).isEqualTo(nadPanel.getId());
        assertThat(nadPanel.getSavedWorkspaceConfigUuid()).isEqualTo(nadConfigId);
    }

    private UUID createConfig() {
        return workspacesConfigRepository.save(new WorkspacesConfigEntity(
            new WorkspacesConfigInfos(null, List.of(
                new WorkspaceInfos(null, WORKSPACE_1, List.of()),
                new WorkspaceInfos(null, WORKSPACE_2, List.of())
            ))
        )).getId();
    }

    private UUID createConfigWithPanel() {
        return workspacesConfigRepository.save(new WorkspacesConfigEntity(
            new WorkspacesConfigInfos(null, List.of(
                new WorkspaceInfos(null, "Workspace", List.of(createPanel(PanelType.TREE, "Tree")))
            ))
        )).getId();
    }

    private UUID createConfigWithNadAndSld() {
        NADPanelInfos nadPanel = new NADPanelInfos();
        nadPanel.setId(UUID.randomUUID());
        nadPanel.setType(PanelType.NAD);
        nadPanel.setTitle("NAD");
        nadPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        nadPanel.setSize(new PanelSizeInfos(1.0, 1.0));
        nadPanel.setSavedWorkspaceConfigUuid(UUID.randomUUID());

        SLDPanelInfos sldPanel = new SLDPanelInfos();
        sldPanel.setId(UUID.randomUUID());
        sldPanel.setType(PanelType.SLD_VOLTAGE_LEVEL);
        sldPanel.setTitle("SLD");
        sldPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        sldPanel.setSize(new PanelSizeInfos(1.0, 1.0));
        sldPanel.setDiagramId("vl1");
        sldPanel.setParentNadPanelId(nadPanel.getId());

        return workspacesConfigRepository.save(new WorkspacesConfigEntity(
            new WorkspacesConfigInfos(null, List.of(
                new WorkspaceInfos(null, "Workspace", List.of(nadPanel, sldPanel))
            ))
        )).getId();
    }

    private PanelInfos createPanel(PanelType type, String title) {
        PanelInfos panel = new PanelInfos();
        panel.setId(UUID.randomUUID());
        panel.setType(type);
        panel.setTitle(title);
        panel.setPosition(new PanelPositionInfos(0.0, 0.0));
        panel.setSize(new PanelSizeInfos(0.5, 1.0));
        panel.setMinimized(false);
        panel.setMaximized(false);
        panel.setPinned(false);
        return panel;
    }
}
