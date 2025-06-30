package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.VoltageLevelDiagramLayout;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class VoltageLevelLayoutEntity extends AbstractDiagramLayoutEntity {
    String voltageLevelId;

    public VoltageLevelDiagramLayout toDto() {
        return VoltageLevelDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .w(width)
            .h(height)
            .x(xPosition)
            .y(yPosition)
            .voltageLevelId(voltageLevelId)
            .build();
    }
}
