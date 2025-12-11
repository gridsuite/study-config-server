/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Schema(name = "ColumnFilterDto", description = "Column filter configuration")
public record ColumnFilterInfos(

    @Schema(description = "Column UUID")
    UUID uuid,

    @Schema(description = "Column id")
    String columnId,

    @Schema(description = "Filter data type")
    String filterDataType,

    @Schema(description = "Filter type")
    String filterType,

    @Schema(description = "Filter value")
    String filterValue,

    @Schema(description = "Filter tolerance for numeric comparisons")
    Double filterTolerance
) { }
