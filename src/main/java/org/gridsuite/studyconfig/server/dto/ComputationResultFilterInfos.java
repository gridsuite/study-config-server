/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.gridsuite.studyconfig.server.constants.ComputationType;

import java.util.List;
import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Schema(name = "ComputationResultFilter")
public record ComputationResultFilterInfos(

        @Schema(description = "Filter UUID")
        UUID id,

        @NotNull
        @Schema(description = "Computation type")
        ComputationType computationType,

        @NotNull
        @Schema(description = "Map of computationType → list of columns filters")
        List<ColumnsFiltersInfos> columnsFilters,

        @NotNull
        @Schema(description = "Global filters")
        List<GlobalFilterInfos> globalFilters
) { }
