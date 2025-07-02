package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.VoltageLevelLayoutEntity;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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
