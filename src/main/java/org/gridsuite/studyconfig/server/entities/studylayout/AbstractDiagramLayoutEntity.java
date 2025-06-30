package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.AbstractDiagramLayout;

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
    Integer width;
    Integer height;
    Integer xPosition;
    Integer yPosition;

    public abstract AbstractDiagramLayout toDto();
}
