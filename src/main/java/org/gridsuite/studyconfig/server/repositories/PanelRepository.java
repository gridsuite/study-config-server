/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.repositories;

import org.gridsuite.studyconfig.server.entities.workspace.PanelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PanelRepository extends JpaRepository<PanelEntity, UUID> {
    @Query(value = "SELECT count(*) from panel WHERE workspace_id = :id", nativeQuery = true)
    int countByWorkspaceId(UUID id);
}
