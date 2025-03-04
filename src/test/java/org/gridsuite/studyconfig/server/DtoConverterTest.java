/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import org.assertj.core.api.WithAssertions;
import org.gridsuite.studyconfig.server.constants.ColumnType;
import org.gridsuite.studyconfig.server.constants.SheetType;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
public class DtoConverterTest implements WithAssertions {

    @Nested
    class SpreadsheetConfigConverterTest {

        @Test
        void testConversionToDtoOfSpreadsheetConfig() {
            UUID id = UUID.randomUUID();
            SpreadsheetConfigEntity entity = SpreadsheetConfigEntity.builder()
                    .id(id)
                    .sheetType(SheetType.BATTERY)
                    .columns(Arrays.asList(
                            ColumnEntity.builder().name("Column1").formula("A+B").id("id1").build(),
                            ColumnEntity.builder().name("Column2").formula("C*D").id("id2").build()
                    ))
                    .build();

            SpreadsheetConfigInfos dto = SpreadsheetConfigMapper.toDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.id()).isEqualTo(id);
                        assertThat(d.sheetType()).isEqualTo(SheetType.BATTERY);
                        assertThat(d.columns()).hasSize(2);
                        assertThat(d.columns().get(0).name()).isEqualTo("Column1");
                        assertThat(d.columns().get(0).formula()).isEqualTo("A+B");
                        assertThat(d.columns().get(0).id()).isEqualTo("id1");
                        assertThat(d.columns().get(1).name()).isEqualTo("Column2");
                        assertThat(d.columns().get(1).formula()).isEqualTo("C*D");
                        assertThat(d.columns().get(1).id()).isEqualTo("id2");
                    });
        }

        @Test
        void testConversionToEntityOfSpreadsheetConfig() {
            UUID id = UUID.randomUUID();
            SpreadsheetConfigInfos dto = new SpreadsheetConfigInfos(
                    id,
                    "TestSheet",
                    SheetType.BUS,
                    Arrays.asList(
                            new ColumnInfos(null, "Column1", ColumnType.NUMBER, 1, "X+Y", "[\"col1\", \"col2\"]", "id1"),
                            new ColumnInfos(null, "Column2", ColumnType.NUMBER, 2, "Z*W", "[\"col1\"]", "id2")
                    )
            );

            SpreadsheetConfigEntity entity = SpreadsheetConfigMapper.toEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getSheetType()).isEqualTo(SheetType.BUS);
                        assertThat(e.getColumns()).hasSize(2);
                        assertThat(e.getColumns().get(0).getName()).isEqualTo("Column1");
                        assertThat(e.getColumns().get(0).getFormula()).isEqualTo("X+Y");
                        assertThat(e.getColumns().get(0).getId()).isEqualTo("id1");
                        assertThat(e.getColumns().get(0).getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getColumns().get(1).getName()).isEqualTo("Column2");
                        assertThat(e.getColumns().get(1).getFormula()).isEqualTo("Z*W");
                        assertThat(e.getColumns().get(1).getId()).isEqualTo("id2");
                        assertThat(e.getColumns().get(1).getDependencies()).isEqualTo("[\"col1\"]");
                    });
        }
    }

    @Nested
    class ColumnConverterTest {
        @Test
        void testConversionToDtoOfColumn() {
            ColumnEntity entity = ColumnEntity.builder()
                    .name("TestColumn")
                    .formula("A+B+C")
                    .id("idTest")
                    .build();

            ColumnInfos dto = SpreadsheetConfigMapper.toColumnDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.name()).isEqualTo("TestColumn");
                        assertThat(d.formula()).isEqualTo("A+B+C");
                        assertThat(d.id()).isEqualTo("idTest");
                    });
        }

        @Test
        void testConversionToEmbeddableOfColumn() {
            ColumnInfos dto = new ColumnInfos(null, "TestColumn", ColumnType.NUMBER, 3, "X*Y*Z", "[\"col1\", \"col2\"]", "idTest");
            ColumnEntity column = SpreadsheetConfigMapper.toColumnEntity(dto);

            assertThat(column)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getName()).isEqualTo("TestColumn");
                        assertThat(e.getFormula()).isEqualTo("X*Y*Z");
                        assertThat(e.getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getId()).isEqualTo("idTest");
                    });
        }
    }
}
