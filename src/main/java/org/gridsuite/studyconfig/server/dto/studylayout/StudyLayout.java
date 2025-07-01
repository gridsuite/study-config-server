package org.gridsuite.studyconfig.server.dto.studylayout;

import lombok.*;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.AbstractDiagramLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudyLayout {
    List<AbstractDiagramLayout> diagramLayoutParams;

    public StudyLayoutEntity toEntity() {
        return StudyLayoutEntity.builder()
            .diagramGridLayoutEntityList(diagramLayoutParams.stream().map(AbstractDiagramLayout::toEntity).toList())
            .build();
    }
}
