/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.workspace.PanelEntity;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceCollectionEntity;
import org.gridsuite.studyconfig.server.mapper.WorkspaceMapper;
import org.gridsuite.studyconfig.server.repositories.WorkspaceCollectionRepository;
import org.gridsuite.studyconfig.server.service.WorkspaceCollectionService;
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
class WorkspaceCollectionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WorkspaceCollectionRepository workspaceCollectionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkspaceCollectionService workspaceCollectionService;

    @Test
    void testGetWorkspaceCollection() throws Exception {
        WorkspaceCollectionEntity expectedResult = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );

        MvcResult mockMvcResult = mockMvc.perform(get("/v1/workspace-collections/{id}", expectedResult.getId()))
            .andExpect(status().isOk())
            .andReturn();

        WorkspaceCollectionInfos result = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            WorkspaceCollectionInfos.class
        );
        assertThat(result).usingRecursiveComparison().isEqualTo(WorkspaceMapper.toDto(expectedResult));
    }

    @Test
    void testDeleteWorkspaceCollection() throws Exception {
        WorkspaceCollectionEntity expectedResult = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );

        mockMvc.perform(delete("/v1/workspace-collections/{id}", expectedResult.getId()))
            .andExpect(status().isNoContent());

        assertTrue(workspaceCollectionRepository.findById(expectedResult.getId()).isEmpty());
    }

    @Test
    void testCreateWorkspaceCollection() throws Exception {
        WorkspaceCollectionInfos workspaceCollectionToSave = createWorkspaceCollection();

        MvcResult mockMvcResult = mockMvc.perform(post("/v1/workspace-collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workspaceCollectionToSave)))
            .andExpect(status().isCreated())
            .andReturn();

        UUID workspaceCollectionUuid = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            UUID.class
        );

        WorkspaceCollectionInfos workspaceCollectionToCheck = workspaceCollectionService.getWorkspaceCollection(workspaceCollectionUuid);
        assertThat(workspaceCollectionToCheck.workspaces()).hasSize(2);
        assertThat(workspaceCollectionToCheck.workspaces().get(0).name()).isEqualTo("Workspace 1");
        assertThat(workspaceCollectionToCheck.workspaces().get(1).name()).isEqualTo("Workspace 2");
    }

    @Test
    void testUpdateWorkspaceCollection() throws Exception {
        WorkspaceCollectionEntity existingWorkspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );

        WorkspaceCollectionInfos updatedWorkspaceCollection = new WorkspaceCollectionInfos(
            existingWorkspaceCollection.getId(),
            List.of(
                createWorkspace("Updated Workspace 1"),
                createWorkspace("Updated Workspace 2"),
                createWorkspace("New Workspace 3")
            )
        );

        mockMvc.perform(put("/v1/workspace-collections/{id}", existingWorkspaceCollection.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedWorkspaceCollection)))
            .andExpect(status().isNoContent());

        WorkspaceCollectionInfos workspaceCollectionToCheck = workspaceCollectionService.getWorkspaceCollection(existingWorkspaceCollection.getId());
        assertThat(workspaceCollectionToCheck.workspaces()).hasSize(3);
        assertThat(workspaceCollectionToCheck.workspaces().get(0).name()).isEqualTo("Updated Workspace 1");
    }

    @Test
    void testDuplicateWorkspaceCollection() throws Exception {
        WorkspaceCollectionEntity originalWorkspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );

        MvcResult mockMvcResult = mockMvc.perform(post("/v1/workspace-collections")
                .param("duplicateFrom", originalWorkspaceCollection.getId().toString()))
            .andExpect(status().isCreated())
            .andReturn();

        UUID duplicatedWorkspaceCollectionUuid = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            UUID.class
        );

        WorkspaceCollectionInfos duplicatedWorkspaceCollection = workspaceCollectionService.getWorkspaceCollection(duplicatedWorkspaceCollectionUuid);
        WorkspaceCollectionInfos originalWorkspaceCollectionDto = workspaceCollectionService.getWorkspaceCollection(originalWorkspaceCollection.getId());

        assertThat(duplicatedWorkspaceCollection.workspaces()).hasSize(originalWorkspaceCollectionDto.workspaces().size());
        assertThat(duplicatedWorkspaceCollection.id()).isNotEqualTo(originalWorkspaceCollectionDto.id());
    }

    @Test
    void testCreateDefaultWorkspaceCollection() throws Exception {
        MvcResult mockMvcResult = mockMvc.perform(post("/v1/workspace-collections/default"))
            .andExpect(status().isCreated())
            .andReturn();

        UUID workspaceCollectionUuid = objectMapper.readValue(
            mockMvcResult.getResponse().getContentAsString(),
            UUID.class
        );

        WorkspaceCollectionInfos workspaceCollection = workspaceCollectionService.getWorkspaceCollection(workspaceCollectionUuid);
        assertThat(workspaceCollection.workspaces()).hasSize(3);
        // First workspace should have Tree and Node Editor panels
        assertThat(workspaceCollection.workspaces().get(0).panels()).hasSize(2);
    }

    @Test
    void testUpdateWorkspace() throws Exception {
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();

        WorkspaceInfos updatedWorkspace = new WorkspaceInfos(
            workspaceId,
            "Updated Workspace Name",
            List.of()
        );

        mockMvc.perform(put("/v1/workspace-collections/{id}/workspaces/{workspaceId}",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedWorkspace)))
            .andExpect(status().isNoContent());

        WorkspaceInfos workspaceToCheck = workspaceCollectionService.getWorkspace(workspaceCollection.getId(), workspaceId);
        assertThat(workspaceToCheck.name()).isEqualTo("Updated Workspace Name");
    }

    @Test
    void testCreatePanel() throws Exception {
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();

        PanelInfos newPanel = createPanel("TREE");
        UUID panelId = newPanel.id() != null ? newPanel.id() : UUID.randomUUID();
        PanelInfos panelWithId = new PanelInfos(panelId, newPanel.type(), newPanel.title(), newPanel.position(),
            newPanel.size(), newPanel.zIndex(), newPanel.orderIndex(), newPanel.isMinimized(), newPanel.isMaximized(),
            newPanel.isPinned(), newPanel.isClosed(), newPanel.restorePosition(), newPanel.restoreSize(), newPanel.metadata());

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelWithId))))
            .andExpect(status().isNoContent());

        PanelInfos panelToCheck = workspaceCollectionService.getPanel(workspaceCollection.getId(), workspaceId, panelId);
        assertThat(panelToCheck.type()).isEqualTo(PanelEntity.PanelType.TREE);
    }

    @Test
    void testUpdatePanel() throws Exception {
        WorkspaceCollectionInfos workspaceCollectionInfos = createWorkspaceCollectionWithPanel();
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(workspaceCollectionInfos)
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();
        UUID panelId = workspaceCollection.getWorkspaces().get(0).getPanels().get(0).getId();

        PanelInfos updatedPanel = new PanelInfos(
            panelId,
            PanelEntity.PanelType.TREE,
            "Tree Panel",
            new PanelPositionInfos(0.5, 0.5),
            new PanelSizeInfos(0.3, 0.3),
            2,
            0,
            true,
            false,
            false,
            false,
            null,
            null,
            null
        );

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        PanelInfos panelToCheck = workspaceCollectionService.getPanel(workspaceCollection.getId(), workspaceId, panelId);
        assertThat(panelToCheck.position().x()).isEqualTo(0.5);
        assertThat(panelToCheck.isMinimized()).isTrue();
    }

    @Test
    void testDeletePanel() throws Exception {
        WorkspaceCollectionInfos workspaceCollectionInfos = createWorkspaceCollectionWithPanel();
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(workspaceCollectionInfos)
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();
        UUID panelId = workspaceCollection.getWorkspaces().get(0).getPanels().get(0).getId();

        mockMvc.perform(delete("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelId))))
            .andExpect(status().isNoContent());

        List<PanelInfos> panels = workspaceCollectionService.getPanels(workspaceCollection.getId(), workspaceId, null);
        assertThat(panels).isEmpty();
    }

    @Test
    void testUpdatePanelWithRestorePositionAndSize() throws Exception {
        WorkspaceCollectionInfos workspaceCollectionInfos = createWorkspaceCollectionWithPanel();
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(workspaceCollectionInfos)
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();
        UUID panelId = workspaceCollection.getWorkspaces().get(0).getPanels().get(0).getId();

        PanelInfos updatedPanel = new PanelInfos(
            panelId,
            PanelEntity.PanelType.TREE,
            "Tree Panel",
            new PanelPositionInfos(0.5, 0.5),
            new PanelSizeInfos(0.3, 0.3),
            2,
            0,
            false,
            true,
            true,
            false,
            new PanelPositionInfos(0.2, 0.2),
            new PanelSizeInfos(0.6, 0.6),
            null
        );

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        PanelInfos panelToCheck = workspaceCollectionService.getPanel(workspaceCollection.getId(), workspaceId, panelId);
        assertThat(panelToCheck.restorePosition()).isNotNull();
        assertThat(panelToCheck.restorePosition().x()).isEqualTo(0.2);
        assertThat(panelToCheck.restoreSize()).isNotNull();
        assertThat(panelToCheck.restoreSize().width()).isEqualTo(0.6);
        assertThat(panelToCheck.isMaximized()).isTrue();
        assertThat(panelToCheck.isPinned()).isTrue();
    }

    // Error scenarios
    @Test
    void testGetWorkspaceCollectionNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/v1/workspace-collections/{id}", nonExistentId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testPanelWithSLDMetadata() throws Exception {
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();

        SLDPanelMetadataInfos sldMetadata = new SLDPanelMetadataInfos(
            "voltage-level-123",
            null,
            List.of("diagram1", "diagram2", "diagram3")
        );

        PanelInfos sldPanel = new PanelInfos(
            null,
            PanelEntity.PanelType.SLD_VOLTAGE_LEVEL,
            "SLD with Metadata",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            1,
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
            sldPanel.size(), sldPanel.zIndex(), sldPanel.orderIndex(), sldPanel.isMinimized(), sldPanel.isMaximized(),
            sldPanel.isPinned(), sldPanel.isClosed(), sldPanel.restorePosition(), sldPanel.restoreSize(), sldPanel.metadata());

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(sldPanelWithId))))
            .andExpect(status().isNoContent());

        PanelInfos retrievedPanel = workspaceCollectionService.getPanel(workspaceCollection.getId(), workspaceId, panelId);
        assertThat(retrievedPanel.metadata()).isNotNull();
        assertThat(retrievedPanel.metadata()).isInstanceOf(SLDPanelMetadataInfos.class);

        SLDPanelMetadataInfos retrievedMetadata = (SLDPanelMetadataInfos) retrievedPanel.metadata();
        assertThat(retrievedMetadata.diagramId()).isEqualTo("voltage-level-123");
        assertThat(retrievedMetadata.sldNavigationHistory()).containsExactlyElementsOf(sldMetadata.sldNavigationHistory());
    }

    @Test
    void testPanelWithNADMetadata() throws Exception {
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();

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
            PanelEntity.PanelType.NAD,
            "NAD with Metadata",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            1,
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
            nadPanel.size(), nadPanel.zIndex(), nadPanel.orderIndex(), nadPanel.isMinimized(), nadPanel.isMaximized(),
            nadPanel.isPinned(), nadPanel.isClosed(), nadPanel.restorePosition(), nadPanel.restoreSize(), nadPanel.metadata());

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(nadPanelWithId))))
            .andExpect(status().isNoContent());

        PanelInfos retrievedPanel = workspaceCollectionService.getPanel(workspaceCollection.getId(), workspaceId, panelId);
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
        WorkspaceCollectionEntity workspaceCollection = workspaceCollectionRepository.save(
            WorkspaceMapper.toEntity(createWorkspaceCollection())
        );
        UUID workspaceId = workspaceCollection.getWorkspaces().get(0).getId();

        SLDPanelMetadataInfos sldMetadata = new SLDPanelMetadataInfos("diagram-id", null, List.of("nav1"));
        PanelInfos panelWithMetadata = new PanelInfos(
            null,
            PanelEntity.PanelType.SLD_SUBSTATION,
            "SLD Panel",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            1,
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
            panelWithMetadata.size(), panelWithMetadata.zIndex(), panelWithMetadata.orderIndex(), panelWithMetadata.isMinimized(), panelWithMetadata.isMaximized(),
            panelWithMetadata.isPinned(), panelWithMetadata.isClosed(), panelWithMetadata.restorePosition(),
            panelWithMetadata.restoreSize(), panelWithMetadata.metadata());

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(panelWithId))))
            .andExpect(status().isNoContent());

        // Update to remove metadata
        PanelInfos updatedPanel = new PanelInfos(
            panelId,
            PanelEntity.PanelType.TREE,
            "Panel without Metadata",
            new PanelPositionInfos(0.5, 0.5),
            new PanelSizeInfos(0.3, 0.3),
            2,
            0,
            false,
            false,
            false,
            false,
            null,
            null,
            null
        );

        mockMvc.perform(post("/v1/workspace-collections/{id}/workspaces/{workspaceId}/panels",
                workspaceCollection.getId(), workspaceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(updatedPanel))))
            .andExpect(status().isNoContent());

        PanelInfos panelToCheck = workspaceCollectionService.getPanel(workspaceCollection.getId(), workspaceId, panelId);
        assertThat(panelToCheck.metadata()).isNull();
    }

    // Helper methods
    private WorkspaceCollectionInfos createWorkspaceCollection() {
        return new WorkspaceCollectionInfos(
            null,
            List.of(
                createWorkspace("Workspace 1"),
                createWorkspace("Workspace 2")
            )
        );
    }

    private WorkspaceCollectionInfos createWorkspaceCollectionWithPanel() {
        PanelInfos panel = createPanel("TREE");
        WorkspaceInfos workspace = new WorkspaceInfos(
            null,
            "Workspace with Panel",
            List.of(panel)
        );
        return new WorkspaceCollectionInfos(null, List.of(workspace));
    }

    private WorkspaceInfos createWorkspace(String name) {
        return new WorkspaceInfos(
            null,
            name,
            new ArrayList<>()
        );
    }

    private PanelInfos createPanel(String type) {
        PanelEntity.PanelType panelType = PanelEntity.PanelType.valueOf(type);
        return new PanelInfos(
            UUID.randomUUID(),
            panelType,
            type + " Panel",
            new PanelPositionInfos(0.0, 0.0),
            new PanelSizeInfos(0.5, 1.0),
            1,
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
