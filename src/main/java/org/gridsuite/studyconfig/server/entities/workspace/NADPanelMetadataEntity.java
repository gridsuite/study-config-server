/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.workspace;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("NAD")
public class NADPanelMetadataEntity extends AbstractPanelMetadataEntity {

    @Column(name = "nad_config_uuid")
    private UUID nadConfigUuid;

    @Column(name = "filter_uuid")
    private UUID filterUuid;

    @Column(name = "current_filter_uuid")
    private UUID currentFilterUuid;

    @Column(name = "saved_workspace_config_uuid")
    private UUID savedWorkspaceConfigUuid;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nad_voltage_level_to_omit", joinColumns = @JoinColumn(name = "metadata_id"))
    @Column(name = "voltage_level_id")
    private List<String> voltageLevelToOmitIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nad_navigation_history", joinColumns = @JoinColumn(name = "metadata_id"))
    @Column(name = "voltage_level_id")
    @OrderColumn(name = "position")
    private List<String> nadNavigationHistory = new ArrayList<>();
}
