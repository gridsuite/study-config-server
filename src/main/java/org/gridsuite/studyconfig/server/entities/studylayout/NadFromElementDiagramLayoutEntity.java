package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_nad_from_element_diagram_layout_abstract"))
public class NadFromElementDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    String elementName;
    String elementType;
    UUID elementUuid;
}
