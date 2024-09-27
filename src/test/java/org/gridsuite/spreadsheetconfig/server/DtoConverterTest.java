/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server;

import org.assertj.core.api.WithAssertions;
import org.gridsuite.spreadsheetconfig.server.constants.SheetType;
import org.gridsuite.spreadsheetconfig.server.dto.CustomColumnDto;
import org.gridsuite.spreadsheetconfig.server.dto.SpreadsheetConfigDto;
import org.gridsuite.spreadsheetconfig.server.entities.CustomColumnEntity;
import org.gridsuite.spreadsheetconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.spreadsheetconfig.server.mapper.SpreadsheetConfigMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
public class DtoConverterTest implements WithAssertions {

    private final SpreadsheetConfigMapper mapper = new SpreadsheetConfigMapper();

    @Nested
    class SpreadsheetConfigConverterTest {

        @Test
        void testConversionToDtoOfSpreadsheetConfig() {
            UUID id = UUID.randomUUID();
            SpreadsheetConfigEntity entity = SpreadsheetConfigEntity.builder()
                    .id(id)
                    .sheetType(SheetType.BATTERIES)
                    .customColumns(Arrays.asList(
                            CustomColumnEntity.builder().id(UUID.randomUUID()).name("Column1").formula("A+B").build(),
                            CustomColumnEntity.builder().id(UUID.randomUUID()).name("Column2").formula("C*D").build()
                    ))
                    .build();

            SpreadsheetConfigDto dto = mapper.toDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.getId()).isEqualTo(id);
                        assertThat(d.getSheetType()).isEqualTo(SheetType.BATTERIES);
                        assertThat(d.getCustomColumns()).hasSize(2);
                        assertThat(d.getCustomColumns().get(0).getName()).isEqualTo("Column1");
                        assertThat(d.getCustomColumns().get(0).getFormula()).isEqualTo("A+B");
                        assertThat(d.getCustomColumns().get(1).getName()).isEqualTo("Column2");
                        assertThat(d.getCustomColumns().get(1).getFormula()).isEqualTo("C*D");
                    });
        }

        @Test
        void testConversionToEntityOfSpreadsheetConfig() {
            UUID id = UUID.randomUUID();
            SpreadsheetConfigDto dto = SpreadsheetConfigDto.builder()
                    .id(id)
                    .sheetType(SheetType.BUSES)
                    .customColumns(Arrays.asList(
                            CustomColumnDto.builder().id(UUID.randomUUID()).name("Column1").formula("X+Y").build(),
                            CustomColumnDto.builder().id(UUID.randomUUID()).name("Column2").formula("Z*W").build()
                    ))
                    .build();

            SpreadsheetConfigEntity entity = mapper.toEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getId()).isEqualTo(id);
                        assertThat(e.getSheetType()).isEqualTo(SheetType.BUSES);
                        assertThat(e.getCustomColumns()).hasSize(2);
                        assertThat(e.getCustomColumns().get(0).getName()).isEqualTo("Column1");
                        assertThat(e.getCustomColumns().get(0).getFormula()).isEqualTo("X+Y");
                        assertThat(e.getCustomColumns().get(1).getName()).isEqualTo("Column2");
                        assertThat(e.getCustomColumns().get(1).getFormula()).isEqualTo("Z*W");
                    });
        }
    }

    @Nested
    class CustomColumnConverterTest {
        @Test
        void testConversionToDtoOfCustomColumn() {
            UUID id = UUID.randomUUID();
            CustomColumnEntity entity = CustomColumnEntity.builder()
                    .id(id)
                    .name("TestColumn")
                    .formula("A+B+C")
                    .build();

            CustomColumnDto dto = mapper.toCustomColumnDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.getId()).isEqualTo(id);
                        assertThat(d.getName()).isEqualTo("TestColumn");
                        assertThat(d.getFormula()).isEqualTo("A+B+C");
                    });
        }

        @Test
        void testConversionToEntityOfCustomColumn() {
            UUID id = UUID.randomUUID();
            CustomColumnDto dto = CustomColumnDto.builder()
                    .id(id)
                    .name("TestColumn")
                    .formula("X*Y*Z")
                    .build();

            SpreadsheetConfigEntity spreadsheetConfig = new SpreadsheetConfigEntity();
            CustomColumnEntity entity = mapper.toCustomColumnEntity(dto, spreadsheetConfig);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getId()).isEqualTo(id);
                        assertThat(e.getName()).isEqualTo("TestColumn");
                        assertThat(e.getFormula()).isEqualTo("X*Y*Z");
                        assertThat(e.getSpreadsheetConfig()).isEqualTo(spreadsheetConfig);
                    });
        }
    }
}
