/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "SLDPanelMetadataDto", description = "SLD panel metadata")
public record SLDPanelMetadataInfos(
    @Schema(description = "Diagram ID (voltage level or substation)")
    String diagramId,

    @Schema(description = "Parent NAD panel ID (if this SLD is associated with a NAD)")
    UUID parentNadPanelId,

    @Schema(description = "Navigation history (null for substation SLDs)")
    List<String> sldNavigationHistory
) implements PanelMetadataInfos {
}
