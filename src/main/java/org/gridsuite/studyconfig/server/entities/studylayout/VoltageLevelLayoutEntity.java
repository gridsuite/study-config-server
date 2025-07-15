package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.VoltageLevelDiagramLayout;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_voltage_level_layout_abstract"))
public class VoltageLevelLayoutEntity extends AbstractDiagramLayoutEntity {
    String voltageLevelId;

    public VoltageLevelDiagramLayout toDto() {
        return VoltageLevelDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toDto()
            )))
            .voltageLevelId(voltageLevelId)
            .build();
    }
}
