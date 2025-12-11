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
@Table(name = "global_filter")
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

    public GlobalFilterEntity copy() {
        return GlobalFilterEntity.builder()
            .filterType(getFilterType())
            .filterSubtype(getFilterSubtype())
            .label(getLabel())
            .uuid(getUuid())
            .equipmentType(getEquipmentType())
            .recent(isRecent())
            .path(getPath())
            .build();
    }
}
