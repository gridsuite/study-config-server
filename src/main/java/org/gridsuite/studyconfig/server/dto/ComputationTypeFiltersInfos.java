/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */

@Schema(name = "ComputationTypeFiltersInfos", description = "Computation type Filters")
public record ComputationTypeFiltersInfos(

        @Schema(description = "Computation type")
        String computationType,

        @NotNull
        @Schema(description = "Global filters")
        List<GlobalFilterInfos> globalFilters,

        @Schema(description = "Subtypes of the computation type with their columns filters")
        List<ComputationSubTypeFilterInfos> computationSubTypeFilterInfos
) { }
