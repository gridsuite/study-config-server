/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigCollectionInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.CustomColumnEmbeddable;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigCollectionEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.studyconfig.server.repositories.SpreadsheetConfigCollectionRepository;
import org.gridsuite.studyconfig.server.repositories.SpreadsheetConfigRepository;
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
    private final SpreadsheetConfigCollectionRepository spreadsheetConfigCollectionRepository;

    private static final String SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND = "SpreadsheetConfigCollection not found with id: ";

    @Transactional
    public UUID createSpreadsheetConfig(SpreadsheetConfigInfos dto) {
        SpreadsheetConfigEntity entity = SpreadsheetConfigMapper.toEntity(dto);
        return spreadsheetConfigRepository.save(entity).getId();
    }

    @Transactional
    public UUID duplicateSpreadsheetConfig(UUID id) {
        SpreadsheetConfigEntity duplicate = duplicateSpreadsheetConfigEntity(id);
        return spreadsheetConfigRepository.save(duplicate).getId();
    }

    private SpreadsheetConfigEntity duplicateSpreadsheetConfigEntity(UUID id) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        SpreadsheetConfigEntity duplicate = SpreadsheetConfigEntity.builder()
                .sheetType(entity.getSheetType())
                .build();
        List<CustomColumnEmbeddable> customColumns = entity.getCustomColumns().stream()
                .map(column -> CustomColumnEmbeddable.builder()
                        .name(column.getName())
                        .type(column.getType())
                        .precision(column.getPrecision())
                        .formula(column.getFormula())
                        .dependencies(column.getDependencies())
                        .id(column.getId())
                        .build())
                .toList();
        duplicate.setCustomColumns(customColumns);
        return duplicate;
    }

    @Transactional(readOnly = true)
    public SpreadsheetConfigInfos getSpreadsheetConfig(UUID id) {
        return SpreadsheetConfigMapper.toDto(findEntityById(id));
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
        SpreadsheetConfigEntity entity = findEntityById(id);

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
            throw entityNotFoundException(id);
        }
        spreadsheetConfigRepository.deleteById(id);
    }

    private SpreadsheetConfigEntity findEntityById(UUID id) {
        return spreadsheetConfigRepository.findById(id)
                .orElseThrow(() -> entityNotFoundException(id));
    }

    private EntityNotFoundException entityNotFoundException(UUID id) {
        return new EntityNotFoundException("SpreadsheetConfig not found with id: " + id);
    }

    @Transactional
    public UUID createSpreadsheetConfigCollection(SpreadsheetConfigCollectionInfos dto) {
        SpreadsheetConfigCollectionEntity entity = new SpreadsheetConfigCollectionEntity();
        entity.setSpreadsheetConfigs(dto.spreadsheetConfigs().stream()
                .map(SpreadsheetConfigMapper::toEntity)
                .toList());
        return spreadsheetConfigCollectionRepository.save(entity).getId();
    }

    @Transactional
    public UUID createSpreadsheetConfigCollectionFromConfigs(List<UUID> configUuids) {
        SpreadsheetConfigCollectionEntity entity = new SpreadsheetConfigCollectionEntity();
        entity.setSpreadsheetConfigs(configUuids.stream()
                .map(this::duplicateSpreadsheetConfigEntity)
                .toList());
        return spreadsheetConfigCollectionRepository.save(entity).getId();
    }

    @Transactional(readOnly = true)
    public SpreadsheetConfigCollectionInfos getSpreadsheetConfigCollection(UUID id) {
        SpreadsheetConfigCollectionEntity entity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));
        return new SpreadsheetConfigCollectionInfos(entity.getId(), entity.getSpreadsheetConfigs().stream()
                .map(SpreadsheetConfigMapper::toDto)
                .toList());
    }

    @Transactional
    public void deleteSpreadsheetConfigCollection(UUID id) {
        if (!spreadsheetConfigCollectionRepository.existsById(id)) {
            throw new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id);
        }
        spreadsheetConfigCollectionRepository.deleteById(id);
    }

    @Transactional
    public void updateSpreadsheetConfigCollection(UUID id, SpreadsheetConfigCollectionInfos dto) {
        SpreadsheetConfigCollectionEntity entity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));

        entity.getSpreadsheetConfigs().clear();
        entity.getSpreadsheetConfigs().addAll(dto.spreadsheetConfigs().stream()
                .map(SpreadsheetConfigMapper::toEntity)
                .toList());
    }

    @Transactional
    public UUID duplicateSpreadsheetConfigCollection(UUID id) {
        SpreadsheetConfigCollectionEntity entity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));

        SpreadsheetConfigCollectionEntity duplicate = new SpreadsheetConfigCollectionEntity();
        duplicate.setSpreadsheetConfigs(entity.getSpreadsheetConfigs().stream()
                .map(config -> {
                    SpreadsheetConfigEntity configDuplicate = SpreadsheetConfigEntity.builder()
                            .sheetType(config.getSheetType())
                            .build();
                    configDuplicate.setCustomColumns(config.getCustomColumns().stream()
                            .map(column -> CustomColumnEmbeddable.builder()
                                    .name(column.getName())
                                    .type(column.getType())
                                    .precision(column.getPrecision())
                                    .formula(column.getFormula())
                                    .dependencies(column.getDependencies())
                                    .id(column.getId())
                                    .build())
                            .toList());
                    return configDuplicate;
                })
                .toList());
        return spreadsheetConfigCollectionRepository.save(duplicate).getId();
    }

}
