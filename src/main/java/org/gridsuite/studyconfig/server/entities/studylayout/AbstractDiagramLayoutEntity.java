package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.AbstractDiagramLayout;

import java.util.Map;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDiagramLayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    UUID diagramUuid;

    @ElementCollection
    @CollectionTable(foreignKey = @ForeignKey(name = "fk_grid_layout_abstract_diagram"))
    @MapKeyColumn(name = "grid_layout_key")
    Map<String, DiagramGridLayoutEntity> gridLayout;

    public abstract AbstractDiagramLayout toDto();
}
