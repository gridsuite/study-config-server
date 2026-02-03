/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.gridsuite.studyconfig.server.constants.SheetType;

import java.util.List;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Schema(name = "SpreadsheetConfigDto", description = "Spreadsheet configuration")
public record SpreadsheetConfigInfos(

    @Schema(description = "Spreadsheet configuration ID")
    UUID id,

    @Schema(description = "Spreadsheet configuration name")
    String name,

    @NotNull(message = "Sheet type is mandatory")
    @Schema(description = "Spreadsheet type")
    SheetType sheetType,

    @Schema(description = "Columns")
    List<SpreadsheetColumnInfos> columns,

    @Schema(description = "Global filters")
    List<GlobalFilterInfos> globalFilters,

    @Schema(description = "List of node aliases")
    List<String> nodeAliases,

    @Schema(description = "Sort configuration")
    SortConfig sortConfig
) { }
