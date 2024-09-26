/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.mapper;

import org.gridsuite.spreadsheetconfig.server.dto.SpreadsheetConfigDto;
import org.gridsuite.spreadsheetconfig.server.dto.CustomColumnDto;
import org.gridsuite.spreadsheetconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.spreadsheetconfig.server.entities.CustomColumnEntity;
import org.springframework.stereotype.Component;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Component
public class SpreadsheetConfigMapper {

    public SpreadsheetConfigDto toDto(SpreadsheetConfigEntity entity) {
        return SpreadsheetConfigDto.builder()
                .id(entity.getId())
                .sheetType(entity.getSheetType())
                .customColumns(entity.getCustomColumns().stream()
                        .map(this::toCustomColumnDto)
                        .toList())
                .build();
    }

    public SpreadsheetConfigEntity toEntity(SpreadsheetConfigDto dto) {
        SpreadsheetConfigEntity entity = SpreadsheetConfigEntity.builder()
                .id(dto.getId())
                .sheetType(dto.getSheetType())
                .build();

        if (dto.getCustomColumns() != null) {
            entity.setCustomColumns(dto.getCustomColumns().stream()
                    .map(columnDto -> toCustomColumnEntity(columnDto, entity))
                    .toList());
        }

        return entity;
    }

    public CustomColumnDto toCustomColumnDto(CustomColumnEntity entity) {
        return CustomColumnDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .formula(entity.getFormula())
                .build();
    }

    public CustomColumnEntity toCustomColumnEntity(CustomColumnDto dto, SpreadsheetConfigEntity spreadsheetConfig) {
        return CustomColumnEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .formula(dto.getFormula())
                .spreadsheetConfig(spreadsheetConfig)
                .build();
    }
}
