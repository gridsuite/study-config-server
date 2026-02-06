/**
* Copyright (c) 2025, RTE (http://www.rte-france.com)
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.dto.ComputationResultColumnFilterInfos;
import org.gridsuite.studyconfig.server.entities.*;
import org.gridsuite.studyconfig.server.entities.computationresult.ColumnEntity;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class ComputationResultFiltersMapper {

    private ComputationResultFiltersMapper() { }

    public static ComputationResultColumnFilterInfos toComputationColumnFilterInfos(ColumnEntity entity) {
        ColumnFilterEntity filter = entity.getColumnFilter();
        return new ComputationResultColumnFilterInfos(entity.getComputationColumnId(), filter != null ?
                new ColumnFilterInfos(filter.getFilterDataType(), filter.getFilterType(), filter.getFilterValue(), filter.getFilterTolerance())
                : null);
    }

    public static ColumnEntity toComputationColumnFilterEntity(ComputationResultColumnFilterInfos columnFilterInfos) {
        return ColumnEntity.builder()
                .computationColumnId(columnFilterInfos.columnId())
                .columnFilter(columnFilterInfos.columnFilterInfos() != null ? ColumnFilterEntity.builder()
                        .filterDataType(columnFilterInfos.columnFilterInfos().filterDataType())
                        .filterType(columnFilterInfos.columnFilterInfos().filterType())
                        .filterValue(columnFilterInfos.columnFilterInfos().filterValue())
                        .filterTolerance(columnFilterInfos.columnFilterInfos().filterTolerance())
                        .build() : null)
                .build();
    }
}


