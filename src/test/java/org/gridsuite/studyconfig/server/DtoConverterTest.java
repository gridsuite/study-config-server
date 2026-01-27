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
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.dto.SpreadSheetColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.entities.ColumnFilterEntity;
import org.gridsuite.studyconfig.server.entities.GlobalFilterEntity;
import org.gridsuite.studyconfig.server.entities.SpreadsheetColumnFilterEntity;
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
                    .spreadsheetColumnFilter(Arrays.asList(
                            SpreadsheetColumnFilterEntity.builder()
                                .name("Column1")
                                .formula("A+B")
                                .id("id1")
                                .filter(ColumnFilterEntity.builder()
                                        .filterDataType("text")
                                        .filterType("contains")
                                        .filterValue("test").build())
                                .visible(false)
                                .build(),
                            SpreadsheetColumnFilterEntity.builder().name("Column2").formula("C*D").id("id2").filter(ColumnFilterEntity.builder().build()).build()
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
                        assertThat(d.columns().get(0).id()).isEqualTo("id1");
                        assertThat(d.columns().get(0).visible()).isFalse();
                        assertThat(d.columns().get(0).filterDataType()).isEqualTo("text");
                        assertThat(d.columns().get(0).filterType()).isEqualTo("contains");
                        assertThat(d.columns().get(0).filterValue()).isEqualTo("test");

                        assertThat(d.columns().get(1).name()).isEqualTo("Column2");
                        assertThat(d.columns().get(1).formula()).isEqualTo("C*D");
                        assertThat(d.columns().get(1).id()).isEqualTo("id2");
                        assertThat(d.columns().get(1).filterDataType()).isNull();
                        assertThat(d.columns().get(1).filterType()).isNull();
                        assertThat(d.columns().get(1).filterValue()).isNull();
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
                            new SpreadSheetColumnFilterInfos(null, "Column1", ColumnType.NUMBER, 1, "X+Y", "[\"col1\", \"col2\"]",
                                    "id1", "number", "greaterThan", "100", 0.5, true),
                            new SpreadSheetColumnFilterInfos(null, "Column2", ColumnType.NUMBER, 2, "Z*W", "[\"col1\"]",
                                    "id2", null, null, null, null, true)
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
                        assertThat(e.getSpreadsheetColumnFilter()).hasSize(2);
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getName()).isEqualTo("Column1");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getFormula()).isEqualTo("X+Y");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getId()).isEqualTo("id1");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getFilter().getFilterDataType()).isEqualTo("number");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getFilter().getFilterType()).isEqualTo("greaterThan");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getFilter().getFilterValue()).isEqualTo("100");
                        assertThat(e.getSpreadsheetColumnFilter().get(0).getFilter().getFilterTolerance()).isEqualTo(0.5);
                        assertThat(e.getSpreadsheetColumnFilter().get(0).isVisible()).isTrue();

                        assertThat(e.getSpreadsheetColumnFilter().get(1).getName()).isEqualTo("Column2");
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getFormula()).isEqualTo("Z*W");
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getId()).isEqualTo("id2");
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getDependencies()).isEqualTo("[\"col1\"]");
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getFilter().getFilterDataType()).isNull();
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getFilter().getFilterType()).isNull();
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getFilter().getFilterValue()).isNull();
                        assertThat(e.getSpreadsheetColumnFilter().get(1).getFilter().getFilterTolerance()).isNull();
                        assertThat(e.getSpreadsheetColumnFilter().get(1).isVisible()).isTrue();

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
            SpreadsheetColumnFilterEntity entity = SpreadsheetColumnFilterEntity.builder()
                    .name("TestColumn")
                    .formula("A+B+C")
                    .id("idTest")
                    .filter(ColumnFilterEntity.builder()
                            .filterDataType("text")
                            .filterType("startsWith")
                            .filterValue("prefix")
                            .filterTolerance(null)
                            .build()).build();

            SpreadSheetColumnFilterInfos dto = CommonFiltersMapper.toSpreadSheetColumnFilterInfos(entity);

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
                        assertThat(d.visible()).isTrue();
                    });
        }

        @Test
        void testConversionToEntityOfColumnWithFilter() {
            SpreadSheetColumnFilterInfos dto = new SpreadSheetColumnFilterInfos(null, "TestColumn",
                    ColumnType.NUMBER, 3, "X*Y*Z", "[\"col1\", \"col2\"]", "idTest",
                    "number", "lessThan", "50.5", 0.1, true);

            SpreadsheetColumnFilterEntity column = CommonFiltersMapper.toSpreadSheetColumnFilterEntity(dto);

            assertThat(column)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getName()).isEqualTo("TestColumn");
                        assertThat(e.getFormula()).isEqualTo("X*Y*Z");
                        assertThat(e.getDependencies()).isEqualTo("[\"col1\", \"col2\"]");
                        assertThat(e.getId()).isEqualTo("idTest");
                        assertThat(e.isVisible()).isTrue();
                        assertThat(e.getFilter()).isNotNull();
                        assertThat(e.getFilter().getFilterDataType()).isEqualTo("number");
                        assertThat(e.getFilter().getFilterType()).isEqualTo("lessThan");
                        assertThat(e.getFilter().getFilterValue()).isEqualTo("50.5");
                        assertThat(e.getFilter().getFilterTolerance()).isEqualTo(0.1);
                    });
        }

        @Test
        void testConversionOfColumnWithoutFilter() {
            SpreadSheetColumnFilterInfos dto = new SpreadSheetColumnFilterInfos(
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
                    null,
                    true
                    );

            SpreadsheetColumnFilterEntity entity = CommonFiltersMapper.toSpreadSheetColumnFilterEntity(dto);
            SpreadSheetColumnFilterInfos convertedDto = CommonFiltersMapper.toSpreadSheetColumnFilterInfos(entity);

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
                .minValue(10)
                .maxValue(20)
                    .build();

            GlobalFilterInfos dto = CommonFiltersMapper.toGlobalFilterDto(entity);

            assertThat(dto)
                    .as("DTO conversion result")
                    .satisfies(d -> {
                        assertThat(d.uuid()).isEqualTo(filterId);
                        assertThat(d.label()).isEqualTo("TestGlobalFilter");
                        assertThat(d.minValue()).isEqualTo(10);
                        assertThat(d.maxValue()).isEqualTo(20);
                    });
        }

        @Test
        void testConversionToEntityOfGlobalFilter() {
            UUID uuid = UUID.randomUUID();
            UUID filterId = UUID.randomUUID();
            GlobalFilterInfos dto = GlobalFilterInfos.builder()
                .id(uuid)
                .uuid(filterId)
                .filterType("country")
                .label("TestGlobalFilter")
                .recent(false)
                .minValue(5)
                .maxValue(15)
                .build();

            GlobalFilterEntity entity = CommonFiltersMapper.toGlobalFilterEntity(dto);

            assertThat(entity)
                    .as("Entity conversion result")
                    .satisfies(e -> {
                        assertThat(e.getUuid()).isEqualTo(filterId);
                        assertThat(e.getLabel()).isEqualTo("TestGlobalFilter");
                        assertThat(e.getMinValue()).isEqualTo(5);
                        assertThat(e.getMaxValue()).isEqualTo(15);
                    });
        }
    }
}
