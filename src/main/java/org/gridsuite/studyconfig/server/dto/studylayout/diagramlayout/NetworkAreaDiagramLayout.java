package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NetworkAreaDiagramLayout extends AbstractDiagramLayout {
    List<String> voltageLevelIds;
    Integer depth;
}
