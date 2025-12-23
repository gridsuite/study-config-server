/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.ColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetColumnEntity;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class CommonFiltersMapper {

    private CommonFiltersMapper() { }

    public static ColumnInfos toColumnDto(SpreadsheetColumnEntity entity) {
        return new ColumnInfos(
                entity.getUuid(),
                entity.getName(),
                entity.getType(),
                entity.getPrecision(),
                entity.getFormula(),
                entity.getDependencies(),
                entity.isVisible(),
                entity.getColumnFilter() == null ? null : toColumnFilterDto(entity.getColumnFilter())
        );
    }

    public static ColumnFilterInfos toColumnFilterDto(ColumnFilterEntity entity) {
        return new ColumnFilterInfos(
                entity.getUuid(),
                entity.getColumnId(),
                entity.getFilterDataType(),
                entity.getFilterType(),
                entity.getFilterValue(),
                entity.getFilterTolerance()
        );
    }

    public static SpreadsheetColumnEntity toColumnEntity(ColumnInfos dto) {
        return SpreadsheetColumnEntity.builder()
                .name(dto.name())
                .type(dto.type())
                .precision(dto.precision())
                .formula(dto.formula())
                .dependencies(dto.dependencies())
                .visible(dto.visible())
                .columnFilter(dto.columnFilterInfos() == null
                        ? null
                        : toColumnFilterEntity(dto.columnFilterInfos()))
                .build();
    }

    public static ColumnFilterEntity toColumnFilterEntity(ColumnFilterInfos dto) {
        return ColumnFilterEntity.builder()
                .columnId(dto.columnId())
                .filterDataType(dto.filterDataType())
                .filterType(dto.filterType())
                .filterValue(dto.filterValue())
                .filterTolerance(dto.filterTolerance())
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
