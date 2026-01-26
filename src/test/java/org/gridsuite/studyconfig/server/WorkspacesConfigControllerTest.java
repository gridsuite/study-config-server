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
import org.gridsuite.studyconfig.server.service.SingleLineDiagramService;
import org.gridsuite.studyconfig.server.service.WorkspacesConfigService;
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
class WorkspacesConfigControllerTest extends AbstractWorkspaceTestBase {

    private static final String DUPLICATE_FROM_PARAM = "duplicateFrom";
    private static final String PANEL_1 = "Panel 1";
    private static final String PANEL_2 = "Panel 2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspacesConfigService workspacesConfigService;

    @MockitoBean
    private SingleLineDiagramService singleLineDiagramService;

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
        MvcResult result = mockMvc.perform(get(getWorkspacesConfigBasePath() + "/{id}/workspaces", configId))
            .andExpect(status().isOk())
            .andReturn();

        List<WorkspaceMetadata> metadata = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, WorkspaceMetadata.class)
        );

        assertThat(metadata).hasSize(3);
        assertThat(metadata).extracting(WorkspaceMetadata::name)
            .containsExactly(WORKSPACE_EMPTY, WORKSPACE_WITH_PANELS, WORKSPACE_WITH_NAD);
    }

    @Test
    void testDeleteWorkspacesConfig() throws Exception {
        mockMvc.perform(delete(getWorkspacesConfigBasePath() + "/{id}", configId))
            .andExpect(status().isNoContent());

        assertTrue(workspacesConfigRepository.findById(configId).isEmpty());
    }

    @Test
    void testGetWorkspace() throws Exception {
        MvcResult result = mockMvc.perform(get(getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}", configId, emptyWorkspaceId))
            .andExpect(status().isOk())
            .andReturn();

        WorkspaceInfos workspace = objectMapper.readValue(result.getResponse().getContentAsString(), WorkspaceInfos.class);
        assertThat(workspace.name()).isEqualTo(WORKSPACE_EMPTY);
    }

    @Test
    void testRenameWorkspace() throws Exception {
        mockMvc.perform(put(getWorkspacesConfigBasePath() + "/{id}/workspaces/{workspaceId}/name", configId, emptyWorkspaceId)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Renamed"))
            .andExpect(status().isNoContent());

        assertThat(workspacesConfigService.getWorkspace(configId, emptyWorkspaceId).name()).isEqualTo("Renamed");
    }

    @Test
    void testDuplicateWorkspacesConfig() throws Exception {
        UUID nadConfigId = UUID.randomUUID();

        when(singleLineDiagramService.duplicateNadConfig(any())).thenReturn(nadConfigId);

        MvcResult result = mockMvc.perform(post(getWorkspacesConfigBasePath())
                .param(DUPLICATE_FROM_PARAM, configId.toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedConfigId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);

        assertThat(duplicatedConfigId).isNotEqualTo(configId);
        assertThat(workspacesConfigService.getWorkspacesMetadata(duplicatedConfigId)).hasSize(3);
    }

    @Test
    void testCreateAndUpdatePanels() throws Exception {
        PanelInfos panel = createPanel(PanelType.TREE, "Tree Panel");

        mockMvc.perform(post(getPanelsPath(), configId, emptyWorkspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel))))
            .andExpect(status().isOk());

        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, emptyWorkspaceId, Set.of(panel.getId()));
        assertThat(panels).hasSize(1);
        assertThat(panels.get(0).getType()).isEqualTo(PanelType.TREE);

        panel.setTitle("Updated Title");
        mockMvc.perform(post(getPanelsPath(), configId, emptyWorkspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel))))
            .andExpect(status().isOk());

        panels = workspacesConfigService.getPanels(configId, emptyWorkspaceId, Set.of(panel.getId()));
        assertThat(panels.get(0).getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void testDeletePanels() throws Exception {
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceWithPanelsId, null);
        assertThat(panels).hasSize(2); // TREE and SPREADSHEET panels

        UUID treePanel = panels.stream().filter(p -> p.getType() == PanelType.TREE).findFirst().get().getId();
        UUID spreadsheetPanel = panels.stream().filter(p -> p.getType() == PanelType.SPREADSHEET).findFirst().get().getId();

        mockMvc.perform(delete(getPanelsPath(), configId, workspaceWithPanelsId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(treePanel, spreadsheetPanel))))
            .andExpect(status().isNoContent());

        assertThat(workspacesConfigService.getPanels(configId, workspaceWithPanelsId, null)).isEmpty();
    }

    @Test
    void testSldParentRemapping() throws Exception {
        UUID nadConfigId = UUID.randomUUID();

        when(singleLineDiagramService.duplicateNadConfig(any())).thenReturn(nadConfigId);

        MvcResult result = mockMvc.perform(post(getWorkspacesConfigBasePath())
                .param(DUPLICATE_FROM_PARAM, configId.toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedConfigId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        // The config has 3 workspaces, get the one with NAD (index 2)
        UUID duplicatedWorkspaceId = workspacesConfigService.getWorkspacesMetadata(duplicatedConfigId).get(2).id();
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
        MvcResult result = mockMvc.perform(get(getPanelsPath(), configId, workspaceWithPanelsId))
            .andExpect(status().isOk())
            .andReturn();

        List<PanelInfos> panels = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, PanelInfos.class)
        );

        assertThat(panels).hasSize(2);
        assertThat(panels).extracting(PanelInfos::getType)
            .containsExactlyInAnyOrder(PanelType.TREE, PanelType.SPREADSHEET);
    }

    @Test
    void testGetPanelsWithIds() throws Exception {
        PanelInfos panel1 = createPanel(PanelType.TREE, PANEL_1);
        PanelInfos panel2 = createPanel(PanelType.SPREADSHEET, PANEL_2);

        workspacesConfigService.createOrUpdatePanels(configId, emptyWorkspaceId, List.of(panel1, panel2));

        MvcResult result = mockMvc.perform(get(getPanelsPath(), configId, emptyWorkspaceId)
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
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceWithNadId, null);

        UUID nadPanelId = panels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow()
            .getId();

        mockMvc.perform(delete(getPanelsPath(), configId, workspaceWithNadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(nadPanelId))))
            .andExpect(status().isNoContent());

        verify(singleLineDiagramService).deleteNadConfigs(anyList());
    }

    @Test
    void testSaveNadConfig() throws Exception {
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceWithNadId, null);

        UUID nadPanelId = panels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow()
            .getId();

        UUID newNadConfigId = UUID.randomUUID();
        when(singleLineDiagramService.createOrUpdateNadConfig(any())).thenReturn(newNadConfigId);

        MvcResult result = mockMvc.perform(post(getNadConfigPath(),
                configId, workspaceWithNadId, nadPanelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"someKey\":\"someValue\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        UUID returnedId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        assertThat(returnedId).isEqualTo(newNadConfigId);

        NADPanelInfos updatedPanel = (NADPanelInfos) workspacesConfigService.getPanels(configId, workspaceWithNadId, Set.of(nadPanelId)).get(0);
        assertThat(updatedPanel.getCurrentNadConfigUuid()).isEqualTo(newNadConfigId);
    }

    @Test
    void testDeleteNadConfig() throws Exception {
        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, workspaceWithNadId, null);

        NADPanelInfos nadPanel = (NADPanelInfos) panels.stream()
            .filter(p -> p.getType() == PanelType.NAD)
            .findFirst().orElseThrow();

        mockMvc.perform(delete(getNadConfigPath(),
                configId, workspaceWithNadId, nadPanel.getId()))
            .andExpect(status().isNoContent());

        verify(singleLineDiagramService).deleteNadConfig(any());

        NADPanelInfos updatedPanel = (NADPanelInfos) workspacesConfigService.getPanels(configId, workspaceWithNadId, Set.of(nadPanel.getId())).get(0);
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
        UUID treePanelId = workspacesConfigService.getPanels(configId, workspaceWithPanelsId, null)
            .stream().filter(p -> p.getType() == PanelType.TREE).findFirst().get().getId();

        mockMvc.perform(post(getNadConfigPath(),
                configId, workspaceWithPanelsId, treePanelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteNadConfigWhenNoneExists() throws Exception {
        NADPanelInfos nadPanel = new NADPanelInfos();
        nadPanel.setId(UUID.randomUUID());
        nadPanel.setType(PanelType.NAD);
        nadPanel.setTitle("NAD without config");
        nadPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        nadPanel.setSize(new PanelSizeInfos(1.0, 1.0));

        workspacesConfigService.createOrUpdatePanels(configId, emptyWorkspaceId, List.of(nadPanel));

        mockMvc.perform(delete(getNadConfigPath(),
                configId, emptyWorkspaceId, nadPanel.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateMultiplePanelsAtOnce() throws Exception {
        PanelInfos panel1 = createPanel(PanelType.TREE, PANEL_1);
        PanelInfos panel2 = createPanel(PanelType.SPREADSHEET, PANEL_2);
        PanelInfos panel3 = createPanel(PanelType.NAD, "Panel 3");

        mockMvc.perform(post(getPanelsPath(), configId, emptyWorkspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel1, panel2, panel3))))
            .andExpect(status().isOk());

        List<PanelInfos> panels = workspacesConfigService.getPanels(configId, emptyWorkspaceId, null);
        assertThat(panels).hasSize(3);
    }

    @Test
    void testDeleteMultiplePanelsAtOnce() throws Exception {
        PanelInfos panel1 = createPanel(PanelType.TREE, PANEL_1);
        PanelInfos panel2 = createPanel(PanelType.SPREADSHEET, PANEL_2);

        workspacesConfigService.createOrUpdatePanels(configId, emptyWorkspaceId, List.of(panel1, panel2));

        mockMvc.perform(delete(getPanelsPath(), configId, emptyWorkspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panel1.getId(), panel2.getId()))))
            .andExpect(status().isNoContent());

        assertThat(workspacesConfigService.getPanels(configId, emptyWorkspaceId, null)).isEmpty();
    }

}
