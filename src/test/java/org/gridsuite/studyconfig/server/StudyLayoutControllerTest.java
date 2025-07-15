package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NetworkAreaDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.SubstationDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.VoltageLevelDiagramLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutRepository;
import org.gridsuite.studyconfig.server.service.StudyLayoutService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class StudyLayoutControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private StudyLayoutRepository studyLayoutRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StudyLayoutService studyLayoutService;

    @Test
    void testGetStudyLayout() throws Exception {
        StudyLayoutEntity expectedResult = studyLayoutRepository.save(createStudyLayout().toEntity());
        MvcResult mockMvcResult = mockMvc.perform(get("/v1/study-layout/{studyLayoutUuid}", expectedResult.getUuid()))
            .andExpect(status().isOk())
            .andReturn();

        StudyLayout result = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), StudyLayout.class);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult.toDto());
    }

    @Test
    void testSaveStudyLayout() throws Exception {
        StudyLayout studyLayoutToSave = createStudyLayout();

        MvcResult mockMvcResult = mockMvc.perform(post("/v1/study-layout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studyLayoutToSave)))
            .andExpect(status().isOk())
            .andReturn();

        UUID studyLayoutUUID = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), UUID.class);

        StudyLayout studyLayoutToCheck = studyLayoutService.getByStudyLayoutUuid(studyLayoutUUID);
        assertThat(studyLayoutToCheck).usingRecursiveComparison().isEqualTo(studyLayoutToSave);
    }

    @Test
    void testUpdateStudyLayout() throws Exception {
        StudyLayoutEntity existingStudyLayout = studyLayoutRepository.save(createStudyLayout().toEntity());

        UUID newDiagramLayoutUuid = UUID.randomUUID();
        StudyLayout updatedStudyLayout = createStudyLayout();
        updatedStudyLayout.getDiagramLayoutParams().add(SubstationDiagramLayout.builder()
            .substationId("s1")
            .gridLayout(Map.of(
                "lg",
                DiagramGridLayout.builder().w(5)
                    .h(6)
                    .x(7)
                    .y(8)
                    .build()
            ))

            .diagramUuid(newDiagramLayoutUuid)
            .build());

        UUID diagramUuid = UUID.randomUUID();
        List<String> voltageLevelIds = List.of("vl1", "vl2", "vl3");
        Integer depth = 5;

        updatedStudyLayout.getDiagramLayoutParams().add(NetworkAreaDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(Map.of(
                "lg",
                DiagramGridLayout.builder()
                    .w(10)
                    .h(20)
                    .x(30)
                    .y(40)
                    .build()
            ))
            .voltageLevelIds(voltageLevelIds)
            .depth(depth)
            .build());

        mockMvc.perform(put("/v1/study-layout/{studyLayoutUuid}", existingStudyLayout.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudyLayout)))
            .andExpect(status().isOk())
            .andReturn();

        StudyLayout studyLayoutToCheck = studyLayoutService.getByStudyLayoutUuid(existingStudyLayout.getUuid());
        assertThat(studyLayoutToCheck).usingRecursiveComparison().isEqualTo(updatedStudyLayout);
    }

    private StudyLayout createStudyLayout() {
        UUID diagramLayoutUuid = UUID.randomUUID();
        return StudyLayout.builder()
            .diagramLayoutParams(new ArrayList<>(List.of(
                VoltageLevelDiagramLayout.builder()
                    .voltageLevelId("vl1")
                    .gridLayout(Map.of(
                        "lg",
                        DiagramGridLayout.builder().w(1)
                            .h(2)
                            .x(3)
                            .y(4)
                            .build()
                    ))

                    .diagramUuid(diagramLayoutUuid)
                    .build())))
            .build();
    }
}
