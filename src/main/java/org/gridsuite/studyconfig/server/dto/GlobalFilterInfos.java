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
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Schema(name = "GlobalFilterDto", description = "Global filter configuration")
public record GlobalFilterInfos(

    @Schema(description = "Global filter UUID")
    UUID id,

    @Schema(description = "Generic filter ID")
    UUID uuid,

    @NotNull(message = "Filter type is mandatory")
    @Schema(description = "Filter type")
    String filterType,

    @Schema(description = "Filter subtype")
    String filterSubtype,

    @NotNull(message = "Filter label is mandatory")
    @Schema(description = "Filter label")
    String label,

    @Schema(description = "Was filter recently applied")
    boolean recent,

    @Schema(description = "Generic filter related equipment type")
    String equipmentType,

    @Schema(description = "Generic filter path")
    String path

) {
}
