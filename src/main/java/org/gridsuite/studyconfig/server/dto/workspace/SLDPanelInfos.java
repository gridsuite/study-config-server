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
@Schema(name = "SLDPanelDto", description = "SLD panel configuration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SLDPanelInfos extends PanelInfos {
    @Schema(description = "Equipment ID (voltage level or substation)")
    private String equipmentId;

    @Schema(description = "Parent NAD panel ID (if this SLD is associated with a NAD)")
    private UUID parentNadPanelId;

    @Schema(description = "Navigation history")
    private List<String> navigationHistory;
}
