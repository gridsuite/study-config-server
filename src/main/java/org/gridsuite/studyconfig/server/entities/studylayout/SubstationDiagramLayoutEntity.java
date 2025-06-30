package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.SubstationDiagramLayout;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class SubstationDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    String substationId;

    public SubstationDiagramLayout toDto() {
        return SubstationDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .w(width)
            .h(height)
            .x(xPosition)
            .y(yPosition)
            .substationId(substationId)
            .build();
    }
}
