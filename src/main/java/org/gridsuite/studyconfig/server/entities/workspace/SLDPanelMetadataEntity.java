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
@DiscriminatorValue("SLD")
public class SLDPanelMetadataEntity extends AbstractPanelMetadataEntity {

    @Column(name = "diagram_id")
    private String diagramId;

    @Column(name = "parent_nad_panel_id")
    private UUID parentNadPanelId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sld_navigation_history", joinColumns = @JoinColumn(name = "metadata_id"))
    @Column(name = "voltage_level_id")
    @OrderColumn(name = "position")
    private List<String> sldNavigationHistory = new ArrayList<>();
}
