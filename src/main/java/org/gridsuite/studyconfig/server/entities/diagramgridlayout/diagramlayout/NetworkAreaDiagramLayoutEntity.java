/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_network_area_diagram_layout_abstract"))
public class NetworkAreaDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    @Column(name = "original_nad_config_uuid")
    UUID originalNadConfigUuid;

    @Column(name = "current_nad_config_uuid")
    UUID currentNadConfigUuid;

    @Column(name = "filter_uuid")
    UUID filterUuid;

    @Column(name = "name")
    String name;
}
