/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.spreadsheetconfig.server.dto.SpreadsheetConfigDto;
import org.gridsuite.spreadsheetconfig.server.entities.CustomColumnEntity;
import org.gridsuite.spreadsheetconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.spreadsheetconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.spreadsheetconfig.server.repositories.SpreadsheetConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class SpreadsheetConfigService {

    private final SpreadsheetConfigRepository spreadsheetConfigRepository;

    private final SpreadsheetConfigMapper spreadsheetConfigMapper;

    @Transactional
    public UUID createSpreadsheetConfig(SpreadsheetConfigDto dto) {
        SpreadsheetConfigEntity entity = spreadsheetConfigMapper.toEntity(dto);
        return spreadsheetConfigRepository.save(entity).getId();
    }

    @Transactional
    public Optional<UUID> duplicateSpreadsheetConfig(UUID id) {
        return spreadsheetConfigRepository.findById(id)
                .map(entity -> {
                    SpreadsheetConfigEntity duplicate = SpreadsheetConfigEntity.builder()
                            .sheetType(entity.getSheetType())
                            .build();

                    List<CustomColumnEntity> customColumns = entity.getCustomColumns().stream()
                            .map(column -> CustomColumnEntity.builder()
                                    .name(column.getName())
                                    .formula(column.getFormula())
                                    .spreadsheetConfig(duplicate)
                                    .build())
                            .toList();

                    duplicate.setCustomColumns(customColumns);

                    return spreadsheetConfigRepository.save(duplicate).getId();
                });
    }

    @Transactional(readOnly = true)
    public Optional<SpreadsheetConfigDto> getSpreadsheetConfig(UUID id) {
        return spreadsheetConfigRepository.findById(id)
                .map(spreadsheetConfigMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<SpreadsheetConfigDto> getAllSpreadsheetConfigs() {
        return spreadsheetConfigRepository.findAll()
                .stream()
                .map(spreadsheetConfigMapper::toDto)
                .toList();
    }

    @Transactional
    public void updateSpreadsheetConfig(UUID id, SpreadsheetConfigDto dto) {
        SpreadsheetConfigEntity entity = spreadsheetConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SpreadsheetConfig not found with id: " + id));

        entity.setSheetType(dto.getSheetType());
        entity.getCustomColumns().clear();
        if (dto.getCustomColumns() != null) {
            entity.getCustomColumns().addAll(dto.getCustomColumns().stream()
                    .map(columnDto -> spreadsheetConfigMapper.toCustomColumnEntity(columnDto, entity))
                    .toList());
        }
    }

    @Transactional
    public void deleteSpreadsheetConfig(UUID id) {
        if (!spreadsheetConfigRepository.existsById(id)) {
            throw new EntityNotFoundException("SpreadsheetConfig not found with id: " + id);
        }
        spreadsheetConfigRepository.deleteById(id);
    }

}
