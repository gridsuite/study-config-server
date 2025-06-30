package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NetworkAreaDiagramLayout;

import java.util.List;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkAreaDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    List<String> voltageLevelIds;
    Integer depth;

    public NetworkAreaDiagramLayout toDto() {
        return NetworkAreaDiagramLayout.builder()
            .diagramUuid(diagramUuid)
            .w(width)
            .h(height)
            .x(xPosition)
            .y(yPosition)
            .depth(depth)
            .voltageLevelIds(voltageLevelIds)
            .build();
    }
}
