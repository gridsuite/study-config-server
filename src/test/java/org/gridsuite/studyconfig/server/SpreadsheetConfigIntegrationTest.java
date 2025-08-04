/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.gridsuite.studyconfig.server.constants.ColumnType;
import org.gridsuite.studyconfig.server.constants.SheetType;
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.repositories.SpreadsheetConfigRepository;
import org.gridsuite.studyconfig.server.service.SpreadsheetConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@SpringBootTest
@AutoConfigureMockMvc
class SpreadsheetConfigIntegrationTest {

    private static final String URI_SPREADSHEET_CONFIG_BASE = "/v1/spreadsheet-configs";
    private static final String URI_SPREADSHEET_CONFIG_GET_PUT = URI_SPREADSHEET_CONFIG_BASE + "/";
    private static final String URI_COLUMN_BASE = "/columns";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SpreadsheetConfigService spreadsheetConfigService;

    @Autowired
    private SpreadsheetConfigRepository spreadsheetConfigRepository;

    @AfterEach
    void tearDown() {
        spreadsheetConfigRepository.deleteAll();
    }

    @Test
    void testCreate() throws Exception {
        SpreadsheetConfigInfos configToCreate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumnsWithFilters(), createGlobalFilters(), List.of("alias1", "alias2"));

        UUID configUuid = postSpreadsheetConfig(configToCreate);
        SpreadsheetConfigInfos createdConfig = getSpreadsheetConfig(configUuid);

        assertThat(createdConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "id", "columns.id", "globalFilters.id")
                .isEqualTo(configToCreate);
        assertThat(createdConfig.id()).isNotNull();
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        SpreadsheetConfigInfos invalidConfig = new SpreadsheetConfigInfos(null, "Battery", null, createColumns(), null, List.of());

        String invalidConfigJson = mapper.writeValueAsString(invalidConfig);

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .content(invalidConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRead() throws Exception {
        SpreadsheetConfigInfos configToRead = new SpreadsheetConfigInfos(null, "Battery", SheetType.BUS, createColumnsWithFilters(), createGlobalFilters(), List.of("alias"));

        UUID configUuid = saveAndReturnId(configToRead);

        SpreadsheetConfigInfos receivedConfig = getSpreadsheetConfig(configUuid);

        assertThat(receivedConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "id", "columns.id", "globalFilters.id")
                .isEqualTo(configToRead);
        assertThat(receivedConfig.id()).isEqualTo(configUuid);
    }

    @Test
    void testGetMetadata() throws Exception {
        SpreadsheetConfigInfos configToRead = new SpreadsheetConfigInfos(null, "Battery", SheetType.BUS, createColumnsWithFilters(), createGlobalFilters(), List.of());

        UUID configUuid = saveAndReturnId(configToRead);

        List<MetadataInfos> metadata = getMetadataInfos(configUuid);

        assertThat(metadata).hasSize(1);
        assertThat(metadata.get(0).id()).isEqualTo(configUuid);
        assertThat(metadata.get(0).sheetType()).isEqualTo(SheetType.BUS);

    }

    private List<MetadataInfos> getMetadataInfos(UUID configUuid) throws Exception {
        MvcResult receivedMetadata = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_BASE + "/metadata")
                        .queryParam("ids", configUuid.toString()))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                receivedMetadata.getResponse().getContentAsString(),
                new TypeReference<>() { });
    }

    @Test
    void testReadNonExistent() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        mockMvc.perform(get(URI_SPREADSHEET_CONFIG_GET_PUT + nonExistentUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        SpreadsheetConfigInfos configToUpdate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumnsWithFilters(), createGlobalFilters(), List.of());

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigInfos invalidUpdate = new SpreadsheetConfigInfos(configUuid, "Test", null, createUpdatedColumns(), null, List.of());

        String invalidUpdateJson = mapper.writeValueAsString(invalidUpdate);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(invalidUpdateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        SpreadsheetConfigInfos configToUpdate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumnsWithFilters(), createGlobalFilters(), List.of("alias1"));

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigInfos updatedConfig = new SpreadsheetConfigInfos(configUuid, "Bus", SheetType.BUS, createUpdatedColumnsWithFilters(), createUpdatedGlobalFilters(), List.of("newAlias"));

        String updatedConfigJson = mapper.writeValueAsString(updatedConfig);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(updatedConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigInfos retrievedConfig = getSpreadsheetConfig(configUuid);

        assertThat(retrievedConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "columns.id", "globalFilters.id", "id")
                .isEqualTo(updatedConfig);
    }

    @Test
    void testDelete() throws Exception {
        SpreadsheetConfigInfos configToDelete = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumnsWithFilters(), createGlobalFilters(), List.of());

        UUID configUuid = saveAndReturnId(configToDelete);

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid))
                .andExpect(status().isNoContent());

        List<SpreadsheetConfigInfos> storedConfigs = getAllSpreadsheetConfigs();

        assertThat(storedConfigs).isEmpty();
    }

    @Test
    void testDeleteNonExistent() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_GET_PUT + nonExistentUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        SpreadsheetConfigInfos config1 = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumnsWithFilters(), createGlobalFilters(), List.of());
        SpreadsheetConfigInfos config2 = new SpreadsheetConfigInfos(null, "Bus", SheetType.BUS, createUpdatedColumnsWithFilters(), createUpdatedGlobalFilters(), List.of());

        saveAndReturnId(config1);
        saveAndReturnId(config2);

        List<SpreadsheetConfigInfos> receivedConfigs = getAllSpreadsheetConfigs();

        assertThat(receivedConfigs).hasSize(2);
    }

    @Test
    void testDuplicate() throws Exception {
        SpreadsheetConfigInfos configToCreate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumnsWithFilters(), createGlobalFilters(), List.of("alias1,", "alias2"));
        UUID configUuid = postSpreadsheetConfig(configToCreate);

        UUID duplicatedConfigUuid = duplicateSpreadsheetConfig(configUuid);

        SpreadsheetConfigInfos duplicatedConfig = getSpreadsheetConfig(duplicatedConfigUuid);
        assertThat(duplicatedConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "id", "columns.id", "globalFilters.id")
                .isEqualTo(configToCreate);
        assertThat(duplicatedConfig.id()).isNotEqualTo(configUuid);
    }

    @Test
    void testDuplicateNonExistent() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .queryParam("duplicateFrom", nonExistentUuid.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, List.of(), null, List.of());
        UUID configId = saveAndReturnId(config);

        ColumnInfos columnToCreate = new ColumnInfos(null, "new_column", ColumnType.NUMBER, 2, "x + 1", "[\"x\"]", "newId", null, null, null, null, true);

        MvcResult result = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE)
                .content(mapper.writeValueAsString(columnToCreate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UUID columnId = mapper.readValue(result.getResponse().getContentAsString(), UUID.class);

        ColumnInfos createdColumn = getColumn(configId, columnId);
        assertThat(createdColumn)
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(columnToCreate);

        // Create a column with a filter
        ColumnInfos columnWithFilter = new ColumnInfos(null, "new_column_with_filter", ColumnType.NUMBER, 2, "x + 1", "[\"x\"]", "newId",
                "text", "equals", "test-value", null, true);

        MvcResult resultWithFilter = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE)
                .content(mapper.writeValueAsString(columnWithFilter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UUID columnWithFilterId = mapper.readValue(resultWithFilter.getResponse().getContentAsString(), UUID.class);
        ColumnInfos createdColumnWithFilter = getColumn(configId, columnWithFilterId);
        assertThat(createdColumnWithFilter)
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(columnWithFilter);
    }

    @Test
    void testUpdateColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        UUID columnId = savedConfig.columns().get(0).uuid();

        ColumnInfos columnUpdate = new ColumnInfos(columnId, "updated_column", ColumnType.TEXT, null, "new_formula", "[]", "updatedId",
                "text", "equals", "updated-value", null, true);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + columnId)
                .content(mapper.writeValueAsString(columnUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ColumnInfos updatedColumn = getColumn(configId, columnId);
        assertThat(updatedColumn).isEqualTo(columnUpdate);
    }

    @Test
    void testDeleteColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        UUID columnId = savedConfig.columns().get(0).uuid();

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + columnId))
                .andExpect(status().isNoContent());

        SpreadsheetConfigInfos configAfterDelete = getSpreadsheetConfig(configId);
        assertThat(configAfterDelete.columns())
                .extracting(ColumnInfos::uuid)
                .isNotEmpty()
                .doesNotContain(columnId);
    }

    @Test
    void testDuplicateColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        UUID columnId = savedConfig.columns().get(0).uuid();
        assertThat(savedConfig.columns()).hasSize(4);

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + columnId + "/duplicate"))
                .andExpect(status().isNoContent());

        SpreadsheetConfigInfos configAfterDuplicate = getSpreadsheetConfig(configId);
        assertThat(configAfterDuplicate.columns()).hasSize(5);
        ColumnInfos columnInfos = configAfterDuplicate.columns().get(0);
        ColumnInfos duplicatedColumnInfos = configAfterDuplicate.columns().get(1);

        assertThat(columnInfos.uuid()).isNotEqualTo(duplicatedColumnInfos.uuid());
        assertEquals(columnInfos.id() + "(1)", duplicatedColumnInfos.id());
        assertEquals(columnInfos.name() + "(1)", duplicatedColumnInfos.name());
        assertThat(columnInfos.visible()).isEqualTo(duplicatedColumnInfos.visible());
        assertThat(columnInfos.formula()).isEqualTo(duplicatedColumnInfos.formula());
        assertThat(columnInfos.dependencies()).isEqualTo(duplicatedColumnInfos.dependencies());
        assertThat(columnInfos.precision()).isEqualTo(duplicatedColumnInfos.precision());

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + columnId + "/duplicate"))
                .andExpect(status().isNoContent());
        configAfterDuplicate = getSpreadsheetConfig(configId);
        assertThat(configAfterDuplicate.columns()).hasSize(6);
        duplicatedColumnInfos = configAfterDuplicate.columns().get(1);
        assertThat(columnInfos.uuid()).isNotEqualTo(duplicatedColumnInfos.uuid());
        assertEquals(columnInfos.id() + "(2)", duplicatedColumnInfos.id());
        assertEquals(columnInfos.name() + "(2)", duplicatedColumnInfos.name());

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + UUID.randomUUID() + "/duplicate"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        UUID columnId = savedConfig.columns().get(0).uuid();

        ColumnInfos column = getColumn(configId, columnId);
        assertThat(column).isNotNull();
        assertThat(column.uuid()).isEqualTo(columnId);
    }

    @Test
    void testReorderColumns() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "ReorderTest", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        // get the saved config to retrieve column UUIDs
        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        List<ColumnInfos> originalColumns = savedConfig.columns();
        assertThat(originalColumns).hasSize(4);

        // reverse the original order
        List<UUID> reorderedColumnIds = originalColumns.stream()
            .map(ColumnInfos::uuid)
            .collect(Collectors.toList());
        Collections.reverse(reorderedColumnIds);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/reorder")
                .content(mapper.writeValueAsString(reorderedColumnIds))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // verify the new order
        SpreadsheetConfigInfos updatedConfig = getSpreadsheetConfig(configId);
        List<ColumnInfos> reorderedColumns = updatedConfig.columns();

        assertThat(reorderedColumns).hasSize(originalColumns.size());

        for (int i = 0; i < reorderedColumns.size(); i++) {
            assertThat(reorderedColumns.get(i).uuid())
                .isEqualTo(originalColumns.get(originalColumns.size() - 1 - i).uuid());
        }
    }

    @Test
    void testUpdateColumnStates() throws Exception {
        // Create config with multiple columns
        List<ColumnInfos> columns = Arrays.asList(
                new ColumnInfos(null, "col1", ColumnType.TEXT, null, "formula1", null, "id1", null, null, null, null, true),
                new ColumnInfos(null, "col2", ColumnType.NUMBER, 2, "formula2", null, "id2", null, null, null, null, true),
                new ColumnInfos(null, "col3", ColumnType.BOOLEAN, null, "formula3", null, "id3", null, null, null, null, false)
        );

        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "TestConfig", SheetType.BATTERY, columns, null, List.of());
        UUID configId = saveAndReturnId(config);

        // Get the saved config to retrieve column UUIDs
        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        List<ColumnInfos> savedColumns = savedConfig.columns();
        assertThat(savedColumns).hasSize(3);

        // Prepare column state updates: reorder and change visibility
        List<ColumnStateUpdateInfos> stateUpdates = Arrays.asList(
                new ColumnStateUpdateInfos(savedColumns.get(0).uuid(), false, 2),  // Hide and move to position 2
                new ColumnStateUpdateInfos(savedColumns.get(1).uuid(), true, 1),   // Keep visible, move to position 1
                new ColumnStateUpdateInfos(savedColumns.get(2).uuid(), true, 0)    // Show and move to position 0
        );

        // Call the update column states endpoint
        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/states")
                        .content(mapper.writeValueAsString(stateUpdates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the updates
        SpreadsheetConfigInfos updatedConfig = getSpreadsheetConfig(configId);
        List<ColumnInfos> updatedColumns = updatedConfig.columns();

        // Check visibility updates
        ColumnInfos firstColumn = updatedColumns.stream()
                .filter(col -> col.uuid().equals(savedColumns.get(0).uuid()))
                .findFirst().orElseThrow();
        assertThat(firstColumn.visible()).isFalse();

        ColumnInfos thirdColumn = updatedColumns.stream()
                .filter(col -> col.uuid().equals(savedColumns.get(2).uuid()))
                .findFirst().orElseThrow();
        assertThat(thirdColumn.visible()).isTrue();

        // Check order: should be col3, col2, col1
        assertThat(updatedColumns.get(0).uuid()).isEqualTo(savedColumns.get(2).uuid());
        assertThat(updatedColumns.get(1).uuid()).isEqualTo(savedColumns.get(1).uuid());
        assertThat(updatedColumns.get(2).uuid()).isEqualTo(savedColumns.get(0).uuid());
    }

    @Test
    void testUpdateColumnStatesInvalidColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "TestConfig", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        UUID nonExistentColumnId = UUID.randomUUID();
        List<ColumnStateUpdateInfos> stateUpdates = List.of(
                new ColumnStateUpdateInfos(nonExistentColumnId, false, 0)
        );

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/states")
                        .content(mapper.writeValueAsString(stateUpdates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateColumnStatesNonExistentConfig() throws Exception {
        UUID nonExistentConfigId = UUID.randomUUID();
        List<ColumnStateUpdateInfos> stateUpdates = List.of(
                new ColumnStateUpdateInfos(UUID.randomUUID(), true, 0)
        );

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + nonExistentConfigId + URI_COLUMN_BASE + "/states")
                        .content(mapper.writeValueAsString(stateUpdates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateColumnStatesWithInvalidData() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "TestConfig", SheetType.BATTERY, createColumns(), null, List.of());
        UUID configId = saveAndReturnId(config);

        // Missing required fields (columnId and visible are null)
        String invalidJson = "[{\"columnId\": null, \"visible\": null, \"order\": 0}]";

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/states")
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRenameSpreadsheetConfig() throws Exception {
        SpreadsheetConfigInfos configToRename = new SpreadsheetConfigInfos(null, "OriginalName", SheetType.BATTERY, createColumns(), List.of(), List.of());
        UUID configUuid = saveAndReturnId(configToRename);

        String newName = "RenamedConfig";
        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid + "/name")
                .content(newName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigInfos renamedConfig = getSpreadsheetConfig(configUuid);
        assertThat(renamedConfig.name()).isEqualTo(newName);

        assertThat(renamedConfig)
                .usingRecursiveComparison()
                .ignoringFields("name", "columns.uuid", "id", "columns.id")
                .isEqualTo(configToRename);
    }

    @Test
    void testRenameNonExistentSpreadsheetConfig() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + nonExistentUuid + "/name")
                .content("NewName")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSetGlobalFiltersForSpreadsheetConfig() throws Exception {
        // Create a spreadsheet config with existing global filters
        SpreadsheetConfigInfos configWithFilters = new SpreadsheetConfigInfos(
                null, "ConfigWithFilters", SheetType.BATTERY, createColumns(), createGlobalFilters(), List.of());
        UUID configId = saveAndReturnId(configWithFilters);

        // Initial config should have two filters
        SpreadsheetConfigInfos initialConfig = getSpreadsheetConfig(configId);
        assertThat(initialConfig.globalFilters()).hasSize(2);

        // Create new filters to set
        List<GlobalFilterInfos> filtersToSet = List.of(
                GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Replacement Filter").recent(false).build()
        );

        // Call the endpoint to set the filters
        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + configId + "/global-filters")
                        .content(mapper.writeValueAsString(filtersToSet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the filters were replaced (not added)
        SpreadsheetConfigInfos updatedConfig = getSpreadsheetConfig(configId);
        assertThat(updatedConfig.globalFilters()).hasSize(1);
        assertThat(updatedConfig.globalFilters())
                .extracting(GlobalFilterInfos::label)
                .containsExactly("Replacement Filter");
    }

    @Test
    void testSetGlobalFiltersToNonExistentConfig() throws Exception {
        UUID nonExistentConfigId = UUID.randomUUID();
        List<GlobalFilterInfos> filtersToAdd = List.of(
                GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Test Filter").recent(false).build()
        );

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_GET_PUT + nonExistentConfigId + "/global-filters")
                        .content(mapper.writeValueAsString(filtersToAdd))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private List<ColumnInfos> createColumns() {
        return Arrays.asList(
                new ColumnInfos(null, "cust_a", ColumnType.BOOLEAN, null, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", "idA", null, null, null, null, true),
                new ColumnInfos(null, "cust_b", ColumnType.NUMBER, 0, "var_minP + 1", null, "idB", null, null, null, null, true),
                new ColumnInfos(null, "cust_c", ColumnType.NUMBER, 2, "cust_b + 1", "[\"cust_b\"]", "idC", null, null, null, null, true),
                new ColumnInfos(null, "cust_d", ColumnType.TEXT, null, "5 + 2", null, "idD", null, null, null, null, true)
        );
    }

    private List<ColumnInfos> createColumnsWithFilters() {
        return Arrays.asList(
                new ColumnInfos(null, "cust_a", ColumnType.BOOLEAN, null, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", "idA",
                        "text", "equals", "test-value", null, true),
                new ColumnInfos(null, "cust_b", ColumnType.NUMBER, 0, "var_minP + 1", null, "idB",
                        "number", "greaterThan", "100", 0.5, true),
                new ColumnInfos(null, "cust_c", ColumnType.NUMBER, 2, "cust_b + 1", "[\"cust_b\"]", "idC",
                        "text", "startsWith", "prefix", null, true),
                new ColumnInfos(null, "cust_d", ColumnType.TEXT, null, "5 + 2", null, "idD",
                        null, null, null, null, true)
        );
    }

    private List<ColumnInfos> createUpdatedColumns() {
        return Arrays.asList(
                new ColumnInfos(null, "cust_x", ColumnType.BOOLEAN, null, "cust_y * 2", "[\"cust_y\"]", "idX", null, null, null, null, true),
                new ColumnInfos(null, "cust_y", ColumnType.NUMBER, 1, "var_maxP - 1", null, "idY", null, null, null, null, true),
                new ColumnInfos(null, "cust_z", ColumnType.NUMBER, 0, "cust_x / 2", "[\"cust_x\"]", "idZ", null, null, null, null, true)
        );
    }

    private List<ColumnInfos> createUpdatedColumnsWithFilters() {
        return Arrays.asList(
                new ColumnInfos(null, "cust_x", ColumnType.BOOLEAN, null, "cust_y * 2", "[\"cust_y\"]", "idX",
                        "text", "contains", "updated-value", null, true),
                new ColumnInfos(null, "cust_y", ColumnType.NUMBER, 1, "var_maxP - 1", null, "idY",
                        "number", "lessThan", "50", 0.1, true),
                new ColumnInfos(null, "cust_z", ColumnType.NUMBER, 0, "cust_x / 2", "[\"cust_x\"]", "idZ",
                        null, null, null, null, true)  // No filter on this column
        );
    }

    private List<GlobalFilterInfos> createGlobalFilters() {
        return Arrays.asList(
                GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Global Filter 1").recent(false).build(),
                GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Global Filter 2").recent(false).build()
        );
    }

    private List<GlobalFilterInfos> createUpdatedGlobalFilters() {
        return List.of(
                GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Updated Global Filter").recent(false).build()
        );
    }

    private SpreadsheetConfigInfos getSpreadsheetConfig(UUID configUuid) throws Exception {
        MvcResult mvcGetResult = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                mvcGetResult.getResponse().getContentAsString(),
                SpreadsheetConfigInfos.class);
    }

    private UUID postSpreadsheetConfig(SpreadsheetConfigInfos configToCreate) throws Exception {
        String configToCreateJson = mapper.writeValueAsString(configToCreate);

        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .content(configToCreateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private UUID duplicateSpreadsheetConfig(UUID configUuid) throws Exception {
        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .queryParam("duplicateFrom", configUuid.toString()))
                .andExpect(status().isCreated())
                .andReturn();

        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private List<SpreadsheetConfigInfos> getAllSpreadsheetConfigs() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_BASE))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() { });
    }

    private UUID saveAndReturnId(SpreadsheetConfigInfos config) {
        return spreadsheetConfigService.createSpreadsheetConfig(config);
    }

    private ColumnInfos getColumn(UUID configId, UUID columnId) throws Exception {
        MvcResult result = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + columnId))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(result.getResponse().getContentAsString(), ColumnInfos.class);
    }
}
