/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.gridsuite.studyconfig.server.entities.workspace.PanelEntity;

import java.util.UUID;

@Schema(name = "PanelDto", description = "Panel configuration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PanelInfos(
    @Schema(description = "Panel ID")
    UUID id,

    @Schema(description = "Panel type")
    PanelEntity.PanelType type,

    @Schema(description = "Panel title")
    String title,

    @Schema(description = "Panel position")
    PanelPositionInfos position,

    @Schema(description = "Panel size")
    PanelSizeInfos size,

    @Schema(description = "Z-index")
    int zIndex,

    @Schema(description = "Order index for panel ordering")
    int orderIndex,

    @Schema(description = "Is minimized")
    boolean isMinimized,

    @Schema(description = "Is maximized")
    boolean isMaximized,

    @Schema(description = "Is pinned")
    boolean isPinned,

    @Schema(description = "Is closed")
    boolean isClosed,

    @Schema(description = "Restore position")
    PanelPositionInfos restorePosition,

    @Schema(description = "Restore size")
    PanelSizeInfos restoreSize,

    @Schema(description = "Panel metadata specific to panel type")
    PanelMetadataInfos metadata
) {
}
