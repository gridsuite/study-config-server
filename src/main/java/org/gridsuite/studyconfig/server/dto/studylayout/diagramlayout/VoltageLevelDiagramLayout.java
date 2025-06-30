package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.VoltageLevelLayoutEntity;

@SuperBuilder
public class VoltageLevelDiagramLayout extends AbstractDiagramLayout {
    String voltageLevelId;

    @Override
    public DiagramLayoutType getDiagramType() {
        return DiagramLayoutType.VOLTAGE_LEVEL;
    }

    @Override
    public VoltageLevelLayoutEntity toEntity() {
        return VoltageLevelLayoutEntity.builder()
            .diagramUuid(diagramUuid)
            .width(w)
            .height(h)
            .xPosition(x)
            .yPosition(y)
            .voltageLevelId(voltageLevelId)
            .build();
    }
}
