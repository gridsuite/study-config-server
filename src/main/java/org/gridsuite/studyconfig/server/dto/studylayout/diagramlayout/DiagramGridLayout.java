package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class DiagramGridLayout {
    Integer w;
    Integer h;
    Integer x;
    Integer y;
}
