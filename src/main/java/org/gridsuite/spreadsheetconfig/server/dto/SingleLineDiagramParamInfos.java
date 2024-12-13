/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
@Schema(name = "SingleLineDiagramParamDto", description = "Single line diagram parameters")
public record SingleLineDiagramParamInfos(

    @Schema(description = "Display name diagonally")
    Boolean diagonalLabel,

    @Schema(description = "Center name")
    Boolean centerLabel,

    @Schema(description = "Substation diagram layout")
    String substationLayout,

    @Schema(description = "Component library selection")
    String componentLibrary
) { }
