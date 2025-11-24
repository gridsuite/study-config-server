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
    // Maximum length for the name field
    private static final int MAX_NAME_LENGTH = 255;

    @Column(name = "original_nad_config_uuid")
    UUID originalNadConfigUuid;

    @Column(name = "current_nad_config_uuid")
    UUID currentNadConfigUuid;

    @Column(name = "original_filter_uuid")
    UUID originalFilterUuid;

    @Column(name = "current_filter_uuid")
    UUID currentFilterUuid;

    @Column(name = "name")
    String name;

    /**
     * Ensures that the name does not exceed the maximum length.
     * This method is called before persisting or updating the entity.
     */
    @PrePersist
    @PreUpdate
    private void truncateName() {
        if (name != null && name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH);
        }
    }

}
