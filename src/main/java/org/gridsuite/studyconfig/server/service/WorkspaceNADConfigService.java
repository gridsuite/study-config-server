/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.entities.workspace.NADPanelEntity;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WorkspaceNADConfigService {

    private final SingleLineDiagramService singleLineDiagramService;

    public void duplicateNadConfigs(WorkspaceEntity workspace) {
        workspace.getNadPanels().forEach(nadPanel -> {
            UUID nadConfigUuid = nadPanel.getCurrentNadConfigUuid();
            if (nadConfigUuid != null) {
                UUID newConfigUuid = singleLineDiagramService.duplicateNadConfig(nadConfigUuid);
                nadPanel.setCurrentNadConfigUuid(newConfigUuid);
            }
        });
    }

    public void deleteNadConfigs(Stream<NADPanelEntity> nadPanels) {
        List<UUID> nadConfigUuids = nadPanels
            .map(NADPanelEntity::getCurrentNadConfigUuid)
            .filter(Objects::nonNull)
            .toList();
        if (!nadConfigUuids.isEmpty()) {
            singleLineDiagramService.deleteNadConfigs(nadConfigUuids);
        }
    }

    public UUID saveNadConfig(Map<String, Object> nadConfigData) {
        return singleLineDiagramService.createOrUpdateNadConfig(nadConfigData);
    }

    public void deleteNadConfig(UUID nadConfigUuid) {
        singleLineDiagramService.deleteNadConfig(nadConfigUuid);
    }
}
