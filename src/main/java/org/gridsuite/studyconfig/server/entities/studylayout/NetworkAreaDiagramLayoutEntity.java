/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.studylayout;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "fk_network_area_diagram_layout_abstract"))
public class NetworkAreaDiagramLayoutEntity extends AbstractDiagramLayoutEntity {
    @Column(name = "filter_uuid")
    UUID filterUuid;

    @Column(name = "nad_config_uuid")
    UUID nadConfigUuid;

    @ElementCollection
    @CollectionTable(
        name = "network_area_diagram_voltage_level_ids",
        joinColumns = @JoinColumn(name = "network_area_diagram_layout_id"),
        foreignKey = @ForeignKey(name = "fk_network_area_diagram_voltage_level_ids")
    )
    List<String> voltageLevelIds;

    @ElementCollection
    @CollectionTable(
        name = "network_area_diagram_voltage_level_to_expand_ids",
        joinColumns = @JoinColumn(name = "network_area_diagram_layout_id"),
        foreignKey = @ForeignKey(name = "fk_network_area_diagram_voltage_level_to_expand_ids")
    )
    List<String> voltageLevelToExpandIds;

    @ElementCollection
    @CollectionTable(
        name = "network_area_diagram_voltage_level_to_omit_ids",
        joinColumns = @JoinColumn(name = "network_area_diagram_layout_id"),
        foreignKey = @ForeignKey(name = "fk_network_area_diagram_voltage_level_to_omit_ids")
    )
    List<String> voltageLevelToOmitIds;

    @Column(name = "depth")
    Integer depth;
}
