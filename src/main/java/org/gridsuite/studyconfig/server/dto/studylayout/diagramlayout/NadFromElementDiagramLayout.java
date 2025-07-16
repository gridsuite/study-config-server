package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.NadFromElementDiagramLayoutEntity;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NadFromElementDiagramLayout extends AbstractDiagramLayout {
    String elementName;
    String elementType;
    UUID elementUuid;

    @Override
    public NadFromElementDiagramLayoutEntity toEntity() {
        return NadFromElementDiagramLayoutEntity.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toEntity()
            )))
            .elementName(elementName)
            .elementType(elementType)
            .elementUuid(elementUuid)
            .build();
    }
}
