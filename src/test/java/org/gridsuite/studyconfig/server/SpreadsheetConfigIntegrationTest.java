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
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.MetadataInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
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

import static org.assertj.core.api.Assertions.assertThat;
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
        SpreadsheetConfigInfos configToCreate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());

        UUID configUuid = postSpreadsheetConfig(configToCreate);
        SpreadsheetConfigInfos createdConfig = getSpreadsheetConfig(configUuid);

        assertThat(createdConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "id", "columns.id")
                .isEqualTo(configToCreate);
        assertThat(createdConfig.id()).isNotNull();
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        SpreadsheetConfigInfos invalidConfig = new SpreadsheetConfigInfos(null, "Battery", null, createColumns());

        String invalidConfigJson = mapper.writeValueAsString(invalidConfig);

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .content(invalidConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRead() throws Exception {
        SpreadsheetConfigInfos configToRead = new SpreadsheetConfigInfos(null, "Battery", SheetType.BUS, createColumns());

        UUID configUuid = saveAndReturnId(configToRead);

        SpreadsheetConfigInfos receivedConfig = getSpreadsheetConfig(configUuid);

        assertThat(receivedConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "id", "columns.id")
                .isEqualTo(configToRead);
        assertThat(receivedConfig.id()).isEqualTo(configUuid);
    }

    @Test
    void testGetMetadata() throws Exception {
        SpreadsheetConfigInfos configToRead = new SpreadsheetConfigInfos(null, "Battery", SheetType.BUS, createColumns());

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
        SpreadsheetConfigInfos configToUpdate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigInfos invalidUpdate = new SpreadsheetConfigInfos(configUuid, "Test", null, createUpdatedColumns());

        String invalidUpdateJson = mapper.writeValueAsString(invalidUpdate);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(invalidUpdateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        SpreadsheetConfigInfos configToUpdate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigInfos updatedConfig = new SpreadsheetConfigInfos(configUuid, "Bus", SheetType.BUS, createUpdatedColumns());

        String updatedConfigJson = mapper.writeValueAsString(updatedConfig);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(updatedConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigInfos retrievedConfig = getSpreadsheetConfig(configUuid);

        assertThat(retrievedConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "columns.id")
                .isEqualTo(updatedConfig);
    }

    @Test
    void testDelete() throws Exception {
        SpreadsheetConfigInfos configToDelete = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());

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
        SpreadsheetConfigInfos config1 = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());
        SpreadsheetConfigInfos config2 = new SpreadsheetConfigInfos(null, "Bus", SheetType.BUS, createUpdatedColumns());

        saveAndReturnId(config1);
        saveAndReturnId(config2);

        List<SpreadsheetConfigInfos> receivedConfigs = getAllSpreadsheetConfigs();

        assertThat(receivedConfigs).hasSize(2);
    }

    @Test
    void testDuplicate() throws Exception {
        SpreadsheetConfigInfos configToCreate = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());
        UUID configUuid = postSpreadsheetConfig(configToCreate);

        UUID duplicatedConfigUuid = duplicateSpreadsheetConfig(configUuid);

        SpreadsheetConfigInfos duplicatedConfig = getSpreadsheetConfig(duplicatedConfigUuid);
        assertThat(duplicatedConfig)
                .usingRecursiveComparison()
                .ignoringFields("columns.uuid", "id", "columns.id")
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
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, List.of());
        UUID configId = saveAndReturnId(config);

        ColumnInfos columnToCreate = new ColumnInfos(null, "new_column", ColumnType.NUMBER, 2, "x + 1", "[\"x\"]", "newId");

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
    }

    @Test
    void testUpdateColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());
        UUID configId = saveAndReturnId(config);

        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        UUID columnId = savedConfig.columns().get(0).uuid();

        ColumnInfos columnUpdate = new ColumnInfos(columnId, "updated_column", ColumnType.TEXT, null, "new_formula", "[]", "updatedId");

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configId + URI_COLUMN_BASE + "/" + columnId)
                .content(mapper.writeValueAsString(columnUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ColumnInfos updatedColumn = getColumn(configId, columnId);
        assertThat(updatedColumn).isEqualTo(columnUpdate);
    }

    @Test
    void testDeleteColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());
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
    void testGetColumn() throws Exception {
        SpreadsheetConfigInfos config = new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, createColumns());
        UUID configId = saveAndReturnId(config);

        SpreadsheetConfigInfos savedConfig = getSpreadsheetConfig(configId);
        UUID columnId = savedConfig.columns().get(0).uuid();

        ColumnInfos column = getColumn(configId, columnId);
        assertThat(column).isNotNull();
        assertThat(column.uuid()).isEqualTo(columnId);
    }

    private List<ColumnInfos> createColumns() {
        return Arrays.asList(
                new ColumnInfos(null, "cust_a", ColumnType.BOOLEAN, null, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", "idA"),
                new ColumnInfos(null, "cust_b", ColumnType.NUMBER, 0, "var_minP + 1", null, "idB"),
                new ColumnInfos(null, "cust_c", ColumnType.NUMBER, 2, "cust_b + 1", "[\"cust_b\"]", "idC"),
                new ColumnInfos(null, "cust_d", ColumnType.TEXT, null, "5 + 2", null, "idD")
        );
    }

    private List<ColumnInfos> createUpdatedColumns() {
        return Arrays.asList(
                new ColumnInfos(null, "cust_x", ColumnType.BOOLEAN, null, "cust_y * 2", "[\"cust_y\"]", "idX"),
                new ColumnInfos(null, "cust_y", ColumnType.NUMBER, 1, "var_maxP - 1", null, "idY"),
                new ColumnInfos(null, "cust_z", ColumnType.NUMBER, 0, "cust_x / 2", "[\"cust_x\"]", "idZ")
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
