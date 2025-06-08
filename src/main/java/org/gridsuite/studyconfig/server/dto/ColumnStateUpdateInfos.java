/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for updating column state (visibility and order)
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Schema(name = "ColumnStateUpdateDto", description = "Column state update information")
public record ColumnStateUpdateInfos(

        @NotNull(message = "Column UUID is mandatory")
        @Schema(description = "Column UUID")
        UUID columnId,

        @NotNull(message = "Visible state is mandatory")
        @Schema(description = "Column visibility state")
        Boolean visible,

        @NotNull(message = "Order is mandatory")
        @Schema(description = "New position in the column order (0-based index)")
        Integer order

) { }
