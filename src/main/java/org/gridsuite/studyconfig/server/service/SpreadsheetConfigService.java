/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigCollectionInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
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
    private static final String COLUMN_NOT_FOUND = "Column not found with id: ";

    @Transactional
    public UUID createSpreadsheetConfig(SpreadsheetConfigInfos dto) {
        SpreadsheetConfigEntity entity = SpreadsheetConfigMapper.toEntity(dto);
        return spreadsheetConfigRepository.save(entity).getId();
    }

    @Transactional
    public UUID duplicateSpreadsheetConfig(UUID id) {
        SpreadsheetConfigEntity entity = findEntityById(id);

        SpreadsheetConfigEntity duplicate = SpreadsheetConfigEntity.builder()
                .sheetType(entity.getSheetType())
                .build();

        List<ColumnEntity> columns = entity.getColumns().stream()
                .map(column -> ColumnEntity.builder()
                        .name(column.getName())
                        .type(column.getType())
                        .precision(column.getPrecision())
                        .formula(column.getFormula())
                        .dependencies(column.getDependencies())
                        .id(column.getId())
                        .build())
                .toList();

        duplicate.setColumns(columns);

        return spreadsheetConfigRepository.save(duplicate).getId();
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
        entity.getColumns().clear();
        if (dto.columns() != null) {
            entity.getColumns().addAll(dto.columns().stream()
                    .map(SpreadsheetConfigMapper::toColumnEntity)
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
                    configDuplicate.setColumns(config.getColumns().stream()
                            .map(column -> ColumnEntity.builder()
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

    @Transactional(readOnly = true)
    public ColumnInfos getColumn(UUID id, UUID columnId) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        return entity.getColumns().stream()
            .filter(column -> column.getUuid().equals(columnId))
            .findFirst()
            .map(SpreadsheetConfigMapper::toColumnDto)
            .orElseThrow(() -> new EntityNotFoundException(COLUMN_NOT_FOUND + columnId));
    }

    @Transactional
    public UUID createColumn(UUID id, ColumnInfos dto) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        ColumnEntity columnEntity = SpreadsheetConfigMapper.toColumnEntity(dto);
        entity.getColumns().add(columnEntity);
        spreadsheetConfigRepository.flush();
        return columnEntity.getUuid();
    }

    @Transactional
    public void updateColumn(UUID id, UUID columnId, ColumnInfos dto) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        ColumnEntity columnEntity = entity.getColumns().stream()
            .filter(column -> column.getUuid().equals(columnId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(COLUMN_NOT_FOUND + columnId));

        columnEntity.setName(dto.name());
        columnEntity.setType(dto.type());
        columnEntity.setPrecision(dto.precision());
        columnEntity.setFormula(dto.formula());
        columnEntity.setDependencies(dto.dependencies());
        columnEntity.setId(dto.id());

        spreadsheetConfigRepository.save(entity);
    }

    @Transactional
    public void deleteColumn(UUID id, UUID columnId) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        boolean removed = entity.getColumns().removeIf(column -> column.getUuid().equals(columnId));
        if (!removed) {
            throw new EntityNotFoundException(COLUMN_NOT_FOUND + columnId);
        }
        spreadsheetConfigRepository.save(entity);
    }

}
