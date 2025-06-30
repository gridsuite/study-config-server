package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.AbstractDiagramLayoutEntity;

import java.util.UUID;

@SuperBuilder
public abstract class AbstractDiagramLayout {
    UUID diagramUuid;

    Integer w;
    Integer h;
    Integer x;
    Integer y;

    @JsonIgnore
    public abstract DiagramLayoutType getDiagramType();

    public String getType() {
        return getDiagramType().getLabel();
    }

    public abstract AbstractDiagramLayoutEntity toEntity();
}
