/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@Schema(name = "SpreadsheetConfigCollectionDto", description = "Spreadsheet configuration collection")
public record SpreadsheetConfigCollectionInfos(

    @Schema(description = "Spreadsheet configuration collection ID")
    UUID id,

    @Schema(description = "List of spreadsheet configurations")
    List<SpreadsheetConfigInfos> spreadsheetConfigs
) {
}
