/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.constants.ComputationType;
import org.gridsuite.studyconfig.server.dto.ColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.ComputationResultFilterInfos;
import org.gridsuite.studyconfig.server.dto.ComputationResultFiltersInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultColumnsFiltersEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultFilterEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultFiltersEntity;
import org.gridsuite.studyconfig.server.mapper.CommonFiltersMapper;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFilterRepository;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class ComputationResultFiltersService {

    private static final String COMPUTATION_FILTERS_NOT_FOUND = "not found";
    private final ComputationResultFiltersRepository computationResultFiltersRepository;
    private final ComputationResultFilterRepository computationResultFilterRepository;
    @Value("classpath:default-computation-result-filters.json")
    private Resource defaultComputationResultFiltersResource;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID createDefaultComputingResultFilters() {
        try {
            ComputationResultFiltersInfos defaultCollection = readComputationResultFiltersInfos();
            return createDefaultComputationConfig(defaultCollection);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read default computation result filters", e);
        }
    }

    public UUID createDefaultComputationConfig(ComputationResultFiltersInfos dto) {
        ComputationResultFiltersEntity root = new ComputationResultFiltersEntity();
        Map<ComputationType, ComputationResultFilterEntity> mappedFilters = dto.computationResultFilters().entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    List<ComputationResultFilterInfos> list = e.getValue();
                    return ComputationResultFiltersMapper.toEntity(list.getFirst());
                }, (a, b) -> a, () -> new EnumMap<>(ComputationType.class)));
        root.setComputationResultFilter(mappedFilters);
        return computationResultFiltersRepository.saveAndFlush(root).getId();
    }

    private ComputationResultFiltersInfos readComputationResultFiltersInfos() throws IOException {
        try (InputStream inputStream = defaultComputationResultFiltersResource.getInputStream()) {
            return objectMapper.readValue(inputStream, ComputationResultFiltersInfos.class);
        }
    }

    @Transactional(readOnly = true)
    public ComputationResultFiltersInfos getComputingResultFilters(UUID id) {
        ComputationResultFiltersEntity entity = computationResultFiltersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(COMPUTATION_FILTERS_NOT_FOUND + id));

        Map<ComputationType, List<ComputationResultFilterInfos>> groupedFilters = entity.getComputationResultFilter()
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        e -> List.of(ComputationResultFiltersMapper.toDto(e.getValue()))));
        return new ComputationResultFiltersInfos(entity.getId(), groupedFilters);
    }

    @Transactional
    public void setGlobalFiltersForComputationResult(UUID id, List<GlobalFilterInfos> globalFilters) {
        ComputationResultFilterEntity entity = findEntityById(id);
        entity.getGlobalFilters().clear();
        entity.getGlobalFilters().addAll(globalFilters.stream()
                .map(CommonFiltersMapper::toGlobalFilterEntity)
                .toList());
    }

    @Transactional
    public void updateColumn(UUID id, UUID columnId, ColumnFilterInfos dto) {
        ComputationResultFilterEntity entity = findEntityById(id);
        ComputationResultColumnsFiltersEntity columnFilterEntity = entity.getColumnsFilters().values().stream()
                .filter(w -> w.getId().equals(columnId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Column wrapper not found: " + columnId));
        ColumnFilterEntity updatedColumn = CommonFiltersMapper.toColumnFilterEntity(dto);
        Optional<ColumnFilterEntity> existingFilter = columnFilterEntity.getColumns().stream()
                .filter(filter -> filter.getId().equals(updatedColumn.getId()))
                .findFirst();
        if (existingFilter.isPresent()) {
            ColumnFilterEntity filterToUpdate = existingFilter.get();
            filterToUpdate.setFilterDataType(updatedColumn.getFilterDataType());
            filterToUpdate.setFilterType(updatedColumn.getFilterType());
            filterToUpdate.setFilterValue(updatedColumn.getFilterValue());
            filterToUpdate.setFilterTolerance(updatedColumn.getFilterTolerance());
        } else {
            columnFilterEntity.getColumns().add(updatedColumn);
        }
        computationResultFilterRepository.save(entity);
    }

    private ComputationResultFilterEntity findEntityById(UUID id) {
        return computationResultFilterRepository.findById(id)
                .orElseThrow(() -> entityNotFoundException(id));
    }

    private EntityNotFoundException entityNotFoundException(UUID id) {
        return new EntityNotFoundException("ComputationResultFilter not found with id: " + id);
    }
}
