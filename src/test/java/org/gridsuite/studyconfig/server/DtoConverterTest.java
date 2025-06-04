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
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.ColumnEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.studyconfig.server.mapper.SpreadsheetConfigMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
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
                            ColumnEntity.builder()
                                .name("Column1")
                                .formula("A+B")
                                .id("id1")
                                .filterDataType("text")
                                .filterType("contains")
                                .filterValue("test")
                                .build(),
                            ColumnEntity.builder().name("Column2").formula("C*D").id("id2").build()
                    ))
                    .globalFilters(Arrays.asList(
                            GlobalFilterEntity.builder()
                                .uuid(UUID.randomUUID())
                                .label("GlobalFilter1")
                                .build(),
                            GlobalFilterEntity.builder()
                                .uuid(UUID.randomUUID())
                                .label("GlobalFilter2")
                                .build()
                    ))
                    .build();

            SpreadsheetConfigInfos dto = SpreadsheetConfigMapper.toDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.id()).isEqualTo(id);
                        assertThat(d.sheetType()).isEqualTo(SheetType.BATTERY);
                        // Columns assertions
                        assertThat(d.columns()).hasSize(2);
                        assertThat(d.columns().get(0).name()).isEqualTo("Column1");
                        assertThat(d.columns().get(0).formula()).isEqualTo("A+B");
                        assertThat(d.columns().get(0).id()).isEqualTo("id1");
                        assertThat(d.columns().get(0).filterDataType()).isEqualTo("text");
                        assertThat(d.columns().get(0).filterType()).isEqualTo("contains");
                        assertThat(d.columns().get(0).filterValue()).isEqualTo("test");

                        assertThat(d.columns().get(1).name()).isEqualTo("Column2");
                        assertThat(d.columns().get(1).formula()).isEqualTo("C*D");
                        assertThat(d.columns().get(1).id()).isEqualTo("id2");
                        assertThat(d.columns().get(1).filterDataType()).isNull();
                        assertThat(d.columns().get(1).filterType()).isNull();
                        assertThat(d.columns().get(1).filterValue()).isNull();
                        // Global filters assertions
                        assertThat(d.globalFilters()).hasSize(2);
                        assertThat(d.globalFilters().get(0).uuid()).isNotNull();
                        assertThat(d.globalFilters().get(0).label()).isEqualTo("GlobalFilter1");

                        assertThat(d.globalFilters().get(1).uuid()).isNotNull();
                        assertThat(d.globalFilters().get(1).label()).isEqualTo("GlobalFilter2");
                    });
        }

        @Test
        void testConversionToEntityOfSpreadsheetConfig() {
            UUID id = UUID.randomUUID();
            UUID filterId = UUID.randomUUID();
            SpreadsheetConfigInfos dto = new SpreadsheetConfigInfos(
                    id,
                    "TestSheet",
                    SheetType.BUS,
                    Arrays.asList(
                            new ColumnInfos(null, "Column1", ColumnType.NUMBER, 1, "X+Y", "[\"col1\", \"col2\"]", "id1",
                                    "number", "greaterThan", "100", 0.5),
                            new ColumnInfos(null, "Column2", ColumnType.NUMBER, 2, "Z*W", "[\"col1\"]", "id2",
                                    null, null, null, null)
                    ),
                    List.of(
                            new GlobalFilterInfos(null, filterId, "country", null,"GlobalFilter1", false, null, null)
                    )
            );

            SpreadsheetConfigEntity entity = SpreadsheetConfigMapper.toEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getName()).isEqualTo("TestSheet");
                        assertThat(e.getSheetType()).isEqualTo(SheetType.BUS);

                        // Column assertions
                        assertThat(e.getColumns()).hasSize(2);
                        assertThat(e.getColumns().get(0).getName()).isEqualTo("Column1");
                        assertThat(e.getColumns().get(0).getFormula()).isEqualTo("X+Y");
                        assertThat(e.getColumns().get(0).getId()).isEqualTo("id1");
                        assertThat(e.getColumns().get(0).getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getColumns().get(0).getFilterDataType()).isEqualTo("number");
                        assertThat(e.getColumns().get(0).getFilterType()).isEqualTo("greaterThan");
                        assertThat(e.getColumns().get(0).getFilterValue()).isEqualTo("100");
                        assertThat(e.getColumns().get(0).getFilterTolerance()).isEqualTo(0.5);

                        assertThat(e.getColumns().get(1).getName()).isEqualTo("Column2");
                        assertThat(e.getColumns().get(1).getFormula()).isEqualTo("Z*W");
                        assertThat(e.getColumns().get(1).getId()).isEqualTo("id2");
                        assertThat(e.getColumns().get(1).getDependencies()).isEqualTo("[\"col1\"]");
                        assertThat(e.getColumns().get(1).getFilterDataType()).isNull();
                        assertThat(e.getColumns().get(1).getFilterType()).isNull();
                        assertThat(e.getColumns().get(1).getFilterValue()).isNull();
                        assertThat(e.getColumns().get(1).getFilterTolerance()).isNull();

                        // Global filter assertions
                        assertThat(e.getGlobalFilters()).hasSize(1);
                        assertThat(e.getGlobalFilters().get(0).getUuid()).isEqualTo(filterId);
                        assertThat(e.getGlobalFilters().get(0).getLabel()).isEqualTo("GlobalFilter1");
                    });
        }
    }

    @Nested
    class ColumnConverterTest {
        @Test
        void testConversionToDtoOfColumnWithFilter() {
            ColumnEntity entity = ColumnEntity.builder()
                    .name("TestColumn")
                    .formula("A+B+C")
                    .id("idTest")
                    .filterDataType("text")
                    .filterType("startsWith")
                    .filterValue("prefix")
                    .filterTolerance(null)
                    .build();

            ColumnInfos dto = SpreadsheetConfigMapper.toColumnDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.name()).isEqualTo("TestColumn");
                        assertThat(d.formula()).isEqualTo("A+B+C");
                        assertThat(d.id()).isEqualTo("idTest");
                        assertThat(d.filterDataType()).isEqualTo("text");
                        assertThat(d.filterType()).isEqualTo("startsWith");
                        assertThat(d.filterValue()).isEqualTo("prefix");
                        assertThat(d.filterTolerance()).isNull();
                    });
        }

        @Test
        void testConversionToEntityOfColumnWithFilter() {
            ColumnInfos dto = new ColumnInfos(
                    null,
                    "TestColumn",
                    ColumnType.NUMBER,
                    3,
                    "X*Y*Z",
                    "[\"col1\", \"col2\"]",
                    "idTest",
                    "number",
                    "lessThan",
                    "50.5",
                    0.1);

            ColumnEntity column = SpreadsheetConfigMapper.toColumnEntity(dto);

            assertThat(column)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getName()).isEqualTo("TestColumn");
                        assertThat(e.getFormula()).isEqualTo("X*Y*Z");
                        assertThat(e.getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getId()).isEqualTo("idTest");
                        assertThat(e.getFilterDataType()).isEqualTo("number");
                        assertThat(e.getFilterType()).isEqualTo("lessThan");
                        assertThat(e.getFilterValue()).isEqualTo("50.5");
                        assertThat(e.getFilterTolerance()).isEqualTo(0.1);
                    });
        }

        @Test
        void testConversionOfColumnWithoutFilter() {
            ColumnInfos dto = new ColumnInfos(
                    null,
                    "TestColumn",
                    ColumnType.TEXT,
                    null,
                    "X*Y*Z",
                    "[\"col1\", \"col2\"]",
                    "idTest",
                    null,
                    null,
                    null,
                    null);

            ColumnEntity entity = SpreadsheetConfigMapper.toColumnEntity(dto);
            ColumnInfos convertedDto = SpreadsheetConfigMapper.toColumnDto(entity);

            assertThat(convertedDto)
                    .as("Round-trip conversion result")
                    .usingRecursiveComparison()
                    .isEqualTo(dto);
        }
    }

    @Nested
    class GlobalFilterConverterTest {
        @Test
        void testConversionToDtoOfGlobalFilter() {
            UUID uuid = UUID.randomUUID();
            UUID filterId = UUID.randomUUID();
            GlobalFilterEntity entity = GlobalFilterEntity.builder()
                    .id(uuid)
                    .uuid(filterId)
                    .label("TestGlobalFilter")
                    .build();

            GlobalFilterInfos dto = SpreadsheetConfigMapper.toGlobalFilterDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.id()).isEqualTo(uuid);
                        assertThat(d.uuid()).isEqualTo(filterId);
                        assertThat(d.label()).isEqualTo("TestGlobalFilter");
                    });
        }

        @Test
        void testConversionToEntityOfGlobalFilter() {
            UUID uuid = UUID.randomUUID();
            UUID filterId = UUID.randomUUID();
            GlobalFilterInfos dto = new GlobalFilterInfos(uuid, filterId, "country", null,"TestGlobalFilter", false, null, null);

            GlobalFilterEntity entity = SpreadsheetConfigMapper.toGlobalFilterEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getUuid()).isEqualTo(filterId);
                        assertThat(e.getLabel()).isEqualTo("TestGlobalFilter");
                    });
        }
    }
}
