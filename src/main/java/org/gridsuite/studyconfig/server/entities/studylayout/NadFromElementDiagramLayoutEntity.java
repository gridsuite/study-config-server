package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NadFromElementDiagramLayout;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_nad_from_element_diagram_layout_abstract"))
public class NadFromElementDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    String elementName;
    String elementType;
    UUID elementUuid;

    @Override
    public NadFromElementDiagramLayout toDto() {
        return NadFromElementDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .gridLayout(gridLayout.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toDto()
            )))
            .elementName(elementName)
            .elementType(elementType)
            .elementUuid(elementUuid)
            .build();
    }
}
