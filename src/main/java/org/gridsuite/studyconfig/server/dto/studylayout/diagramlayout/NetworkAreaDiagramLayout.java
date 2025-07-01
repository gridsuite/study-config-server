package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.NetworkAreaDiagramLayoutEntity;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class NetworkAreaDiagramLayout extends AbstractDiagramLayout {
    List<String> voltageLevelIds;
    Integer depth;

    @Override
    public DiagramLayoutType getDiagramType() {
        return DiagramLayoutType.NETWORK_AREA_DIAGRAM;
    }

    @Override
    public NetworkAreaDiagramLayoutEntity toEntity() {
        return NetworkAreaDiagramLayoutEntity.builder()
            .diagramUuid(diagramUuid)
            .width(w)
            .height(h)
            .xPosition(x)
            .yPosition(y)
            .voltageLevelIds(voltageLevelIds)
            .depth(depth)
            .build();
    }
}
