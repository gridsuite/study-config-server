/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "network_visualization_params")
public class NetworkVisualizationParamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "line_full_path")
    private Boolean lineFullPath = true;

    @Column(name = "line_parallel_path")
    private Boolean lineParallelPath = true;

    @Column(name = "line_flow_mode")
    private String lineFlowMode = "feeders";

    @Column(name = "map_manual_refresh")
    private Boolean mapManualRefresh = true;

    @Column(name = "map_basemap")
    private String mapBaseMap = "mapbox";

    @Column(name = "diagonal_label")
    private Boolean diagonalLabel = true;

    @Column(name = "center_label")
    private Boolean centerLabel = false;

    @Column(name = "substation_layout")
    private String substationLayout = "horizontal";

    @Column(name = "component_library")
    private String componentLibrary = "";

    @Column(name = "nad_generation_mode")
    private String nadGenerationMode = "GEOGRAPHICAL_COORDINATES";

    @Column(name = "nad_config_uuid")
    private UUID nadConfigUuid;
}
