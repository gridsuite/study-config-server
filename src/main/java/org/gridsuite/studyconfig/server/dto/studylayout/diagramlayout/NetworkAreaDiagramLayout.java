package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.NetworkAreaDiagramLayoutEntity;

import java.util.List;

@SuperBuilder
public class NetworkAreaDiagramLayout extends AbstractDiagramLayout {
    List<String> voltageLevelIds;
    Integer depth;

    @Override
    public DiagramLayoutType getDiagramType() {
        return DiagramLayoutType.NETWORK_AREA;
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
