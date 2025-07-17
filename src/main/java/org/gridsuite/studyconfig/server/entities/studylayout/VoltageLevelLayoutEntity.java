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
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_voltage_level_layout_abstract"))
public class VoltageLevelLayoutEntity extends AbstractDiagramLayoutEntity {
    String voltageLevelId;
}
