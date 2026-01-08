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
import org.gridsuite.studyconfig.server.dto.ColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.SpreadsheetColumnEntity;
import org.gridsuite.studyconfig.server.entities.ColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetConfigEntity;
import org.gridsuite.studyconfig.server.mapper.CommonFiltersMapper;
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
                            SpreadsheetColumnEntity.builder()
                                .name("Column1")
                                .formula("A+B")
                                .columnFilter(ColumnFilterEntity.builder()
                                        .columnId("id1")
                                        .filterDataType("text")
                                        .filterType("contains")
                                        .filterValue("test").build())
                                .visible(false)
                                .build(),
                            SpreadsheetColumnEntity.builder().name("Column2").formula("C*D").columnFilter(ColumnFilterEntity.builder()
                            .columnId("id2").build()).build()
                    ))
                    .globalFilters(Arrays.asList(
                            GlobalFilterEntity.builder()
                                    .id(UUID.randomUUID())
                                    .label("GlobalFilter1")
                                    .build(),
                            GlobalFilterEntity.builder()
                                    .id(UUID.randomUUID())
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
                        assertThat(d.columns().get(0).columnFilterInfos().columnId()).isEqualTo("id1");
                        assertThat(d.columns().get(0).columnFilterInfos().filterDataType()).isEqualTo("text");
                        assertThat(d.columns().get(0).columnFilterInfos().filterType()).isEqualTo("contains");
                        assertThat(d.columns().get(0).columnFilterInfos().filterValue()).isEqualTo("test");
                        assertThat(d.columns().get(0).visible()).isFalse();

                        assertThat(d.columns().get(1).name()).isEqualTo("Column2");
                        assertThat(d.columns().get(1).formula()).isEqualTo("C*D");
                        assertThat(d.columns().get(1).columnFilterInfos().columnId()).isEqualTo("id2");
                        assertThat(d.columns().get(1).columnFilterInfos().filterDataType()).isNull();
                        assertThat(d.columns().get(1).columnFilterInfos().filterType()).isNull();
                        assertThat(d.columns().get(1).columnFilterInfos().filterValue()).isNull();
                        assertThat(d.columns().get(1).visible()).isTrue();

                        // Global filters assertions
                        assertThat(d.globalFilters()).hasSize(2);
                        assertThat(d.globalFilters().get(0).label()).isEqualTo("GlobalFilter1");
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
                            new ColumnInfos(null, "Column1", ColumnType.NUMBER, 1, "X+Y", "[\"col1\", \"col2\"]", true,
                                    new ColumnFilterInfos(null, "id1", "number", "greaterThan", "100", 0.5)),
                            new ColumnInfos(null, "Column2", ColumnType.NUMBER, 2, "Z*W", "[\"col1\"]", true,
                                    new ColumnFilterInfos(null, "id2", null, null, null, null))
                    ),
                    List.of(
                            GlobalFilterInfos.builder().uuid(filterId).filterType("country").label("GlobalFilter1").recent(false).build()
                    ),
                    List.of(),
                    null
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
                        assertThat(e.getColumns().get(0).getColumnFilter().getColumnId()).isEqualTo("id1");
                        assertThat(e.getColumns().get(0).getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getColumns().get(0).getColumnFilter().getFilterDataType()).isEqualTo("number");
                        assertThat(e.getColumns().get(0).getColumnFilter().getFilterType()).isEqualTo("greaterThan");
                        assertThat(e.getColumns().get(0).getColumnFilter().getFilterValue()).isEqualTo("100");
                        assertThat(e.getColumns().get(0).getColumnFilter().getFilterTolerance()).isEqualTo(0.5);
                        assertThat(e.getColumns().get(0).isVisible()).isTrue();

                        assertThat(e.getColumns().get(1).getName()).isEqualTo("Column2");
                        assertThat(e.getColumns().get(1).getFormula()).isEqualTo("Z*W");
                        assertThat(e.getColumns().get(1).getColumnFilter().getColumnId()).isEqualTo("id2");
                        assertThat(e.getColumns().get(1).getDependencies()).isEqualTo("[\"col1\"]");
                        assertThat(e.getColumns().get(1).getColumnFilter().getFilterDataType()).isNull();
                        assertThat(e.getColumns().get(1).getColumnFilter().getFilterType()).isNull();
                        assertThat(e.getColumns().get(1).getColumnFilter().getFilterValue()).isNull();
                        assertThat(e.getColumns().get(1).getColumnFilter().getFilterTolerance()).isNull();
                        assertThat(e.getColumns().get(1).isVisible()).isTrue();

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
            SpreadsheetColumnEntity entity = SpreadsheetColumnEntity.builder()
                    .name("TestColumn")
                    .formula("A+B+C")
                    .columnFilter(ColumnFilterEntity.builder()
                            .columnId("idTest")
                            .filterDataType("text")
                            .filterType("startsWith")
                            .filterValue("prefix")
                            .filterTolerance(null)
                            .build()).build();

            ColumnInfos dto = CommonFiltersMapper.toColumnDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.name()).isEqualTo("TestColumn");
                        assertThat(d.formula()).isEqualTo("A+B+C");
                        assertThat(d.columnFilterInfos().columnId()).isEqualTo("idTest");
                        assertThat(d.columnFilterInfos().filterDataType()).isEqualTo("text");
                        assertThat(d.columnFilterInfos().filterType()).isEqualTo("startsWith");
                        assertThat(d.columnFilterInfos().filterValue()).isEqualTo("prefix");
                        assertThat(d.columnFilterInfos().filterTolerance()).isNull();
                        assertThat(d.visible()).isTrue();
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
                    true,
                    new ColumnFilterInfos(null, "idTest",
                            "number",
                            "lessThan",
                            "50.5",
                            0.1));

            SpreadsheetColumnEntity column = CommonFiltersMapper.toColumnEntity(dto);

            assertThat(column)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getName()).isEqualTo("TestColumn");
                        assertThat(e.getFormula()).isEqualTo("X*Y*Z");
                        assertThat(e.getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getColumnFilter().getColumnId()).isEqualTo("idTest");
                        assertThat(e.getColumnFilter().getFilterDataType()).isEqualTo("number");
                        assertThat(e.getColumnFilter().getFilterType()).isEqualTo("lessThan");
                        assertThat(e.getColumnFilter().getFilterValue()).isEqualTo("50.5");
                        assertThat(e.getColumnFilter().getFilterTolerance()).isEqualTo(0.1);
                        assertThat(e.isVisible()).isTrue();
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
                    true,
                    new ColumnFilterInfos(null, "idTest",
                            null,
                            null,
                            null,
                            null));

            SpreadsheetColumnEntity entity = CommonFiltersMapper.toColumnEntity(dto);
            ColumnInfos convertedDto = CommonFiltersMapper.toColumnDto(entity);

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

            GlobalFilterInfos dto = CommonFiltersMapper.toGlobalFilterDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.uuid()).isEqualTo(filterId);
                        assertThat(d.label()).isEqualTo("TestGlobalFilter");
                    });
        }

        @Test
        void testConversionToEntityOfGlobalFilter() {
            UUID uuid = UUID.randomUUID();
            UUID filterId = UUID.randomUUID();
            GlobalFilterInfos dto = GlobalFilterInfos.builder().id(uuid).uuid(filterId).filterType("country").label("TestGlobalFilter").recent(false).build();

            GlobalFilterEntity entity = CommonFiltersMapper.toGlobalFilterEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getUuid()).isEqualTo(filterId);
                        assertThat(e.getLabel()).isEqualTo("TestGlobalFilter");
                    });
        }
    }
}
