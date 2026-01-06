/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "NADPanelDto", description = "NAD panel configuration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NADPanelInfos extends PanelInfos {
    @Schema(description = "NAD configuration UUID")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private UUID nadConfigUuid;

    @Schema(description = "Filter UUID")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private UUID filterUuid;

    @Schema(description = "Current filter UUID")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private UUID currentFilterUuid;

    @Schema(description = "Saved workspace configuration UUID")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private UUID savedWorkspaceConfigUuid;

    @Schema(description = "Voltage level IDs to omit")
    private List<String> voltageLevelToOmitIds;

    @Schema(description = "Navigation history")
    private List<String> navigationHistory;
}
