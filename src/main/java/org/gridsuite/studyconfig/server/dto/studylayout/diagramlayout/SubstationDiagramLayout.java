package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.SubstationDiagramLayoutEntity;

import java.util.Map;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SubstationDiagramLayout extends AbstractDiagramLayout {
    String substationId;

    @Override
    public SubstationDiagramLayoutEntity toEntity() {
        return SubstationDiagramLayoutEntity.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toEntity()
            )))
            .substationId(substationId)
            .build();
    }
}
