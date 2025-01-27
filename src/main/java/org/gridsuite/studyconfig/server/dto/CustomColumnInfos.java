/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Schema(name = "CustomColumnDto", description = "Custom column configuration")
public record CustomColumnInfos(

    @NotNull(message = "Column name is mandatory")
    @Schema(description = "Column name")
    String name,

    @Schema(description = "Column formula")
    String formula,

    @Schema(description = "Column dependencies")
    String dependencies
) { }
