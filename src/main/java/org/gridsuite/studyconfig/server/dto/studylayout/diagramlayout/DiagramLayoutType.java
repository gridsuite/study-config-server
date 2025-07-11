package org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout;

import lombok.Getter;

@Getter
public enum DiagramLayoutType {
    NETWORK_AREA_DIAGRAM("network-area-diagram"),
    SUBSTATION("substation"),
    VOLTAGE_LEVEL("voltage-level");

    private final String label;

    DiagramLayoutType(String label) {
        this.label = label;
    }

}
