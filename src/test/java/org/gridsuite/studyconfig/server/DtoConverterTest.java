/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import org.assertj.core.api.WithAssertions;
import org.gridsuite.studyconfig.server.constants.SheetType;
import org.gridsuite.studyconfig.server.dto.CustomColumnInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.CustomColumnEmbeddable;
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
                    .customColumns(Arrays.asList(
                            CustomColumnEmbeddable.builder().name("Column1").formula("A+B").build(),
                            CustomColumnEmbeddable.builder().name("Column2").formula("C*D").build()
                    ))
                    .build();

            SpreadsheetConfigInfos dto = SpreadsheetConfigMapper.toDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.id()).isEqualTo(id);
                        assertThat(d.sheetType()).isEqualTo(SheetType.BATTERY);
                        assertThat(d.customColumns()).hasSize(2);
                        assertThat(d.customColumns().get(0).name()).isEqualTo("Column1");
                        assertThat(d.customColumns().get(0).formula()).isEqualTo("A+B");
                        assertThat(d.customColumns().get(1).name()).isEqualTo("Column2");
                        assertThat(d.customColumns().get(1).formula()).isEqualTo("C*D");
                    });
        }

        @Test
        void testConversionToEntityOfSpreadsheetConfig() {
            UUID id = UUID.randomUUID();
            SpreadsheetConfigInfos dto = new SpreadsheetConfigInfos(
                    id,
                    SheetType.BUS,
                    Arrays.asList(
                            new CustomColumnInfos("Column1", "X+Y", "[\"col1\", \"col2\"]"),
                            new CustomColumnInfos("Column2", "Z*W", "[\"col1\"]")
                    )
            );

            SpreadsheetConfigEntity entity = SpreadsheetConfigMapper.toEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getSheetType()).isEqualTo(SheetType.BUS);
                        assertThat(e.getCustomColumns()).hasSize(2);
                        assertThat(e.getCustomColumns().get(0).getName()).isEqualTo("Column1");
                        assertThat(e.getCustomColumns().get(0).getFormula()).isEqualTo("X+Y");
                        assertThat(e.getCustomColumns().get(0).getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getCustomColumns().get(1).getName()).isEqualTo("Column2");
                        assertThat(e.getCustomColumns().get(1).getFormula()).isEqualTo("Z*W");
                        assertThat(e.getCustomColumns().get(1).getDependencies()).isEqualTo("[\"col1\"]");
                    });
        }
    }

    @Nested
    class CustomColumnConverterTest {
        @Test
        void testConversionToDtoOfCustomColumn() {
            CustomColumnEmbeddable entity = CustomColumnEmbeddable.builder()
                    .name("TestColumn")
                    .formula("A+B+C")
                    .build();

            CustomColumnInfos dto = SpreadsheetConfigMapper.toCustomColumnDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.name()).isEqualTo("TestColumn");
                        assertThat(d.formula()).isEqualTo("A+B+C");
                    });
        }

        @Test
        void testConversionToEmbeddableOfCustomColumn() {
            CustomColumnInfos dto = new CustomColumnInfos("TestColumn", "X*Y*Z", "[\"col1\", \"col2\"]");

            CustomColumnEmbeddable customColumnEmbeddable = SpreadsheetConfigMapper.toCustomColumnEmbeddable(dto);

            assertThat(customColumnEmbeddable)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getName()).isEqualTo("TestColumn");
                        assertThat(e.getFormula()).isEqualTo("X*Y*Z");
                        assertThat(e.getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                    });
        }
    }
}
