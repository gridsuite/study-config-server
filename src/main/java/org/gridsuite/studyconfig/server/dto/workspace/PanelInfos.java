/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gridsuite.studyconfig.server.entities.workspace.PanelType;

import java.util.UUID;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NADPanelInfos.class, name = "NAD"),
    @JsonSubTypes.Type(value = SLDPanelInfos.class, name = "SLD_VOLTAGE_LEVEL"),
    @JsonSubTypes.Type(value = SLDPanelInfos.class, name = "SLD_SUBSTATION"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "TREE"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "SPREADSHEET"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "LOGS"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "RESULTS"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "PARAMETERS"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "MAP"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "MODIFICATIONS"),
    @JsonSubTypes.Type(value = PanelInfos.class, name = "EVENT_SCENARIO")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PanelDto", description = "Panel configuration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PanelInfos {
    @Schema(description = "Panel ID")
    private UUID id;

    @Schema(description = "Panel type")
    private PanelType type;

    @Schema(description = "Panel title")
    private String title;

    @Schema(description = "Panel position")
    private PanelPositionInfos position;

    @Schema(description = "Panel size")
    private PanelSizeInfos size;

    @Schema(description = "Is minimized (to dock for NAD/SLD, hidden for toggle panels)")
    private boolean isMinimized;

    @Schema(description = "Is maximized")
    private boolean isMaximized;

    @Schema(description = "Is pinned")
    private boolean isPinned;

    @Schema(description = "Restore position")
    private PanelPositionInfos restorePosition;

    @Schema(description = "Restore size")
    private PanelSizeInfos restoreSize;
}
