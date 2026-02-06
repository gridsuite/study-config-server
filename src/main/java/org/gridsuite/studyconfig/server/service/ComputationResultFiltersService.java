/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.ComputationResultColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.FilterSubTypeEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.FilterTypeEntity;
import org.gridsuite.studyconfig.server.entities.computationresult.FiltersEntity;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.studyconfig.server.repositories.computationresult.FilterSubTypeRepository;
import org.gridsuite.studyconfig.server.repositories.computationresult.FilterTypeRepository;
import org.gridsuite.studyconfig.server.repositories.computationresult.FiltersRepository;
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
        FiltersEntity filtersEntity = new FiltersEntity();
        return filtersRepository.saveAndFlush(filtersEntity).getId();
    }

    private FiltersEntity getFiltersEntity(UUID computationResultFiltersId) {
        return filtersRepository.findById(computationResultFiltersId)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + computationResultFiltersId));
    }

    private FilterTypeEntity findComputationType(FiltersEntity filtersEntity, String type) {
        return filtersEntity.getComputationResultFilter().stream()
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
    public List<ComputationResultColumnFilterInfos> getComputingResultColumnFilters(UUID computationResultFiltersId, String computationType, String computationSubType) {
        FiltersEntity filtersEntity = getFiltersEntity(computationResultFiltersId);
        FilterTypeEntity typeEntity = findComputationType(filtersEntity, computationType);
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
    public List<GlobalFilterInfos> getComputingResultGlobalFilters(UUID computationResultFiltersId, String computationType) {
        FiltersEntity filtersEntity = getFiltersEntity(computationResultFiltersId);
        FilterTypeEntity typeEntity = findComputationType(filtersEntity, computationType);
        if (typeEntity == null) {
            return List.of();
        }
        return typeEntity.getGlobalFilters().stream().map(SpreadsheetConfigMapper::toGlobalFilterDto).toList();
    }

    @Transactional
    public void setGlobalFiltersForComputationResult(UUID computationResultFiltersId, String computationType, List<GlobalFilterInfos> globalFilters) {
        FiltersEntity filtersEntity = getFiltersEntity(computationResultFiltersId);
        FilterTypeEntity typeEntity = filtersEntity.getComputationResultFilter().stream()
                .filter(type -> computationType.equals(type.getComputationType()))
                .findFirst()
                .orElseGet(() -> {
                    FilterTypeEntity entity = new FilterTypeEntity();
                    entity.setComputationType(computationType);
                    filtersEntity.getComputationResultFilter().add(entity);
                    return entity;
                });

        List<GlobalFilterEntity> newFilters = globalFilters.stream().map(SpreadsheetConfigMapper::toGlobalFilterEntity).toList();
        if (!typeEntity.getGlobalFilters().equals(newFilters)) {
            typeEntity.getGlobalFilters().clear();
            typeEntity.getGlobalFilters().addAll(newFilters);
        }
        filtersRepository.save(filtersEntity);
    }

    @Transactional
    public void updateColumn(UUID computationResultFiltersId, String computationType, String computationSubType, ComputationResultColumnFilterInfos columns) {
        FiltersEntity filtersEntity = getFiltersEntity(computationResultFiltersId);
        FilterTypeEntity typeEntity = findOrCreateComputationType(filtersEntity, computationType);
        if (typeEntity.getUuid() == null) {
            filterTypeRepository.save(typeEntity);
        }
        FilterSubTypeEntity subTypeEntity = typeEntity.getComputationSubTypes().stream()
                .filter(sub -> sub.getComputationSubType().equals(computationSubType))
                .findFirst()
                .orElseGet(() -> createAndAttachSubType(typeEntity, computationSubType));

        // DELETE case (columnFilter is null)
        var filterInfos = columns.columnFilterInfos();
        if (filterInfos == null) {
            String columnId = columns.columnId();
            subTypeEntity.getColumns().removeIf(col -> columnId != null && columnId.equals(col.getComputationColumnId()));
            filtersRepository.save(filtersEntity);
            return;
        }
        // UPDATE/INSERT case
        ColumnEntity updatedColumn = toComputationColumnFilterEntity(columns);
        String updatedColumnId = updatedColumn.getComputationColumnId();
        subTypeEntity.getColumns().removeIf(col -> updatedColumnId != null && updatedColumnId.equals(col.getComputationColumnId()));
        subTypeEntity.getColumns().add(updatedColumn);
        filtersRepository.save(filtersEntity);
    }

    private FilterTypeEntity findOrCreateComputationType(FiltersEntity filtersEntity, String computationType) {
        return filtersEntity.getComputationResultFilter().stream()
                .filter(type -> type.getComputationType().equals(computationType))
                .findFirst()
                .orElseGet(() -> {
                    FilterTypeEntity newType = new FilterTypeEntity();
                    newType.setComputationType(computationType);
                    filtersEntity.getComputationResultFilter().add(newType);
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
