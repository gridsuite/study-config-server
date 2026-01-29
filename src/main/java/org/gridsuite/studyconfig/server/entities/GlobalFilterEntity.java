/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Entity
@Table(name = "spreadsheet_global_filter", indexes = {
    @Index(name = "idx_global_filter_spreadsheet_config_id", columnList = "spreadsheet_config_id"),
    @Index(name = "idx_global_filter_computation_type_filters_id", columnList = "computation_type_filters_id")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalFilterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "filter_type", nullable = false)
    private String filterType;

    @Column(name = "filter_subtype")
    private String filterSubtype;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "recent")
    private boolean recent;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "equipment_type")
    private String equipmentType;

    @Column(name = "path")
    private String path;

    @Column(name = "min_value")
    private Integer minValue;

    @Column(name = "max_value")
    private Integer maxValue;

    public GlobalFilterEntity copy() {
        return GlobalFilterEntity.builder()
            .filterType(getFilterType())
            .filterSubtype(getFilterSubtype())
            .label(getLabel())
            .uuid(getUuid())
            .equipmentType(getEquipmentType())
            .recent(isRecent())
            .path(getPath())
            .minValue(getMinValue())
            .maxValue(getMaxValue())
            .build();
    }
}
