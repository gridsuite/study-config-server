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
import org.gridsuite.studyconfig.server.entities.*;
import org.gridsuite.studyconfig.server.mapper.CommonFiltersMapper;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationSubTypeFiltersRepository;
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

        List<GlobalFilterEntity> newFilters = globalFilters.stream().map(CommonFiltersMapper::toGlobalFilterEntity).toList();
        if (!typeEntity.getGlobalFilters().equals(newFilters)) {
            typeEntity.getGlobalFilters().clear();
            typeEntity.getGlobalFilters().addAll(newFilters);
        }
        computationResultFiltersRepository.save(root);
    }

    @Transactional
    public void updateColumn(UUID rootId, String computationType, String computationSubType, ColumnFilterInfos columns) {
        ComputationResultFiltersEntity root = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        ComputationSubTypeFiltersEntity subTypeEntity = root.getComputationResultFilter().stream()
                .flatMap(type -> type.getComputationSubTypeResultFilter().stream())
                .filter(sub -> sub.getComputationSubType().equals(computationSubType))
                .findFirst()
                .orElseGet(() -> createAndAttachSubType(root, computationType, computationSubType));
        ColumnFilterEntity updatedColumn = CommonFiltersMapper.toColumnFilterEntity(columns);
        subTypeEntity.getColumns().removeIf(col -> col.getId().equals(updatedColumn.getId()));
        subTypeEntity.getColumns().add(updatedColumn);
        computationResultFiltersRepository.save(root);
        computationSubTypeFiltersRepository.save(subTypeEntity);
    }

    private ComputationTypeFiltersEntity findOrCreateComputationType(ComputationResultFiltersEntity root, String computationType) {
        return root.getComputationResultFilter().stream()
                .filter(type -> type.getComputationType().contains(computationType))
                .findFirst()
                .orElseGet(() -> {
                    ComputationTypeFiltersEntity newType = new ComputationTypeFiltersEntity();
                    newType.setComputationType(computationType);
                    root.getComputationResultFilter().add(newType);
                    return newType;
                });
    }

    private ComputationSubTypeFiltersEntity createAndAttachSubType(ComputationResultFiltersEntity root, String computationType,
                                                                   String computationSubType) {
        ComputationTypeFiltersEntity typeEntity = findOrCreateComputationType(root, computationType);
        ComputationSubTypeFiltersEntity newSubType = new ComputationSubTypeFiltersEntity();
        newSubType.setComputationSubType(computationSubType);
        typeEntity.getComputationSubTypeResultFilter().add(newSubType);
        return computationSubTypeFiltersRepository.save(newSubType);
    }
}
