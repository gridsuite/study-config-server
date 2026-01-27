/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.repositories;

import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, UUID> {

    @EntityGraph(attributePaths = {"panels"})
    @Override
    Optional<WorkspaceEntity> findById(UUID id);
}
