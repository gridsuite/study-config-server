/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.ColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.ComputationResultFiltersInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.ComputationSubTypeFiltersEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultFiltersEntity;
import org.gridsuite.studyconfig.server.entities.ComputationTypeFiltersEntity;
import org.gridsuite.studyconfig.server.mapper.CommonFiltersMapper;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.repositories.ComputationSubTypeFiltersRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationTypeFiltersRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class ComputationResultFiltersService {

    private static final String COMPUTATION_FILTERS_NOT_FOUND = "not found";
    private final ComputationResultFiltersRepository computationResultFiltersRepository;
    private final ComputationTypeFiltersRepository computationTypeFiltersRepository;
    private final ComputationSubTypeFiltersRepository computationSubTypeFiltersRepository;

    @Transactional
    public UUID createDefaultComputingResultFilters() {
        ComputationResultFiltersEntity root = new ComputationResultFiltersEntity();
        return computationResultFiltersRepository.saveAndFlush(root).getId();
    }

    @Transactional(readOnly = true)
    public ComputationResultFiltersInfos getComputingResultFilters(UUID rootId) {
        ComputationResultFiltersEntity entity = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        return ComputationResultFiltersMapper.toDto(entity);
    }

    @Transactional
    public void setGlobalFiltersForComputationResult(UUID rootId, String computationType, List<GlobalFilterInfos> globalFilters) {
        ComputationTypeFiltersEntity typeEntity = findTypeEntity(rootId, computationType);
        typeEntity.getGlobalFilters().clear();
        typeEntity.getGlobalFilters().addAll(globalFilters.stream().map(CommonFiltersMapper::toGlobalFilterEntity).toList());
        computationTypeFiltersRepository.save(typeEntity);
    }

    @Transactional
    public void updateColumn(UUID rootId, String computationSubType, ColumnFilterInfos columns) {
        ComputationResultFiltersEntity root = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException("Sub-type not found: " + rootId));

        ComputationSubTypeFiltersEntity subTypeEntity =
                root.getComputationResultFilter().stream()
                        .flatMap(type -> type.getComputationSubTypeResultFilter().stream())
                        .filter(subType ->
                                subType.getComputationSubType().equals(computationSubType))
                        .findFirst()
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Computation sub-type not found: " + computationSubType));

        ColumnFilterEntity updatedColumn = CommonFiltersMapper.toColumnFilterEntity(columns);

        subTypeEntity.getColumns().removeIf(col -> col.getId().equals(updatedColumn.getId()));
        subTypeEntity.getColumns().add(updatedColumn);
        computationResultFiltersRepository.save(root);
        computationSubTypeFiltersRepository.save(subTypeEntity);
    }

    @Transactional(readOnly = true)
    public ComputationTypeFiltersEntity findTypeEntity(UUID rootId, String computationType) {
        ComputationResultFiltersEntity root =
                computationResultFiltersRepository.findById(rootId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));

        return root.getComputationResultFilter().stream()
                .filter(type -> type.getComputationType().equals(computationType))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Computation type not found: " + computationType));
    }
}
