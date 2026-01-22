/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.gridsuite.studyconfig.server.service.SingleLineDiagramService;
import org.gridsuite.studyconfig.server.service.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkspaceControllerTest extends AbstractWorkspaceTestBase {

    private static final String DUPLICATE_FROM_PARAM = "duplicateFrom";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspaceService workspaceService;

    @MockitoBean
    private SingleLineDiagramService singleLineDiagramService;

    private String getWorkspacesBasePath() {
        return "/" + StudyConfigApi.API_VERSION + "/workspaces";
    }

    @Test
    void testGetWorkspace() throws Exception {
        MvcResult result = mockMvc.perform(get(getWorkspacesBasePath() + "/{workspaceId}", workspaceWithPanelsId))
            .andExpect(status().isOk())
            .andReturn();

        WorkspaceInfos workspace = objectMapper.readValue(result.getResponse().getContentAsString(), WorkspaceInfos.class);
        assertThat(workspace.name()).isEqualTo(WORKSPACE_WITH_PANELS);
        assertThat(workspace.panels()).hasSize(2);
    }

    @Test
    void testGetWorkspaceNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get(getWorkspacesBasePath() + "/{workspaceId}", nonExistentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDuplicateWorkspace() throws Exception {
        MvcResult result = mockMvc.perform(
                post(getWorkspacesBasePath())
                    .param(DUPLICATE_FROM_PARAM, workspaceWithPanelsId.toString())
            )
            .andExpect(status().isCreated())
            .andReturn();

        UUID newWorkspaceId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        assertThat(newWorkspaceId).isNotNull().isNotEqualTo(workspaceWithPanelsId);

        WorkspaceInfos newWorkspace = workspaceService.getWorkspace(newWorkspaceId)
            .map(WorkspaceEntity::toDto)
            .orElseThrow();
        assertThat(newWorkspace.name()).isEqualTo(WORKSPACE_WITH_PANELS);
        assertThat(newWorkspace.panels()).hasSize(2);
    }

    @Test
    void testDuplicateWorkspaceWithNadConfig() throws Exception {
        WorkspaceInfos sourceWorkspace = workspaceService.getWorkspace(workspaceWithNadId)
            .map(WorkspaceEntity::toDto)
            .orElseThrow();
        UUID originalNadConfigId = ((org.gridsuite.studyconfig.server.dto.workspace.NADPanelInfos)
            sourceWorkspace.panels().get(0)).getCurrentNadConfigUuid();

        UUID duplicatedNadConfigId = UUID.randomUUID();
        when(singleLineDiagramService.duplicateNadConfig(originalNadConfigId)).thenReturn(duplicatedNadConfigId);

        MvcResult result = mockMvc.perform(
                post(getWorkspacesBasePath())
                    .param(DUPLICATE_FROM_PARAM, workspaceWithNadId.toString())
            )
            .andExpect(status().isCreated())
            .andReturn();

        UUID newWorkspaceId = objectMapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        verify(singleLineDiagramService, times(1)).duplicateNadConfig(originalNadConfigId);

        WorkspaceInfos newWorkspace = workspaceService.getWorkspace(newWorkspaceId)
            .map(WorkspaceEntity::toDto)
            .orElseThrow();
        assertThat(newWorkspace.panels()).hasSize(2);
        assertThat(((org.gridsuite.studyconfig.server.dto.workspace.NADPanelInfos) newWorkspace.panels().get(0))
            .getCurrentNadConfigUuid()).isEqualTo(duplicatedNadConfigId);
    }

    @Test
    void testDuplicateWorkspaceNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(
                post(getWorkspacesBasePath())
                    .param(DUPLICATE_FROM_PARAM, nonExistentId.toString())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void testReplaceWorkspace() throws Exception {
        mockMvc.perform(
                put(getWorkspacesBasePath() + "/{workspaceId}/replace", emptyWorkspaceId)
                    .param(DUPLICATE_FROM_PARAM, workspaceWithPanelsId.toString())
            )
            .andExpect(status().isNoContent());

        WorkspaceInfos replacedWorkspace = workspaceService.getWorkspace(emptyWorkspaceId)
            .map(WorkspaceEntity::toDto)
            .orElseThrow();
        assertThat(replacedWorkspace.name()).isEqualTo(WORKSPACE_WITH_PANELS);
        assertThat(replacedWorkspace.panels()).hasSize(2);
    }

    @Test
    void testReplaceWorkspaceWithNadConfig() throws Exception {
        UUID newNadConfigId = UUID.randomUUID();

        when(singleLineDiagramService.duplicateNadConfig(any())).thenReturn(newNadConfigId);

        mockMvc.perform(
                put(getWorkspacesBasePath() + "/{workspaceId}/replace", emptyWorkspaceId)
                    .param(DUPLICATE_FROM_PARAM, workspaceWithNadId.toString())
            )
            .andExpect(status().isNoContent());

        verify(singleLineDiagramService, never()).deleteNadConfigs(any()); // Empty workspace has no NAD to delete
        verify(singleLineDiagramService, atLeastOnce()).duplicateNadConfig(any());

        WorkspaceInfos replacedWorkspace = workspaceService.getWorkspace(emptyWorkspaceId)
            .map(WorkspaceEntity::toDto)
            .orElseThrow();
        assertThat(replacedWorkspace.panels()).hasSize(2);
    }

    @Test
    void testReplaceWorkspaceNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(
                put(getWorkspacesBasePath() + "/{workspaceId}/replace", nonExistentId)
                    .param(DUPLICATE_FROM_PARAM, workspaceWithPanelsId.toString())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void testReplaceWorkspaceSourceNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(
                put(getWorkspacesBasePath() + "/{workspaceId}/replace", emptyWorkspaceId)
                    .param(DUPLICATE_FROM_PARAM, nonExistentId.toString())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteWorkspace() throws Exception {
        mockMvc.perform(delete(getWorkspacesBasePath() + "/{workspaceId}", workspaceWithPanelsId))
            .andExpect(status().isNoContent());

        assertTrue(workspaceRepository.findById(workspaceWithPanelsId).isEmpty());
    }

    @Test
    void testDeleteWorkspaceWithNadConfig() throws Exception {
        mockMvc.perform(delete(getWorkspacesBasePath() + "/{workspaceId}", workspaceWithNadId))
            .andExpect(status().isNoContent());

        verify(singleLineDiagramService, atLeastOnce()).deleteNadConfigs(any());
        assertTrue(workspaceRepository.findById(workspaceWithNadId).isEmpty());
    }

    @Test
    void testDeleteWorkspaceNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete(getWorkspacesBasePath() + "/{workspaceId}", nonExistentId))
            .andExpect(status().isNotFound());
    }

}
