/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Data
@AllArgsConstructor
@Builder
@Jacksonized
@Schema(name = "CustomColumnDto", description = "Custom column configuration")
public class CustomColumnDto {

    @Schema(description = "Custom column ID")
    UUID id;

    @NotNull(message = "Column name is mandatory")
    @Schema(description = "Column name")
    String name;

    @Schema(description = "Column formula")
    String formula;
}
