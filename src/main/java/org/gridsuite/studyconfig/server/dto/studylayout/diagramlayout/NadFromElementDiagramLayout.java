package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NadFromElementDiagramLayout extends AbstractDiagramLayout {
    String elementName;
    String elementType;
    UUID elementUuid;
}
