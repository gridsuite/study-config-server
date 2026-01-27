/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.ComputationResultColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.SpreadSheetColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.entities.ColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.ComputationResultColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetColumnFilterEntity;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class CommonFiltersMapper {

    private CommonFiltersMapper() { }

    public static SpreadSheetColumnFilterInfos toSpreadSheetColumnFilterInfos(SpreadsheetColumnFilterEntity entity) {
        return new SpreadSheetColumnFilterInfos(
                entity.getUuid(),
                entity.getName(),
                entity.getType(),
                entity.getPrecision(),
                entity.getFormula(),
                entity.getDependencies(),
                entity.getId(),
                entity.getFilter() != null ? entity.getFilter().getFilterDataType() : null,
                entity.getFilter() != null ? entity.getFilter().getFilterType() : null,
                entity.getFilter() != null ? entity.getFilter().getFilterValue() : null,
                entity.getFilter() != null ? entity.getFilter().getFilterTolerance() : null,
                entity.isVisible()
        );
    }

    public static SpreadsheetColumnFilterEntity toSpreadSheetColumnFilterEntity(SpreadSheetColumnFilterInfos dto) {
        return SpreadsheetColumnFilterEntity.builder()
                .name(dto.name())
                .type(dto.type())
                .precision(dto.precision())
                .formula(dto.formula())
                .dependencies(dto.dependencies())
                .visible(dto.visible())
                .id(dto.id())
                .filter(ColumnFilterEntity.builder()
                        .filterDataType(dto.filterDataType())
                        .filterTolerance(dto.filterTolerance())
                        .filterType(dto.filterType())
                        .filterValue(dto.filterValue())
                        .build())
                .build();
    }

    public static ComputationResultColumnFilterInfos toComputationColumnFilterInfos(ComputationResultColumnFilterEntity entity) {
        return new ComputationResultColumnFilterInfos(
                entity.getId(),
                entity.getFilter().getFilterDataType(),
                entity.getFilter().getFilterType(),
                entity.getFilter().getFilterValue(),
                entity.getFilter().getFilterTolerance()
        );
    }

    public static ComputationResultColumnFilterEntity toColumnFilterEntity(ComputationResultColumnFilterInfos dto) {
        return ComputationResultColumnFilterEntity.builder()
                .id(dto.getId())
                .filter(ColumnFilterEntity.builder()
                        .filterDataType(dto.getFilterDataType())
                        .filterTolerance(dto.getFilterTolerance())
                        .filterType(dto.getFilterType())
                        .filterValue(dto.getFilterValue())
                        .build())
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
