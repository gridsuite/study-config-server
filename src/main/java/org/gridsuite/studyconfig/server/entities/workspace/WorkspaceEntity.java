/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.workspace;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workspace")
public class WorkspaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", foreignKey = @ForeignKey(name = "fk_workspace"))
    @OrderColumn(name = "panel_order")
    private List<PanelEntity> panels = new ArrayList<>();

    public WorkspaceEntity(WorkspaceInfos dto) {
        id = dto.id();
        name = dto.name();
        if (dto.panels() != null) {
            setPanels(dto.panels().stream()
                .map(PanelEntity::toEntity)
                .toList());
        }
    }

    public WorkspaceInfos toDto() {
        return new WorkspaceInfos(
            getId(),
            getName(),
            getPanels().stream()
                .map(PanelEntity::toDto)
                .toList()
        );
    }

    public PanelEntity getPanel(UUID uuid) {
        return getPanels().stream()
            .filter(p -> p.getId().equals(uuid))
            .findFirst()
            .orElse(null);
    }
}
