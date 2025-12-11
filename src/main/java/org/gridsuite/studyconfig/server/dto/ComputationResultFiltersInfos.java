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
import java.util.Map;
import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */

@Schema(name = "ComputationResultFiltersInfos", description = "Computation Result Filters")
public record ComputationResultFiltersInfos(
        @Schema(description = "ID of the computation result filter")
        UUID id,

        @NotNull
        @Schema(description = "map of computation result filter by computation type")
        Map<ComputationType, List<ComputationResultFilterInfos>> computationResultFilters
) {

}
