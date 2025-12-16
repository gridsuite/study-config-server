/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.dto.workspace;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NADPanelMetadataInfos.class, name = "NAD"),
    @JsonSubTypes.Type(value = SLDPanelMetadataInfos.class, name = "SLD")
})
@Schema(name = "PanelMetadataDto", description = "Panel metadata")
public sealed interface PanelMetadataInfos permits NADPanelMetadataInfos, SLDPanelMetadataInfos {
}
