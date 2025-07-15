package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.SubstationDiagramLayout;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_substation_diagram_layout_abstract"))
public class SubstationDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    String substationId;

    public SubstationDiagramLayout toDto() {
        return SubstationDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toDto()
            )))
            .substationId(substationId)
            .build();
    }
}
