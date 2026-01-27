/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PanelSizeDto", description = "Panel size (relative 0-1)")
public record PanelSizeInfos(
    @Schema(description = "Width (0-1)")
    double width,

    @Schema(description = "Height (0-1)")
    double height
) {
}
