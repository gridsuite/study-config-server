/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gridsuite.studyconfig.server.dto.diagramgridlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.DiagramPosition;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.NetworkAreaDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.SubstationDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.VoltageLevelDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.MapLayout;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.DiagramGridLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.DiagramGridLayoutRepository;
import org.gridsuite.studyconfig.server.mapper.DiagramGridLayoutMapper;
import org.gridsuite.studyconfig.server.service.DiagramGridLayoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class DiagramGridLayoutControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private DiagramGridLayoutRepository diagramGridLayoutRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiagramGridLayoutService diagramGridLayoutService;

    @Test
    void testGetDiagramGridLayout() throws Exception {
        DiagramGridLayoutEntity expectedResult = diagramGridLayoutRepository.save(DiagramGridLayoutMapper.toEntity(createDiagramGridLayout()));
        MvcResult mockMvcResult = mockMvc.perform(get("/v1/diagram-grid-layout/{diagramGridLayoutUuid}", expectedResult.getUuid()))
            .andExpect(status().isOk())
            .andReturn();

        DiagramGridLayout result = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), DiagramGridLayout.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(DiagramGridLayoutMapper.toDto(expectedResult));
    }

    @Test
    void testDeleteDiagramGridLayout() throws Exception {
        DiagramGridLayoutEntity expectedResult = diagramGridLayoutRepository.save(DiagramGridLayoutMapper.toEntity(createDiagramGridLayout()));
        mockMvc.perform(delete("/v1/diagram-grid-layout/{diagramGridLayoutUuid}", expectedResult.getUuid()))
            .andExpect(status().isOk());

        assertTrue(diagramGridLayoutRepository.findById(expectedResult.getUuid()).isEmpty());
    }

    @Test
    void testSaveDiagramGridLayout() throws Exception {
        DiagramGridLayout diagramGridLayoutToSave = createDiagramGridLayout();

        MvcResult mockMvcResult = mockMvc.perform(post("/v1/diagram-grid-layout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(diagramGridLayoutToSave)))
            .andExpect(status().isOk())
            .andReturn();

        UUID diagramGridLayoutUUID = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), UUID.class);

        DiagramGridLayout diagramGridLayoutToCheck = diagramGridLayoutService.getByDiagramGridLayoutUuid(diagramGridLayoutUUID);
        assertThat(diagramGridLayoutToCheck).usingRecursiveComparison().isEqualTo(diagramGridLayoutToSave);
    }

    @Test
    void testSaveMapGridLayout() throws Exception {
        DiagramGridLayout mapLayoutToSave = createMapGridLayout();

        MvcResult mockMvcResult = mockMvc.perform(post("/v1/diagram-grid-layout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapLayoutToSave)))
            .andExpect(status().isOk())
            .andReturn();

        UUID mapGridLayoutUUID = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), UUID.class);

        DiagramGridLayout diagramGridLayoutToCheck = diagramGridLayoutService.getByDiagramGridLayoutUuid(mapGridLayoutUUID);
        assertThat(diagramGridLayoutToCheck).usingRecursiveComparison().isEqualTo(mapLayoutToSave);
    }

    @Test
    void testUpdateDiagramGridLayout() throws Exception {
        DiagramGridLayoutEntity existingDiagramGridLayout = diagramGridLayoutRepository.save(DiagramGridLayoutMapper.toEntity(createDiagramGridLayout()));

        UUID newDiagramLayoutUuid = UUID.randomUUID();
        DiagramGridLayout updatedDiagramGridLayout = createDiagramGridLayout();
        updatedDiagramGridLayout.getDiagramLayouts().add(SubstationDiagramLayout.builder()
            .substationId("s1")
            .diagramPositions(Map.of(
                "lg",
                DiagramPosition.builder().w(5)
                    .h(6)
                    .x(7)
                    .y(8)
                    .build()
            ))

            .diagramUuid(newDiagramLayoutUuid)
            .build());

        updatedDiagramGridLayout.getDiagramLayouts().add(NetworkAreaDiagramLayout.builder()
            .diagramUuid(UUID.randomUUID())
            .diagramPositions(Map.of(
                "lg",
                DiagramPosition.builder()
                    .w(10)
                    .h(20)
                    .x(30)
                    .y(40)
                    .build()
            ))
            .originalNadConfigUuid(UUID.randomUUID())
            .currentNadConfigUuid(UUID.randomUUID())
            .originalFilterUuid(UUID.randomUUID())
            .currentFilterUuid(UUID.randomUUID())
            .name("Network Area Layout")
            .build());

        mockMvc.perform(put("/v1/diagram-grid-layout/{diagramLayoutUuid}", existingDiagramGridLayout.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDiagramGridLayout)))
            .andExpect(status().isOk())
            .andReturn();

        DiagramGridLayout diagramGridLayoutToCheck = diagramGridLayoutService.getByDiagramGridLayoutUuid(existingDiagramGridLayout.getUuid());
        assertThat(diagramGridLayoutToCheck).usingRecursiveComparison().isEqualTo(updatedDiagramGridLayout);
    }

    private DiagramGridLayout createDiagramGridLayout() {
        UUID diagramLayoutUuid = UUID.randomUUID();
        return DiagramGridLayout.builder()
            .diagramLayouts(new ArrayList<>(List.of(
                VoltageLevelDiagramLayout.builder()
                    .voltageLevelId("vl1")
                    .diagramPositions(Map.of(
                        "lg",
                        DiagramPosition.builder().w(1)
                            .h(2)
                            .x(3)
                            .y(4)
                            .build()
                    ))

                    .diagramUuid(diagramLayoutUuid)
                    .build())))
            .build();
    }

    private DiagramGridLayout createMapGridLayout() {
        return DiagramGridLayout.builder()
            .diagramLayouts(new ArrayList<>(List.of(
                MapLayout.builder()
                    .diagramPositions(Map.of(
                        "lg",
                        DiagramPosition.builder().w(1)
                            .h(2)
                            .x(3)
                            .y(4)
                            .build()
                    ))
                    .diagramUuid(UUID.randomUUID())
                    .build())))
            .build();
    }
}
