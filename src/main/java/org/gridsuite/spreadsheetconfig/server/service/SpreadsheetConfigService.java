/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.spreadsheetconfig.server.dto.MetadataInfos;
import org.gridsuite.spreadsheetconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.spreadsheetconfig.server.entities.CustomColumnEmbeddable;
import org.gridsuite.spreadsheetconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.spreadsheetconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.spreadsheetconfig.server.repositories.SpreadsheetConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class SpreadsheetConfigService {

    private final SpreadsheetConfigRepository spreadsheetConfigRepository;

    @Transactional
    public UUID createSpreadsheetConfig(SpreadsheetConfigInfos dto) {
        SpreadsheetConfigEntity entity = SpreadsheetConfigMapper.toEntity(dto);
        return spreadsheetConfigRepository.save(entity).getId();
    }

    @Transactional
    public UUID duplicateSpreadsheetConfig(UUID id) {
        SpreadsheetConfigEntity entity = spreadsheetConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SpreadsheetConfig not found with id: " + id));

        SpreadsheetConfigEntity duplicate = SpreadsheetConfigEntity.builder()
                .sheetType(entity.getSheetType())
                .build();

        List<CustomColumnEmbeddable> customColumns = entity.getCustomColumns().stream()
                .map(column -> CustomColumnEmbeddable.builder()
                        .name(column.getName())
                        .formula(column.getFormula())
                        .build())
                .toList();

        duplicate.setCustomColumns(customColumns);

        return spreadsheetConfigRepository.save(duplicate).getId();
    }

    @Transactional(readOnly = true)
    public SpreadsheetConfigInfos getSpreadsheetConfig(UUID id) {
        return spreadsheetConfigRepository.findById(id)
                .map(SpreadsheetConfigMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("SpreadsheetConfig not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<SpreadsheetConfigInfos> getAllSpreadsheetConfigs() {
        return spreadsheetConfigRepository.findAll()
                .stream()
                .map(SpreadsheetConfigMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MetadataInfos> getSpreadsheetConfigsMetadata(List<UUID> ids) {
        Objects.requireNonNull(ids);
        return spreadsheetConfigRepository.findAllById(ids)
                .stream()
                .map(SpreadsheetConfigMapper::toMetadataDto)
                .toList();
    }

    @Transactional
    public void updateSpreadsheetConfig(UUID id, SpreadsheetConfigInfos dto) {
        SpreadsheetConfigEntity entity = spreadsheetConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SpreadsheetConfig not found with id: " + id));

        entity.setSheetType(dto.sheetType());
        entity.getCustomColumns().clear();
        if (dto.customColumns() != null) {
            entity.getCustomColumns().addAll(dto.customColumns().stream()
                    .map(SpreadsheetConfigMapper::toCustomColumnEmbeddable)
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
