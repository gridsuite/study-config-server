/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.ColumnsFiltersInfos;
import org.gridsuite.studyconfig.server.dto.ComputationResultFilterInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.ColumnsFiltersEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultFilterEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class ComputationResultFiltersMapper {

    private ComputationResultFiltersMapper() { }

    public static ComputationResultFilterInfos toDto(ComputationResultFilterEntity entity) {
        return new ComputationResultFilterInfos(
                entity.getId(),
                entity.getComputationType(),
                entity.getColumnsFilters().stream()
                        .map(ComputationResultFiltersMapper::toColumnsFiltersDto)
                        .toList(),
                entity.getGlobalFilters().stream()
                        .map(SpreadsheetConfigMapper::toGlobalFilterDto)
                        .toList()
        );
    }

    public static ColumnsFiltersInfos toColumnsFiltersDto(ColumnsFiltersEntity entity) {
        return ColumnsFiltersInfos.builder()
                .id(entity.getId())
                .computationSubType(entity.getComputationSubType())
                .columns(entity.getColumns().stream()
                        .map(SpreadsheetConfigMapper::toColumnDto)
                        .toList())
                .build();
    }

    public static ComputationResultFilterEntity toEntity(ComputationResultFilterInfos filter) {

        List<ColumnsFiltersEntity> tabs = new ArrayList<>();

        for (ColumnsFiltersInfos tab : filter.columnsFilters()) {

            List<ColumnEntity> colEntities = tab.columns().stream()
                    .map(ComputationResultFiltersMapper::toColumnEntity)
                    .toList();

            tabs.add(ColumnsFiltersEntity.builder()
                    .computationSubType(tab.computationSubType())
                    .columns(colEntities)
                    .build());
        }

        return ComputationResultFilterEntity.builder()
                .computationType(filter.computationType())
                .columnsFilters(tabs)
                .globalFilters(filter.globalFilters().stream()
                        .map(ComputationResultFiltersMapper::toGlobalFilterEntity)
                        .toList())
                .build();
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

    public static GlobalFilterEntity toGlobalFilterEntity(GlobalFilterInfos dto) {
        return GlobalFilterEntity.builder()
                .filterType(dto.filterType())
                .filterSubtype(dto.filterSubtype())
                .uuid(dto.uuid())
                .label(dto.label())
                .recent(dto.recent())
                .equipmentType(dto.equipmentType())
                .path(dto.path())
                .build();
    }
}
