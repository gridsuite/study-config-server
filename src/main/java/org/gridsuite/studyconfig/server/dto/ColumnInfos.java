/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import java.util.UUID;

import org.gridsuite.studyconfig.server.constants.ColumnType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Schema(name = "ColumnDto", description = "Column configuration")
public record ColumnInfos(

    @Schema(description = "Column UUID")
    UUID uuid,

    @NotNull(message = "Column name is mandatory")
    @Schema(description = "Column name")
    String name,

    @Schema(description = "Column type")
    ColumnType type,

    @Schema(description = "Column precision")
    Integer precision,

    @Schema(description = "Column formula")
    String formula,

    @Schema(description = "Column dependencies")
    String dependencies,

    @Schema(description = "Column id")
    String id,

    @Schema(description = "Filter data type")
    String filterDataType,

    @Schema(description = "Filter type")
    String filterType,

    @Schema(description = "Filter value")
    String filterValue,

    @Schema(description = "Filter tolerance for numeric comparisons")
    Double filterTolerance,

    @Schema(description = "Column visibility", defaultValue = "true")
    boolean visible
) { }
