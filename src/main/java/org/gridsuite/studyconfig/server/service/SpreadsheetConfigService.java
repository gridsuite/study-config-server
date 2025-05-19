/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigCollectionEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.gridsuite.studyconfig.server.repositories.SpreadsheetConfigCollectionRepository;
import org.gridsuite.studyconfig.server.repositories.SpreadsheetConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class SpreadsheetConfigService {

    private final SpreadsheetConfigRepository spreadsheetConfigRepository;
    private final SpreadsheetConfigCollectionRepository spreadsheetConfigCollectionRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:default-spreadsheet-config-collection.json")
    private Resource defaultSpreadsheetConfigCollectionResource;

    private static final String SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND = "SpreadsheetConfigCollection not found with id: ";
    private static final String COLUMN_NOT_FOUND = "Column not found with id: ";

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
                .name(entity.getName())
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
                        .filterDataType(column.getFilterDataType())
                        .filterType(column.getFilterType())
                        .filterValue(column.getFilterValue())
                        .filterTolerance(column.getFilterTolerance())
                        .build())
                .toList();
        duplicate.setColumns(columns);

        // Copy global filters if needed
        if (entity.getGlobalFilters() != null) {
            duplicate.setGlobalFilters(entity.getGlobalFilters().stream()
                    .map(globalFilter -> GlobalFilterEntity.builder()
                            .filterType(globalFilter.getFilterType())
                            .label(globalFilter.getLabel())
                            .filterUuid(globalFilter.getFilterUuid())
                            .equipmentType(globalFilter.getEquipmentType())
                            .recent(globalFilter.isRecent())
                            .path(globalFilter.getPath())
                            .build())
                    .toList());
        }
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
        entity.setName(dto.name());
        entity.getColumns().clear();
        if (dto.columns() != null) {
            entity.getColumns().addAll(dto.columns().stream()
                    .map(SpreadsheetConfigMapper::toColumnEntity)
                    .toList());
        }
        entity.getGlobalFilters().clear();
        if (dto.globalFilters() != null) {
            entity.getGlobalFilters().addAll(dto.globalFilters().stream()
                    .map(SpreadsheetConfigMapper::toGlobalFilterEntity)
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

    public UUID createSpreadsheetConfigCollection(SpreadsheetConfigCollectionInfos dto) {
        SpreadsheetConfigCollectionEntity entity = new SpreadsheetConfigCollectionEntity();
        entity.setSpreadsheetConfigs(dto.spreadsheetConfigs().stream()
                .map(SpreadsheetConfigMapper::toEntity)
                .toList());
        entity.setNodeAliases(dto.nodeAliases());
        return spreadsheetConfigCollectionRepository.save(entity).getId();
    }

    @Transactional
    public UUID createSpreadsheetConfigCollectionFromConfigs(List<UUID> configUuids) {
        SpreadsheetConfigCollectionEntity entity = new SpreadsheetConfigCollectionEntity();
        Set<String> targetNames = new HashSet<>();
        entity.setSpreadsheetConfigs(configUuids.stream()
                .map(configId -> {
                    SpreadsheetConfigEntity clone = duplicateSpreadsheetConfigEntity(configId);
                    String newName = getUniqueName(clone.getName(), targetNames, Set.of());
                    clone.setName(newName);
                    targetNames.add(newName);
                    return clone;
                })
                .toList());
        return spreadsheetConfigCollectionRepository.save(entity).getId();
    }

    @Transactional(readOnly = true)
    public SpreadsheetConfigCollectionInfos getSpreadsheetConfigCollection(UUID id) {
        SpreadsheetConfigCollectionEntity entity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));
        return new SpreadsheetConfigCollectionInfos(entity.getId(), entity.getSpreadsheetConfigs().stream()
                .map(SpreadsheetConfigMapper::toDto)
                .toList(), entity.getNodeAliases());
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
        entity.setNodeAliases(dto.nodeAliases());
    }

    @Transactional
    public void updateSpreadsheetConfigCollectionWithConfigs(UUID id, List<UUID> configUuids) {
        SpreadsheetConfigCollectionEntity entity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));

        entity.getSpreadsheetConfigs().clear();
        entity.getSpreadsheetConfigs().addAll(configUuids.stream()
                .map(this::duplicateSpreadsheetConfigEntity)
                .toList());
    }

    @Transactional
    public void appendSpreadsheetConfigCollection(UUID id, UUID sourceCollectionId) {
        SpreadsheetConfigCollectionEntity targetEntity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));
        SpreadsheetConfigCollectionEntity sourceEntity = spreadsheetConfigCollectionRepository.findById(sourceCollectionId)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + sourceCollectionId));
        // Make sure names are unique in the merged collection
        Set<String> targetNames = targetEntity.getSpreadsheetConfigs().stream().map(SpreadsheetConfigEntity::getName).collect(Collectors.toSet());
        Set<String> sourceNames = sourceEntity.getSpreadsheetConfigs().stream().map(SpreadsheetConfigEntity::getName).collect(Collectors.toSet());
        targetEntity.getSpreadsheetConfigs().addAll(sourceEntity.getSpreadsheetConfigs().stream().map(SpreadsheetConfigEntity::getId)
                .map(configId -> {
                    SpreadsheetConfigEntity clone = duplicateSpreadsheetConfigEntity(configId);
                    clone.setName(getUniqueName(clone.getName(), targetNames, sourceNames));
                    return clone;
                })
                .toList());
        // keep only aliases of appended collection, they will be invalidated by the Front
        targetEntity.getNodeAliases().clear();
        targetEntity.getNodeAliases().addAll(sourceEntity.getNodeAliases());
    }

    private String getUniqueName(String name, Set<String> targetNames, Set<String> sourceNames) {
        if (!targetNames.contains(name)) {
            return name;
        }
        final String newNameFormat = name + " (%d)";
        String nextName;
        int x = 0;
        do {
            nextName = String.format(newNameFormat, ++x);
        } while (targetNames.contains(nextName) || sourceNames.contains(nextName));
        return nextName;
    }

    @Transactional
    public UUID duplicateSpreadsheetConfigCollection(UUID id) {
        SpreadsheetConfigCollectionEntity entity = spreadsheetConfigCollectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + id));

        SpreadsheetConfigCollectionEntity duplicate = new SpreadsheetConfigCollectionEntity();
        duplicate.setSpreadsheetConfigs(entity.getSpreadsheetConfigs().stream()
                .map(config -> {
                    SpreadsheetConfigEntity configDuplicate = SpreadsheetConfigEntity.builder()
                            .name(config.getName())
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
                                    .filterDataType(column.getFilterDataType())
                                    .filterType(column.getFilterType())
                                    .filterValue(column.getFilterValue())
                                    .filterTolerance(column.getFilterTolerance())
                                    .build())
                            .toList());
                    configDuplicate.setGlobalFilters(config.getGlobalFilters().stream()
                            .map(globalFilter -> GlobalFilterEntity.builder()
                                    .filterType(globalFilter.getFilterType())
                                    .label(globalFilter.getLabel())
                                    .filterUuid(globalFilter.getFilterUuid())
                                    .equipmentType(globalFilter.getEquipmentType())
                                    .recent(globalFilter.isRecent())
                                    .path(globalFilter.getPath())
                                    .build())
                            .toList());
                    return configDuplicate;
                })
                .toList());
        duplicate.setNodeAliases(new ArrayList<>(entity.getNodeAliases()));
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
        columnEntity.setFilterDataType(dto.filterDataType());
        columnEntity.setFilterType(dto.filterType());
        columnEntity.setFilterValue(dto.filterValue());
        columnEntity.setFilterTolerance(dto.filterTolerance());

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

    @Transactional
    public void reorderColumns(UUID id, List<UUID> columnOrder) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        List<ColumnEntity> columns = entity.getColumns();

        columns.sort((c1, c2) -> {
            int idx1 = columnOrder.indexOf(c1.getUuid());
            int idx2 = columnOrder.indexOf(c2.getUuid());
            return Integer.compare(idx1, idx2);
        });
    }

    private SpreadsheetConfigCollectionInfos readDefaultSpreadsheetConfigCollection() throws IOException {
        try (InputStream inputStream = defaultSpreadsheetConfigCollectionResource.getInputStream()) {
            return objectMapper.readValue(inputStream, SpreadsheetConfigCollectionInfos.class);
        }
    }

    @Transactional
    public void setGlobalFiltersForSpreadsheetConfig(UUID id, List<GlobalFilterInfos> globalFilters) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        entity.getGlobalFilters().clear();
        entity.getGlobalFilters().addAll(globalFilters.stream()
                .map(SpreadsheetConfigMapper::toGlobalFilterEntity)
                .toList());
    }

    public UUID createDefaultSpreadsheetConfigCollection() {
        try {
            SpreadsheetConfigCollectionInfos defaultCollection = readDefaultSpreadsheetConfigCollection();
            return createSpreadsheetConfigCollection(defaultCollection);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read default spreadsheet config collection", e);
        }
    }

    @Transactional
    public UUID addSpreadsheetConfigToCollection(UUID collectionId, SpreadsheetConfigInfos dto) {
        SpreadsheetConfigCollectionEntity collection = spreadsheetConfigCollectionRepository.findById(collectionId)
            .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + collectionId));

        SpreadsheetConfigEntity newConfig = SpreadsheetConfigMapper.toEntity(dto);
        collection.getSpreadsheetConfigs().add(newConfig);
        spreadsheetConfigCollectionRepository.flush();
        return newConfig.getId();
    }

    @Transactional
    public void removeSpreadsheetConfigFromCollection(UUID collectionId, UUID configId) {
        SpreadsheetConfigCollectionEntity collection = spreadsheetConfigCollectionRepository.findById(collectionId)
            .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + collectionId));

        boolean removed = collection.getSpreadsheetConfigs().removeIf(config -> config.getId().equals(configId));
        if (!removed) {
            throw new EntityNotFoundException("Spreadsheet configuration not found in collection");
        }
        spreadsheetConfigCollectionRepository.save(collection);
    }

    @Transactional
    public void reorderSpreadsheetConfigs(UUID collectionId, List<UUID> newOrder) {
        SpreadsheetConfigCollectionEntity collection = spreadsheetConfigCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new EntityNotFoundException(SPREADSHEET_CONFIG_COLLECTION_NOT_FOUND + collectionId));

        // Validate inputs
        Set<UUID> existingIds = collection.getSpreadsheetConfigs().stream()
                .map(SpreadsheetConfigEntity::getId)
                .collect(Collectors.toSet());

        if (existingIds.size() != newOrder.size() || !existingIds.containsAll(newOrder)) {
            throw new IllegalArgumentException("New order must contain exactly the same configs as the collection");
        }

        List<SpreadsheetConfigEntity> configs = collection.getSpreadsheetConfigs();
        configs.sort((c1, c2) -> {
            int idx1 = newOrder.indexOf(c1.getId());
            int idx2 = newOrder.indexOf(c2.getId());
            return Integer.compare(idx1, idx2);
        });
    }

    @Transactional
    public void renameSpreadsheetConfig(UUID id, String name) {
        SpreadsheetConfigEntity entity = findEntityById(id);
        entity.setName(name);
    }

}
