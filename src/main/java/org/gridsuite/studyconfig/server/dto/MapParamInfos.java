/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
@Schema(name = "MapParamDto", description = "Map parameters")
public record MapParamInfos(

    @Schema(description = "Line full path")
    Boolean lineFullPath,

    @Schema(description = "Spread overlapping lines")
    Boolean lineParallelPath,

    @Schema(description = "Line flow mode")
    String lineFlowMode,

    @Schema(description = "Line color mode")
    String lineFlowColorMode,

    @Schema(description = "Line overloads warning threshold")
    Integer lineFlowAlertThreshold,

    @Schema(description = "Manual update of geographical view")
    Boolean mapManualRefresh,

    @Schema(description = "Basemap")
    String mapBaseMap
) { }
