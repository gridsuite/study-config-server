/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;

import java.util.ArrayList;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
public final class SpreadsheetConfigMapper {

    private SpreadsheetConfigMapper() {
    }

    public static SpreadsheetConfigInfos toDto(SpreadsheetConfigEntity entity) {
        return new SpreadsheetConfigInfos(
                entity.getId(),
                entity.getName(),
                entity.getSheetType(),
                entity.getColumns().stream()
                    .map(SpreadsheetConfigMapper::toColumnDto)
                    .toList(),
                entity.getGlobalFilters().stream()
                    .map(SpreadsheetConfigMapper::toGlobalFilterDto)
                    .toList(),
                entity.getNodeAliases()
        );
    }

    public static MetadataInfos toMetadataDto(SpreadsheetConfigEntity entity) {
        return new MetadataInfos(entity.getId(), entity.getSheetType());
    }

    public static SpreadsheetConfigEntity toEntity(SpreadsheetConfigInfos dto) {
        SpreadsheetConfigEntity entity = SpreadsheetConfigEntity.builder()
                .name(dto.name())
                .sheetType(dto.sheetType())
                .build();
        if (dto.nodeAliases() != null) {
            entity.setNodeAliases(new ArrayList<>(dto.nodeAliases()));
        }
        if (dto.columns() != null) {
            entity.setColumns(dto.columns().stream()
                    .map(SpreadsheetConfigMapper::toColumnEntity)
                    .toList());
        }

        if (dto.globalFilters() != null) {
            entity.setGlobalFilters(dto.globalFilters().stream()
                    .map(SpreadsheetConfigMapper::toGlobalFilterEntity)
                    .toList());
        }

        return entity;
    }

    public static ColumnInfos toColumnDto(ColumnEntity entity) {
        return new ColumnInfos(
                entity.getUuid(),
                entity.getName(),
                entity.getType(),
                entity.getPrecision(),
                entity.getFormula(),
                entity.getDependencies(),
                entity.getId(),
                entity.getFilterDataType(),
                entity.getFilterType(),
                entity.getFilterValue(),
                entity.getFilterTolerance(),
                entity.isVisible()
                );
    }

    public static ColumnEntity toColumnEntity(ColumnInfos dto) {
        return ColumnEntity.builder()
                .name(dto.name())
                .type(dto.type())
                .precision(dto.precision())
                .formula(dto.formula())
                .dependencies(dto.dependencies())
                .id(dto.id())
                .filterDataType(dto.filterDataType())
                .filterType(dto.filterType())
                .filterValue(dto.filterValue())
                .filterTolerance(dto.filterTolerance())
                .visible(dto.visible())
                .build();
    }

    public static GlobalFilterInfos toGlobalFilterDto(GlobalFilterEntity entity) {
        return GlobalFilterInfos.builder()
                .uuid(entity.getUuid())
                .filterType(entity.getFilterType())
                .filterSubtype(entity.getFilterSubtype())
                .label(entity.getLabel())
                .recent(entity.isRecent())
                .equipmentType(entity.getEquipmentType())
                .path(entity.getPath())
                .minValue(entity.getMinValue())
                .maxValue(entity.getMaxValue())
                .build();

    }

    public static GlobalFilterEntity toGlobalFilterEntity(GlobalFilterInfos dto) {
        return GlobalFilterEntity.builder()
                .filterType(dto.filterType())
                .filterSubtype(dto.filterSubtype())
                .uuid(dto.uuid())
                .label(dto.label())
                .recent(dto.recent())
                .equipmentType(dto.equipmentType())
                .path(dto.path())
                .minValue(dto.minValue())
                .maxValue(dto.maxValue())
                .build();
    }
}
