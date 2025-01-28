/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.constants.SheetType;
import org.gridsuite.studyconfig.server.dto.CustomColumnInfos;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SpreadsheetConfigService spreadsheetConfigService;

    @Autowired
    private SpreadsheetConfigRepository spreadsheetConfigRepository;

    @AfterEach
    public void tearDown() {
        spreadsheetConfigRepository.deleteAll();
    }

    @Test
    void testCreate() throws Exception {
        SpreadsheetConfigInfos configToCreate = new SpreadsheetConfigInfos(null, SheetType.BATTERY, createCustomColumns());

        UUID configUuid = postSpreadsheetConfig(configToCreate);
        SpreadsheetConfigInfos createdConfig = getSpreadsheetConfig(configUuid);

        assertThat(createdConfig)
                .usingRecursiveComparison()
                .ignoringFields("id", "customColumns.id")
                .isEqualTo(configToCreate);
        assertThat(createdConfig.id()).isNotNull();
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        SpreadsheetConfigInfos invalidConfig = new SpreadsheetConfigInfos(null, null, createCustomColumns());

        String invalidConfigJson = mapper.writeValueAsString(invalidConfig);

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .content(invalidConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRead() throws Exception {
        SpreadsheetConfigInfos configToRead = new SpreadsheetConfigInfos(null, SheetType.BUS, createCustomColumns());

        UUID configUuid = saveAndReturnId(configToRead);

        SpreadsheetConfigInfos receivedConfig = getSpreadsheetConfig(configUuid);

        assertThat(receivedConfig)
                .usingRecursiveComparison()
                .ignoringFields("id", "customColumns.id")
                .isEqualTo(configToRead);
        assertThat(receivedConfig.id()).isEqualTo(configUuid);
    }

    @Test
    void testGetMetadata() throws Exception {
        SpreadsheetConfigInfos configToRead = new SpreadsheetConfigInfos(null, SheetType.BUS, createCustomColumns());

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
        SpreadsheetConfigInfos configToUpdate = new SpreadsheetConfigInfos(null, SheetType.BATTERY, createCustomColumns());

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigInfos invalidUpdate = new SpreadsheetConfigInfos(configUuid, null, createUpdatedCustomColumns());

        String invalidUpdateJson = mapper.writeValueAsString(invalidUpdate);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(invalidUpdateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        SpreadsheetConfigInfos configToUpdate = new SpreadsheetConfigInfos(null, SheetType.BATTERY, createCustomColumns());

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigInfos updatedConfig = new SpreadsheetConfigInfos(configUuid, SheetType.BUS, createUpdatedCustomColumns());

        String updatedConfigJson = mapper.writeValueAsString(updatedConfig);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(updatedConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigInfos retrievedConfig = getSpreadsheetConfig(configUuid);

        assertThat(retrievedConfig)
                .usingRecursiveComparison()
                .ignoringFields("customColumns.id")
                .isEqualTo(updatedConfig);
    }

    @Test
    void testDelete() throws Exception {
        SpreadsheetConfigInfos configToDelete = new SpreadsheetConfigInfos(null, SheetType.BATTERY, createCustomColumns());

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
        SpreadsheetConfigInfos config1 = new SpreadsheetConfigInfos(null, SheetType.BATTERY, createCustomColumns());
        SpreadsheetConfigInfos config2 = new SpreadsheetConfigInfos(null, SheetType.BUS, createUpdatedCustomColumns());

        saveAndReturnId(config1);
        saveAndReturnId(config2);

        List<SpreadsheetConfigInfos> receivedConfigs = getAllSpreadsheetConfigs();

        assertThat(receivedConfigs).hasSize(2);
    }

    @Test
    void testDuplicate() throws Exception {
        SpreadsheetConfigInfos configToCreate = new SpreadsheetConfigInfos(null, SheetType.BATTERY, createCustomColumns());
        UUID configUuid = postSpreadsheetConfig(configToCreate);

        UUID duplicatedConfigUuid = duplicateSpreadsheetConfig(configUuid);

        SpreadsheetConfigInfos duplicatedConfig = getSpreadsheetConfig(duplicatedConfigUuid);
        assertThat(duplicatedConfig)
                .usingRecursiveComparison()
                .ignoringFields("id", "customColumns.id")
                .isEqualTo(configToCreate);
        assertThat(duplicatedConfig.id()).isNotEqualTo(configUuid);
    }

    @Test
    void testDuplicateNonExistent() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE + "/duplicate")
                        .queryParam("duplicateFrom", nonExistentUuid.toString()))
                .andExpect(status().isNotFound());
    }

    private List<CustomColumnInfos> createCustomColumns() {
        return Arrays.asList(
                new CustomColumnInfos("cust_a", "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", "idA"),
                new CustomColumnInfos("cust_b", "var_minP + 1", null, "idB"),
                new CustomColumnInfos("cust_c", "cust_b + 1", "[\"cust_b\"]", "idC"),
                new CustomColumnInfos("cust_d", "5 + 2", null, "idD")
        );
    }

    private List<CustomColumnInfos> createUpdatedCustomColumns() {
        return Arrays.asList(
                new CustomColumnInfos("cust_x", "cust_y * 2", "[\"cust_y\"]", "idX"),
                new CustomColumnInfos("cust_y", "var_maxP - 1", null, "idY"),
                new CustomColumnInfos("cust_z", "cust_x / 2", "[\"cust_x\"]", "idZ")
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
        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE + "/duplicate")
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
}
