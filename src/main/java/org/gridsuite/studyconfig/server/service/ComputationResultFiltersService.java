/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ComputationResultFiltersEntity;
import org.gridsuite.studyconfig.server.entities.ComputationTypeFiltersEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationTypeFiltersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class ComputationResultFiltersService {
    private static final String COMPUTATION_FILTERS_NOT_FOUND = "not found";
    private final ComputationResultFiltersRepository computationResultFiltersRepository;
    private final ComputationTypeFiltersRepository computationTypeFiltersRepository;

    @Transactional
    public UUID createDefaultComputingResultFilters() {
        ComputationResultFiltersEntity root = new ComputationResultFiltersEntity();
        return computationResultFiltersRepository.saveAndFlush(root).getId();
    }

    @Transactional(readOnly = true)
    public List<GlobalFilterInfos> getComputingResultGlobalFilters(UUID rootId, String computationType) {
        ComputationResultFiltersEntity rootEntity = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        return rootEntity.getComputationResultFilter().stream().filter(type -> type.getComputationType().equals(computationType))
                .findFirst()
                .map(type -> type.getGlobalFilters().stream().map(SpreadsheetConfigMapper::toGlobalFilterDto).toList())
                .orElse(List.of());
    }

    @Transactional
    public void setGlobalFiltersForComputationResult(UUID rootId, String computationType, List<GlobalFilterInfos> globalFilters) {
        ComputationResultFiltersEntity root = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        ComputationTypeFiltersEntity typeEntity = root.getComputationResultFilter().stream()
                .filter(type -> computationType.equals(type.getComputationType()))
                .findFirst()
                .orElseGet(() -> {
                    ComputationTypeFiltersEntity entity = new ComputationTypeFiltersEntity();
                    entity.setComputationType(computationType);
                    root.getComputationResultFilter().add(entity);
                    return entity;
                });

        List<GlobalFilterEntity> newFilters = globalFilters.stream().map(SpreadsheetConfigMapper::toGlobalFilterEntity).toList();
        if (!typeEntity.getGlobalFilters().equals(newFilters)) {
            typeEntity.getGlobalFilters().clear();
            typeEntity.getGlobalFilters().addAll(newFilters);
        }
        computationResultFiltersRepository.save(root);
    }
}
