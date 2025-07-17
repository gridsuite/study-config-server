package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
public class DiagramGridLayoutEntity {
    Integer width;
    Integer height;
    Integer xPosition;
    Integer yPosition;
}
