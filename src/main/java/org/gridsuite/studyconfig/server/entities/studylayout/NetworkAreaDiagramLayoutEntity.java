package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NetworkAreaDiagramLayout;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_network_area_diagram_layout_abstract"))
public class NetworkAreaDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    @ElementCollection()
    @CollectionTable(
        name = "network_area_diagram_voltage_level_ids",
        joinColumns = @JoinColumn(name = "network_area_diagram_layout_id"),
        foreignKey = @ForeignKey(name = "fk_network_area_diagram_voltage_level_ids")
    )
    List<String> voltageLevelIds;

    Integer depth;

    public NetworkAreaDiagramLayout toDto() {
        return NetworkAreaDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toDto()
            )))
            .depth(depth)
            .voltageLevelIds(voltageLevelIds != null ? List.copyOf(voltageLevelIds) : null)
            .build();
    }
}
