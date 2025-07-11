package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.NetworkAreaDiagramLayoutEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toEntity()
            )))
            .voltageLevelIds(voltageLevelIds)
            .depth(depth)
            .build();
    }
}
