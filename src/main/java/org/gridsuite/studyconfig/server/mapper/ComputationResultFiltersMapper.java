/**
* Copyright (c) 2025, RTE (http://www.rte-france.com)
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.entities.*;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class ComputationResultFiltersMapper {

    private ComputationResultFiltersMapper() { }

    public static ComputationResultFiltersInfos toDto(ComputationResultFiltersEntity entity) {
        return new ComputationResultFiltersInfos(entity.getComputationResultFilter().stream()
                .map(ComputationResultFiltersMapper::toTypeDto)
                .toList());
    }

    public static ComputationTypeFiltersInfos toTypeDto(ComputationTypeFiltersEntity entity) {
        return new ComputationTypeFiltersInfos(entity.getComputationType(),
                entity.getGlobalFilters().stream()
                .map(SpreadsheetConfigMapper::toGlobalFilterDto).toList(),
                entity.getComputationSubTypes().stream().map(ComputationResultFiltersMapper::toSubTypeDto).toList()
        );
    }

    private static ComputationSubTypeFilterInfos toSubTypeDto(ComputationSubTypeFiltersEntity entity) {
        return new ComputationSubTypeFilterInfos(
                entity.getComputationSubType(),
                entity.getColumns().stream().map(ComputationResultFiltersMapper::toComputationColumnFilterInfos).toList()
        );
    }

    public static ComputationResultColumnFilterInfos toComputationColumnFilterInfos(ComputationResultColumnFilterEntity entity) {
        ColumnFilter filter = entity.getColumnFilter();
        return new ComputationResultColumnFilterInfos(entity.getComputationColumnId(), filter != null ?
                new ColumnFilterInfos(filter.getFilterDataType(), filter.getFilterType(), filter.getFilterValue(), filter.getFilterTolerance())
                : null);
    }

    public static ComputationResultColumnFilterEntity toComputationColumnFilterEntity(ComputationResultColumnFilterInfos columnFilterInfos) {
        return ComputationResultColumnFilterEntity.builder()
                .computationColumnId(columnFilterInfos.id())
                .columnFilter(columnFilterInfos.columnFilterInfos() != null ? ColumnFilter.builder()
                        .filterDataType(columnFilterInfos.columnFilterInfos().filterDataType())
                        .filterType(columnFilterInfos.columnFilterInfos().filterType())
                        .filterValue(columnFilterInfos.columnFilterInfos().filterValue())
                        .filterTolerance(columnFilterInfos.columnFilterInfos().filterTolerance())
                        .build() : null)
                .build();
    }
}


