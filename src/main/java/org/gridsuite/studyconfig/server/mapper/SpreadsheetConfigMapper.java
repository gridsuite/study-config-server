/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.dto.CustomColumnInfos;
import org.gridsuite.studyconfig.server.entities.CustomColumnEmbeddable;
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
                entity.getSheetType(),
                entity.getCustomColumns().stream()
                    .map(SpreadsheetConfigMapper::toCustomColumnDto)
                    .toList()
        );
    }

    public static MetadataInfos toMetadataDto(SpreadsheetConfigEntity entity) {
        return new MetadataInfos(entity.getId(), entity.getSheetType());
    }

    public static SpreadsheetConfigEntity toEntity(SpreadsheetConfigInfos dto) {
        SpreadsheetConfigEntity entity = SpreadsheetConfigEntity.builder()
                .sheetType(dto.sheetType())
                .build();

        if (dto.customColumns() != null) {
            entity.setCustomColumns(dto.customColumns().stream()
                    .map(SpreadsheetConfigMapper::toCustomColumnEmbeddable)
                    .toList());
        }

        return entity;
    }

    public static CustomColumnInfos toCustomColumnDto(CustomColumnEmbeddable entity) {
        return new CustomColumnInfos(entity.getName(), entity.getFormula(), entity.getDependencies(), entity.getId());
    }

    public static CustomColumnEmbeddable toCustomColumnEmbeddable(CustomColumnInfos dto) {
        return new CustomColumnEmbeddable(dto.name(), dto.formula(), dto.dependencies(), dto.id());
    }
}
