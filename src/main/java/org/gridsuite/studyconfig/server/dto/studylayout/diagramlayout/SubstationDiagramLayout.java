package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.SubstationDiagramLayoutEntity;

@SuperBuilder
public class SubstationDiagramLayout extends AbstractDiagramLayout {
    String substationId;

    @Override
    public DiagramLayoutType getDiagramType() {
        return DiagramLayoutType.SUBSTATION;
    }

    @Override
    public SubstationDiagramLayoutEntity toEntity() {
        return SubstationDiagramLayoutEntity.builder()
            .diagramUuid(diagramUuid)
            .width(w)
            .height(h)
            .xPosition(x)
            .yPosition(y)
            .substationId(substationId)
            .build();
    }
}
