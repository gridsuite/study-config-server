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
import org.gridsuite.studyconfig.server.dto.ComputationResultColumnFilterInfos;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.FilterTypeEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.FiltersEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.FilterSubTypeEntity;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.studyconfig.server.repositories.computationresult.FiltersRepository;
import org.gridsuite.studyconfig.server.repositories.computationresult.FilterSubTypeRepository;
import org.gridsuite.studyconfig.server.repositories.computationresult.FilterTypeRepository;
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
    private final FiltersRepository filtersRepository;
    private final FilterTypeRepository filterTypeRepository;
    private final FilterSubTypeRepository filterSubTypeRepository;

    @Transactional
    public UUID createDefaultComputingResultFilters() {
        FiltersEntity root = new FiltersEntity();
        return filtersRepository.saveAndFlush(root).getId();
    }

    private FiltersEntity getRootEntity(UUID rootId) {
        return filtersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
    }

    private FilterTypeEntity findComputationType(FiltersEntity root, String type) {
        return root.getComputationResultFilter().stream()
                .filter(t -> t.getComputationType().equals(type))
                .findFirst()
                .orElse(null);
    }

    private FilterSubTypeEntity findComputationSubType(FilterTypeEntity typeEntity, String subType) {
        return typeEntity.getComputationSubTypes().stream()
                .filter(s -> s.getComputationSubType().equals(subType))
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ComputationResultColumnFilterInfos> getComputingResultColumnFilters(UUID rootId, String computationType, String computationSubType) {
        FiltersEntity rootEntity = getRootEntity(rootId);
        FilterTypeEntity typeEntity = findComputationType(rootEntity, computationType);
        if (typeEntity == null) {
            return List.of();
        }
        FilterSubTypeEntity subTypeEntity = findComputationSubType(typeEntity, computationSubType);
        if (subTypeEntity == null) {
            return List.of();
        }
        return subTypeEntity.getColumns().stream().map(ComputationResultFiltersMapper::toComputationColumnFilterInfos).toList();
    }

    @Transactional(readOnly = true)
    public List<GlobalFilterInfos> getComputingResultGlobalFilters(UUID rootId, String computationType) {
        FiltersEntity rootEntity = getRootEntity(rootId);
        FilterTypeEntity typeEntity = findComputationType(rootEntity, computationType);
        if (typeEntity == null) {
            return List.of();
        }
        return typeEntity.getGlobalFilters().stream().map(SpreadsheetConfigMapper::toGlobalFilterDto).toList();
    }

    @Transactional
    public void setGlobalFiltersForComputationResult(UUID rootId, String computationType, List<GlobalFilterInfos> globalFilters) {
        FiltersEntity root = filtersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        FilterTypeEntity typeEntity = root.getComputationResultFilter().stream()
                .filter(type -> computationType.equals(type.getComputationType()))
                .findFirst()
                .orElseGet(() -> {
                    FilterTypeEntity entity = new FilterTypeEntity();
                    entity.setComputationType(computationType);
                    root.getComputationResultFilter().add(entity);
                    return entity;
                });

        List<GlobalFilterEntity> newFilters = globalFilters.stream().map(SpreadsheetConfigMapper::toGlobalFilterEntity).toList();
        if (!typeEntity.getGlobalFilters().equals(newFilters)) {
            typeEntity.getGlobalFilters().clear();
            typeEntity.getGlobalFilters().addAll(newFilters);
        }
        filtersRepository.save(root);
    }

    @Transactional
    public void updateColumn(UUID rootId, String computationType, String computationSubType, ComputationResultColumnFilterInfos columns) {
        FiltersEntity root = filtersRepository.findById(rootId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + rootId));
        FilterTypeEntity typeEntity = findOrCreateComputationType(root, computationType);
        if (typeEntity.getUuid() == null) {
            filterTypeRepository.save(typeEntity);
        }
        FilterSubTypeEntity subTypeEntity = typeEntity.getComputationSubTypes().stream()
                .filter(sub -> sub.getComputationSubType().equals(computationSubType))
                .findFirst()
                .orElseGet(() -> createAndAttachSubType(typeEntity, computationSubType));
        ColumnEntity updatedColumn = toComputationColumnFilterEntity(columns);
        subTypeEntity.getColumns().removeIf(col -> col.getComputationColumnId() != null && col.getComputationColumnId().equals(updatedColumn.getComputationColumnId()));
        subTypeEntity.getColumns().add(updatedColumn);
        filtersRepository.save(root);
    }

    private FilterTypeEntity findOrCreateComputationType(FiltersEntity root, String computationType) {
        return root.getComputationResultFilter().stream()
                .filter(type -> type.getComputationType().equals(computationType))
                .findFirst()
                .orElseGet(() -> {
                    FilterTypeEntity newType = new FilterTypeEntity();
                    newType.setComputationType(computationType);
                    root.getComputationResultFilter().add(newType);
                    return newType;
                });
    }

    private FilterSubTypeEntity createAndAttachSubType(FilterTypeEntity typeEntity, String computationSubType) {
        FilterSubTypeEntity newSubType = new FilterSubTypeEntity();
        newSubType.setComputationSubType(computationSubType);
        typeEntity.getComputationSubTypes().add(newSubType);
        return filterSubTypeRepository.save(newSubType);
    }
}
