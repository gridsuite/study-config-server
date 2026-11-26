/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@Schema(name = "SaveNadConfigRequest", description = "The whole definition of the NAD a panel draws")
public record SaveNadConfigRequest(
    @Schema(description = "Panel title")
    String title,

    @Schema(description = "NAD config, null when the panel has no layout of its own")
    Map<String, Object> nadConfig,

    @Schema(description = "Source NAD config")
    UUID nadConfigUuid,

    @Schema(description = "Filter the NAD shows, what it matches on each node")
    UUID filterUuid,

    @Schema(description = "Filter name, kept to be shown once the filter is deleted")
    String filterName,

    @Schema(description = "Hidden voltage levels")
    List<String> voltageLevelToOmitIds
) {
}
