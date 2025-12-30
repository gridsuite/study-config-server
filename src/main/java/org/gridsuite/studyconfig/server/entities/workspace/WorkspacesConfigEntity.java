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
import org.gridsuite.studyconfig.server.dto.workspace.WorkspacesConfigInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workspaces_config")
public class WorkspacesConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaces_config_id", foreignKey = @ForeignKey(name = "fk_workspaces_config"))
    @OrderColumn(name = "workspace_order")
    private List<WorkspaceEntity> workspaces = new ArrayList<>();

    public WorkspacesConfigEntity(WorkspacesConfigInfos dto) {
        id = dto.id();
        if (dto.workspaces() != null) {
            setWorkspaces(dto.workspaces().stream()
                .map(WorkspaceEntity::new)
                .toList());
        }
    }

    public WorkspacesConfigInfos toDto() {
        return new WorkspacesConfigInfos(
            getId(),
            getWorkspaces().stream()
                .map(WorkspaceEntity::toDto)
                .toList()
        );
    }
}
