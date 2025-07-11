package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.DiagramGridLayout;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class DiagramGridLayoutEntity {
    Integer width;
    Integer height;
    Integer xPosition;
    Integer yPosition;

    public DiagramGridLayout toDto() {
        return DiagramGridLayout.builder()
            .w(width)
            .h(height)
            .x(xPosition)
            .y(yPosition)
            .build();
    }
}
