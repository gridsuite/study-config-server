/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Builder
@Schema(name = "GlobalFilterDto", description = "Global filter configuration")
public record GlobalFilterInfos(

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Generic filter ID")
    UUID uuid,

    @NotNull(message = "Filter type is mandatory")
    @Schema(description = "Filter type")
    String filterType,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Filter subtype")
    String filterSubtype,

    @NotNull(message = "Filter label is mandatory")
    @Schema(description = "Filter label")
    String label,

    @Schema(description = "Was filter recently applied")
    boolean recent,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Generic filter related equipment type")
    String equipmentType,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Generic filter path")
    String path,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Voltage level filter minimum range value")
    Integer minValue,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Voltage level filter maximum range value")
    Integer maxValue

) {
}
