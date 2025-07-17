package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_network_area_diagram_layout_abstract"))
public class NetworkAreaDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "network_area_diagram_voltage_level_ids",
        joinColumns = @JoinColumn(name = "network_area_diagram_layout_id"),
        foreignKey = @ForeignKey(name = "fk_network_area_diagram_voltage_level_ids")
    )
    List<String> voltageLevelIds;

    Integer depth;
}
