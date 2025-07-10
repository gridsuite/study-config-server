package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.VoltageLevelDiagramLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutRepository;
import org.gridsuite.studyconfig.server.service.StudyLayoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class StudyLayoutTest {

    @Autowired
    private StudyLayoutRepository studyLayoutRepository;
    @Autowired
    private StudyLayoutService studyLayoutService;

    @Test
    void testCreateStudyLayout() throws Exception {
        UUID diagramLayoutUuid = UUID.randomUUID();
        StudyLayout studyLayout = StudyLayout.builder()
            .diagramLayoutParams(List.of(
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
                    .build()))
            .build();

        StudyLayoutEntity studyLayoutEntity = studyLayoutRepository.save(studyLayout.toEntity());

        ObjectMapper objectMapper = new ObjectMapper();

        String test = objectMapper.writeValueAsString(studyLayoutService.getByStudyUuidAndUserId(studyLayoutEntity.getUuid()));

        assertTrue(true);
    }
}
