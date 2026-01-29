/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.entities.*;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationSubTypeFiltersRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationTypeFiltersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper.toComputationColumnFilterEntity;


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
    public ComputationTypeFiltersInfos getComputingResultFilters(UUID rootId, String computationType, String computationSubType) {
        ComputationResultFiltersEntity rootEntity = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        return ComputationResultFiltersMapper.toTypeDto(rootEntity.getComputationResultFilter().stream()
                .filter(type -> type.getComputationType().equals(computationType))
                .filter(type -> type.getComputationSubTypes().stream().anyMatch(
                        sub -> sub.getComputationSubType().equals(computationSubType)))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + computationType)));
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

    @Transactional
    public void updateColumn(
            UUID rootId,
            String computationType,
            String computationSubType,
            ComputationResultColumnFilterInfos columns
    ) {
        ComputationResultFiltersEntity root = computationResultFiltersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        ComputationTypeFiltersEntity typeEntity = findOrCreateComputationType(root, computationType);
        if (typeEntity.getUuid() == null) {
            computationTypeFiltersRepository.save(typeEntity);
        }
        ComputationSubTypeFiltersEntity subTypeEntity = typeEntity.getComputationSubTypes().stream()
                .filter(sub -> sub.getComputationSubType().equals(computationSubType))
                .findFirst()
                .orElseGet(() -> createAndAttachSubType(typeEntity, computationSubType));
        ComputationResultColumnFilterEntity updatedColumn = toComputationColumnFilterEntity(columns);
        subTypeEntity.getColumns().removeIf(col -> col.getComputationColumnId() != null && col.getComputationColumnId().equals(updatedColumn.getComputationColumnId()));
        subTypeEntity.getColumns().add(updatedColumn);
        computationResultFiltersRepository.save(root);
    }

    private ComputationTypeFiltersEntity findOrCreateComputationType(ComputationResultFiltersEntity root, String computationType) {
        return root.getComputationResultFilter().stream()
                .filter(type -> type.getComputationType().equals(computationType))
                .findFirst()
                .orElseGet(() -> {
                    ComputationTypeFiltersEntity newType = new ComputationTypeFiltersEntity();
                    newType.setComputationType(computationType);
                    root.getComputationResultFilter().add(newType);
                    return newType;
                });
    }

    private ComputationSubTypeFiltersEntity createAndAttachSubType(ComputationTypeFiltersEntity typeEntity, String computationSubType) {
        ComputationSubTypeFiltersEntity newSubType = new ComputationSubTypeFiltersEntity();
        newSubType.setComputationSubType(computationSubType);
        typeEntity.getComputationSubTypes().add(newSubType);
        return computationSubTypeFiltersRepository.save(newSubType);
    }
}
