/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;

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
                    .toList()
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

        if (dto.columns() != null) {
            entity.setColumns(dto.columns().stream()
                    .map(SpreadsheetConfigMapper::toColumnEntity)
                    .toList());
        }

        return entity;
    }

    public static ColumnInfos toColumnDto(ColumnEntity entity) {
        return new ColumnInfos(entity.getUuid(), entity.getName(), entity.getType(), entity.getPrecision(), entity.getFormula(), entity.getDependencies(), entity.getId());
    }

    public static ColumnEntity toColumnEntity(ColumnInfos dto) {
        return ColumnEntity.builder()
                .name(dto.name())
                .type(dto.type())
                .precision(dto.precision())
                .formula(dto.formula())
                .dependencies(dto.dependencies())
                .id(dto.id())
                .build();
    }
}
