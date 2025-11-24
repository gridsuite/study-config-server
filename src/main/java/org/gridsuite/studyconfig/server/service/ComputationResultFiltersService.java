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
import org.gridsuite.studyconfig.server.dto.ComputationResultFiltersInfos;
import org.gridsuite.studyconfig.server.entities.ComputationResultFiltersEntity;
import org.gridsuite.studyconfig.server.mapper.ComputationResultFiltersMapper;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class ComputationResultFiltersService {

    private static final String COMPUTATION_FILTERS_NOT_FOUND = "not found";
    private final ComputationResultFiltersRepository computationResultFiltersRepository;
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
        root.setComputationResultFilter(dto.computationResultFilters().stream()
                .map(ComputationResultFiltersMapper::toEntity)
                .toList());
        return computationResultFiltersRepository.save(root).getId();
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
        return new ComputationResultFiltersInfos(entity.getId(), entity.getComputationResultFilter().stream()
                .map(ComputationResultFiltersMapper::toDto)
                .toList());
    }
}
