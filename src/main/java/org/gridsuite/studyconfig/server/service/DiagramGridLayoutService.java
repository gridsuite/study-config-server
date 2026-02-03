/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import org.gridsuite.studyconfig.server.dto.diagramgridlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.DiagramGridLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.DiagramGridLayoutRepository;
import org.gridsuite.studyconfig.server.mapper.DiagramGridLayoutMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class DiagramGridLayoutService {
    private final DiagramGridLayoutRepository diagramGridLayoutRepository;

    public DiagramGridLayoutService(DiagramGridLayoutRepository diagramGridLayoutRepository) {
        this.diagramGridLayoutRepository = diagramGridLayoutRepository;
    }

    @Transactional(readOnly = true)
    public DiagramGridLayout getByDiagramGridLayoutUuid(UUID diagramGridLayoutUuid) {
        DiagramGridLayoutEntity entity = diagramGridLayoutRepository
                .findById(diagramGridLayoutUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diagram grid layout not found with columnId: " + diagramGridLayoutUuid));

        return DiagramGridLayoutMapper.toDto(entity);
    }

    @Transactional
    public void deleteDiagramGridLayout(UUID diagramGridLayoutUuid) {
        diagramGridLayoutRepository.deleteById(diagramGridLayoutUuid);
    }

    @Transactional
    public UUID saveDiagramGridLayout(DiagramGridLayout diagramGridLayout) {
        DiagramGridLayoutEntity diagramGridLayoutEntity = diagramGridLayoutRepository.save(DiagramGridLayoutMapper.toEntity(diagramGridLayout));

        return diagramGridLayoutEntity.getUuid();
    }

    @Transactional
    public void updateDiagramGridLayout(UUID diagramGridLayoutUuid, DiagramGridLayout diagramGridLayout) {
        DiagramGridLayoutEntity diagramGridLayoutEntity = diagramGridLayoutRepository.findById(diagramGridLayoutUuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diagram grid layout not found with columnId: " + diagramGridLayoutUuid));

        diagramGridLayoutEntity.replaceAllDiagramLayouts(diagramGridLayout.getDiagramLayouts().stream()
                .map(DiagramGridLayoutMapper::toDiagramLayoutEntity)
                .toList());

        diagramGridLayoutRepository.save(diagramGridLayoutEntity);
    }
}
