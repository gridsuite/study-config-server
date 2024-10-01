/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.gridsuite.spreadsheetconfig.server.constants.SheetType;

import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Schema(name = "MetadataDto", description = "Spreadsheet configuration metadata")
public record MetadataInfos(

    @Schema(description = "Spreadsheet configuration ID")
    UUID id,

    @Schema(description = "Spreadsheet type")
    SheetType sheetType

) { }
