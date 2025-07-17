package org.gridsuite.studyconfig.server.dto.studylayout;

import lombok.*;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.AbstractDiagramLayout;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudyLayout {
    List<AbstractDiagramLayout> diagramLayoutParams;
}
