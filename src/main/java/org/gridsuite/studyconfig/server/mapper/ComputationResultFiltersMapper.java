/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.constants.ComputationSubType;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.ColumnsFiltersInfos;
import org.gridsuite.studyconfig.server.dto.ComputationResultFilterInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.ColumnsFiltersEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultFilterEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class ComputationResultFiltersMapper {

    private ComputationResultFiltersMapper() { }

    public static ComputationResultFilterInfos toDto(ComputationResultFilterEntity entity) {
        Map<ComputationSubType, ColumnsFiltersInfos> columnsFiltersMap = entity.getColumnsFilters().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new ColumnsFiltersInfos(
                                e.getValue().getId(),
                                e.getValue().getColumns().stream()
                                        .map(SpreadsheetConfigMapper::toColumnDto)
                                        .toList()
                        ),
                        (a, b) -> a,
                        () -> new EnumMap<>(ComputationSubType.class)
                ));

        List<GlobalFilterInfos> globalFiltersList = entity.getGlobalFilters().stream()
                .map(SpreadsheetConfigMapper::toGlobalFilterDto)
                .toList();

        return new ComputationResultFilterInfos(
                entity.getId(),
                columnsFiltersMap,
                globalFiltersList
        );
    }

    public static ComputationResultFilterEntity toEntity(ComputationResultFilterInfos filter) {

        Map<ComputationSubType, ColumnsFiltersEntity> columnsFiltersMap = new EnumMap<>(ComputationSubType.class);

        filter.columnsFilters().forEach((subType, cfi) -> {

            List<ColumnEntity> columns = cfi.columns().stream()
                    .map(ComputationResultFiltersMapper::toColumnEntity)
                    .toList();

            ColumnsFiltersEntity entity = ColumnsFiltersEntity.builder()
                    .id(cfi.id())
                    .columns(columns)
                    .build();

            columnsFiltersMap.put(subType, entity);
        });

        return ComputationResultFilterEntity.builder()
                .id(filter.id())
                .columnsFilters(columnsFiltersMap)
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
