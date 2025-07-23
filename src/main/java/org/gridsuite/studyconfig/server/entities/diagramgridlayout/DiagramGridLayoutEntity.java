/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.diagramgridlayout;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.AbstractDiagramLayoutEntity;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class DiagramGridLayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID uuid;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "diagram_grid_layout_id", foreignKey = @ForeignKey(name = "fk_diagram_layout_grid_layout"))
    List<AbstractDiagramLayoutEntity> diagramLayouts;

    public void replaceAllDiagramLayouts(List<AbstractDiagramLayoutEntity> diagramLayouts) {
        this.diagramLayouts.clear();
        this.diagramLayouts.addAll(diagramLayouts);
    }
}
