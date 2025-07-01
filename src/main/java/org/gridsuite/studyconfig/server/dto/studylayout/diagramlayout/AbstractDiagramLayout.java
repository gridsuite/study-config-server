package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.studylayout.AbstractDiagramLayoutEntity;

import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SubstationDiagramLayout.class, name = "substation"),
    @JsonSubTypes.Type(value = VoltageLevelDiagramLayout.class, name = "voltage-level"),
    @JsonSubTypes.Type(value = NetworkAreaDiagramLayout.class, name = "network-area-diagram"),
})
public abstract class AbstractDiagramLayout {
    UUID diagramUuid;

    Integer w;
    Integer h;
    Integer x;
    Integer y;

//    String type = getDiagramType().getLabel();

    @JsonIgnore
    public abstract DiagramLayoutType getDiagramType();

    public abstract AbstractDiagramLayoutEntity toEntity();
}
