package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Getter
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_substation_diagram_layout_abstract"))
public class SubstationDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    String substationId;
}
