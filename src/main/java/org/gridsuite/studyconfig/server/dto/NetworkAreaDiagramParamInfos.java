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
@Schema(name = "NetworkAreaDiagramParamDto", description = "Network area diagram parameters")
public record NetworkAreaDiagramParamInfos(

    @Schema(description = "Initialize with geographical data")
    Boolean initNadWithGeoData
) { }
