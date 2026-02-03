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

    public static ComputationResultColumnFilterInfos toComputationColumnFilterInfos(ComputationResultColumnFilterEntity entity) {
        ColumnFilter filter = entity.getColumnFilter();
        return new ComputationResultColumnFilterInfos(entity.getComputationColumnId(), filter != null ?
                new ColumnFilterInfos(filter.getFilterDataType(), filter.getFilterType(), filter.getFilterValue(), filter.getFilterTolerance())
                : null);
    }

    public static ComputationResultColumnFilterEntity toComputationColumnFilterEntity(ComputationResultColumnFilterInfos columnFilterInfos) {
        return ComputationResultColumnFilterEntity.builder()
                .computationColumnId(columnFilterInfos.columnId())
                .columnFilter(columnFilterInfos.columnFilterInfos() != null ? ColumnFilter.builder()
                        .filterDataType(columnFilterInfos.columnFilterInfos().filterDataType())
                        .filterType(columnFilterInfos.columnFilterInfos().filterType())
                        .filterValue(columnFilterInfos.columnFilterInfos().filterValue())
                        .filterTolerance(columnFilterInfos.columnFilterInfos().filterTolerance())
                        .build() : null)
                .build();
    }
}


