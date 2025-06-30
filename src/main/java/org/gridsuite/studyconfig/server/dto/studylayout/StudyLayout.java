package org.gridsuite.studyconfig.server.dto.studylayout;

import lombok.Builder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.AbstractDiagramLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;

import java.util.List;

@Builder
public class StudyLayout {
    List<AbstractDiagramLayout> diagramLayoutParams;

    public StudyLayoutEntity toEntity() {
        return StudyLayoutEntity.builder()
            .diagramGridLayoutEntityList(diagramLayoutParams.stream().map(AbstractDiagramLayout::toEntity).toList())
            .build();
    }
}
