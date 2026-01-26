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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkspacesConfigControllerTest {

    private static final String WORKSPACE_1 = "Workspace 1";
    private static final String WORKSPACE_2 = "Workspace 2";
    private static final String DUPLICATE_FROM_PARAM = "duplicateFrom";
    private static final String PANEL_1 = "Panel 1";
    private static final String PANEL_2 = "Panel 2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkspacesConfigRepository workspacesConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspacesConfigService workspacesConfigService;

    @MockitoBean
    private SingleLineDiagramService singleLineDiagramService;

    @AfterEach
    void tearDown() {
        workspacesConfigRepository.deleteAll();
    }

    private String getWorkspacesConfigBasePath() {
        return "/" + StudyConfigApi.API_VERSION + "/workspaces-configs";
    }

    private String getPanelsPath() {
        return getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}/panels";
    }

    private String getNadConfigPath() {
        return getPanelsPath() + "/{panelId}/current-nad-config";
    }

    @Test
    void testCreateDefaultWorkspacesConfig() throws Exception {
        MvcResult result = mockMvc.perform(post(getWorkspacesConfigBasePath() + "/default"))
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

        MvcResult result = mockMvc.perform(get(getWorkspacesConfigBasePath() + "/{id}/workspaces", configId))
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

        mockMvc.perform(delete(getWorkspacesConfigBasePath() + "/{id}", configId))
            .andExpect(status().isNoContent());

        assertTrue(workspacesConfigRepository.findById(configId).isEmpty());
    }

    @Test
    void testGetWorkspace() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        MvcResult result = mockMvc.perform(get(getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}", configId, workspaceId))
            .andExpect(status().isOk())
            .andReturn();

        WorkspaceInfos workspace = objectMapper.readValue(result.getResponse().getContentAsString(), WorkspaceInfos.class);
        assertThat(workspace.name()).isEqualTo(WORKSPACE_1);
    }

    @Test
    void testRenameWorkspace() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        mockMvc.perform(put(getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}/name", configId, workspaceId)
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

        MvcResult result = mockMvc.perform(post(getWorkspacesConfigBasePath())
                .param(DUPLICATE_FROM_PARAM, originalConfigId.toString()))
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

        mockMvc.perform(post(getPanelsPath(), configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel))))
            .andExpect(status().isOk());

        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceId, Set.of(panel.getId()));
        assertThat(panels).hasSize(1);
        assertThat(panels.get(0).getType()).isEqualTo(PanelType.TREE);

        panel.setTitle("Updated Title");
        mockMvc.perform(post(getPanelsPath(), configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel))))
            .andExpect(status().isOk());

        panels = workspacesConfigService.getPanels(configId, workspaceId, Set.of(panel.getId()));
        assertThat(panels.get(0).getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void testDeletePanels() throws Exception {
        UUID configId = createConfigWithPanel();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();
        UUID panelId = workspacesConfigService.getPanels(configId, workspaceId, null).get(0).getId();

        mockMvc.perform(delete(getPanelsPath(), configId, workspaceId)
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

        MvcResult result = mockMvc.perform(post(getWorkspacesConfigBasePath())
                .param(DUPLICATE_FROM_PARAM, configId.toString()))
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
        assertThat(nadPanel.getCurrentNadConfigUuid()).isEqualTo(nadConfigId);
    }

    @Test
    void testGetPanelsEndpoint() throws Exception {
        UUID configId = createConfigWithPanel();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        MvcResult result = mockMvc.perform(get(getPanelsPath(), configId, workspaceId))
            .andExpect(status().isOk())
            .andReturn();

        List<PanelInfos> panels = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, PanelInfos.class)
        );

        assertThat(panels).hasSize(1);
        assertThat(panels.get(0).getType()).isEqualTo(PanelType.TREE);
    }

    @Test
    void testGetPanelsWithIds() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        PanelInfos panel1 = createPanel(PanelType.TREE, PANEL_1);
        PanelInfos panel2 = createPanel(PanelType.SPREADSHEET, PANEL_2);

        workspacesConfigService.createOrUpdatePanels(configId, workspaceId, List.of(panel1, panel2));

        MvcResult result = mockMvc.perform(get(getPanelsPath(), configId, workspaceId)
                .param("panelIds", panel1.getId().toString()))
            .andExpect(status().isOk())
            .andReturn();

        List<PanelInfos> panels = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, PanelInfos.class)
        );

        assertThat(panels).hasSize(1);
        assertThat(panels.get(0).getId()).isEqualTo(panel1.getId());
    }

    @Test
    void testDeletePanelsWithNadConfig() throws Exception {
        UUID configId = createConfigWithNadAndSld();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceId, null);

        UUID nadPanelId = panels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow()
            .getId();

        mockMvc.perform(delete(getPanelsPath(), configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(nadPanelId))))
            .andExpect(status().isNoContent());

        verify(singleLineDiagramService).deleteNadConfigs(anyList());
    }

    @Test
    void testSaveNadConfig() throws Exception {
        UUID configId = createConfigWithNadAndSld();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceId, null);

        UUID nadPanelId = panels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow()
            .getId();

        UUID newNadConfigId = UUID.randomUUID();
        when(singleLineDiagramService.createOrUpdateNadConfig(any())).thenReturn(newNadConfigId);

        MvcResult result = mockMvc.perform(post(getNadConfigPath(),
                configId, workspaceId, nadPanelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"someKey\":\"someValue\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        UUID returnedId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        assertThat(returnedId).isEqualTo(newNadConfigId);

        NADPanelInfos updatedPanel = (NADPanelInfos) workspacesConfigService.getPanels(configId, workspaceId, Set.of(nadPanelId)).get(0);
        assertThat(updatedPanel.getCurrentNadConfigUuid()).isEqualTo(newNadConfigId);
    }

    @Test
    void testDeleteNadConfig() throws Exception {
        UUID configId = createConfigWithNadAndSld();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceId, null);

        NADPanelInfos nadPanel = (NADPanelInfos) panels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow();

        mockMvc.perform(delete(getNadConfigPath(),
                configId, workspaceId, nadPanel.getId()))
            .andExpect(status().isNoContent());

        verify(singleLineDiagramService).deleteNadConfig(any());

        NADPanelInfos updatedPanel = (NADPanelInfos) workspacesConfigService.getPanels(configId, workspaceId, Set.of(nadPanel.getId())).get(0);
        assertThat(updatedPanel.getCurrentNadConfigUuid()).isNull();
    }

    @Test
    void testGetWorkspacesConfigNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get(getWorkspacesConfigBasePath() + "/{id}/workspaces", nonExistentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetWorkspaceNotFound() throws Exception {
        UUID configId = createConfig();
        UUID nonExistentWorkspaceId = UUID.randomUUID();

        mockMvc.perform(get(getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}", configId, nonExistentWorkspaceId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteWorkspacesConfigNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete(getWorkspacesConfigBasePath() + "/{id}", nonExistentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testRenameWorkspaceNotFound() throws Exception {
        UUID configId = createConfig();
        UUID nonExistentWorkspaceId = UUID.randomUUID();

        mockMvc.perform(put(getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}/name", configId, nonExistentWorkspaceId)
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Name"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDuplicateWorkspacesConfigNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(post(getWorkspacesConfigBasePath())
                .param(DUPLICATE_FROM_PARAM, nonExistentId.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testSaveNadConfigOnNonNadPanel() throws Exception {
        UUID configId = createConfigWithPanel();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();
        UUID treePanelId = workspacesConfigService.getPanels(configId, workspaceId, null).get(0).getId();

        mockMvc.perform(post(getNadConfigPath(),
                configId, workspaceId, treePanelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteNadConfigWhenNoneExists() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        NADPanelInfos nadPanel = new NADPanelInfos();
        nadPanel.setId(UUID.randomUUID());
        nadPanel.setType(PanelType.NAD);
        nadPanel.setTitle("NAD without config");
        nadPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        nadPanel.setSize(new PanelSizeInfos(1.0, 1.0));

        workspacesConfigService.createOrUpdatePanels(configId, workspaceId, List.of(nadPanel));

        mockMvc.perform(delete(getNadConfigPath(),
                configId, workspaceId, nadPanel.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateMultiplePanelsAtOnce() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        PanelInfos panel1 = createPanel(PanelType.TREE, PANEL_1);
        PanelInfos panel2 = createPanel(PanelType.SPREADSHEET, PANEL_2);
        PanelInfos panel3 = createPanel(PanelType.NAD, "Panel 3");

        mockMvc.perform(post(getPanelsPath(), configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel1, panel2, panel3))))
            .andExpect(status().isOk());

        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceId, null);
        assertThat(panels).hasSize(3);
    }

    @Test
    void testDeleteMultiplePanelsAtOnce() throws Exception {
        UUID configId = createConfig();
        UUID workspaceId = workspacesConfigService.getWorkspacesMetadata(configId).get(0).id();

        PanelInfos panel1 = createPanel(PanelType.TREE, PANEL_1);
        PanelInfos panel2 = createPanel(PanelType.SPREADSHEET, PANEL_2);

        workspacesConfigService.createOrUpdatePanels(configId, workspaceId, List.of(panel1, panel2));

        mockMvc.perform(delete(getPanelsPath(), configId, workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel1.getId(), panel2.getId()))))
            .andExpect(status().isNoContent());

        assertThat(workspacesConfigService.getPanels(configId, workspaceId, null)).isEmpty();
    }

    @Test
    void testDuplicateWithoutNadPanels() throws Exception {
        UUID configId = createConfigWithPanel();

        MvcResult result = mockMvc.perform(post(getWorkspacesConfigBasePath())
                .param(DUPLICATE_FROM_PARAM, configId.toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedConfigId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);

        assertThat(duplicatedConfigId).isNotEqualTo(configId);
        verify(singleLineDiagramService, never()).duplicateNadConfig(any());
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
        nadPanel.setCurrentNadConfigUuid(UUID.randomUUID());

        SLDPanelInfos sldPanel = new SLDPanelInfos();
        sldPanel.setId(UUID.randomUUID());
        sldPanel.setType(PanelType.SLD_VOLTAGE_LEVEL);
        sldPanel.setTitle("SLD");
        sldPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        sldPanel.setSize(new PanelSizeInfos(1.0, 1.0));
        sldPanel.setEquipmentId("vl1");
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
