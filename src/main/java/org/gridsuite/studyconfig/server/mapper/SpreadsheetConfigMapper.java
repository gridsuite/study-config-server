/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.constants.SortDirection;
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.SpreadsheetColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;

import java.util.ArrayList;
import java.util.List;

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
                entity.getSpreadsheetColumnFilter().stream()
                    .map(CommonFiltersMapper::toSpreadSheetColumnFilterInfos)
                    .toList(),
                entity.getGlobalFilters().stream()
                    .map(CommonFiltersMapper::toGlobalFilterDto)
                    .toList(),
                entity.getNodeAliases(),
                (entity.getSortColumnId() != null && entity.getSortDirection() != null) ? new SortConfig(entity.getSortColumnId(), entity.getSortDirection().name().toLowerCase()) : null
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
            List<SpreadsheetColumnFilterEntity> cols = dto.columns().stream()
                    .map(CommonFiltersMapper::toSpreadSheetColumnFilterEntity)
                    .toList();
            entity.setSpreadsheetColumnFilter(cols);
            cols.forEach(c -> c.setSpreadsheetConfig(entity));
        }

        if (dto.globalFilters() != null) {
            entity.setGlobalFilters(dto.globalFilters().stream()
                    .map(CommonFiltersMapper::toGlobalFilterEntity)
                    .toList());
        }
        if (dto.sortConfig() != null) {
            entity.setSortColumnId(dto.sortConfig().colId());
            entity.setSortDirection(SortDirection.valueOf(dto.sortConfig().sort().toUpperCase()));
        }

        return entity;
    }
}
