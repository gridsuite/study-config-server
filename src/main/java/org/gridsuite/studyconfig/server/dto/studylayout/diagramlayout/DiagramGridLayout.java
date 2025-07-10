package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.gridsuite.studyconfig.server.entities.studylayout.DiagramGridLayoutEntity;

@Builder
@AllArgsConstructor
@Getter
public class DiagramGridLayout {
    Integer w;
    Integer h;
    Integer x;
    Integer y;

    public DiagramGridLayoutEntity toEntity() {
        return DiagramGridLayoutEntity.builder()
            .width(w)
            .height(h)
            .xPosition(x)
            .yPosition(y)
            .build();
    }
}
