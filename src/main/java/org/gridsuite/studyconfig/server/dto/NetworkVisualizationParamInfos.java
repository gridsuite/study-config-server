/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
@Schema(name = "NetworkVisualizationParamDto", description = "Network visualization parameters")
public record NetworkVisualizationParamInfos(

    @Schema(description = "Parameters ID")
    UUID id,

    @Schema(description = "Map parameters")
    MapParamInfos mapParameters,

    @Schema(description = "Single line diagram parameters")
    SingleLineDiagramParamInfos singleLineDiagramParameters,

    @Schema(description = "Network area diagram parameters")
    NetworkAreaDiagramParamInfos networkAreaDiagramParameters
) { }
