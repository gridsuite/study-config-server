/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.spreadsheetconfig.server.constants.SheetType;
import org.gridsuite.spreadsheetconfig.server.dto.CustomColumnDto;
import org.gridsuite.spreadsheetconfig.server.dto.SpreadsheetConfigDto;
import org.gridsuite.spreadsheetconfig.server.repositories.SpreadsheetConfigRepository;
import org.gridsuite.spreadsheetconfig.server.service.SpreadsheetConfigService;
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
class SpreadsheetConfigControllerTest {

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
        SpreadsheetConfigDto configToCreate = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BATTERIES)
                .customColumns(createCustomColumns())
                .build();

        UUID configUuid = postSpreadsheetConfig(configToCreate);
        SpreadsheetConfigDto createdConfig = getSpreadsheetConfig(configUuid);

        assertThat(createdConfig)
                .usingRecursiveComparison()
                .ignoringFields("id", "customColumns.id")
                .isEqualTo(configToCreate);
        assertThat(createdConfig.getId()).isNotNull();
        assertThat(createdConfig.getCustomColumns()).allMatch(column -> column.getId() != null);
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        SpreadsheetConfigDto invalidConfig = SpreadsheetConfigDto.builder()
                .sheetType(null)  // SheetType is required
                .customColumns(createCustomColumns())
                .build();

        String invalidConfigJson = mapper.writeValueAsString(invalidConfig);

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE)
                        .content(invalidConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRead() throws Exception {
        SpreadsheetConfigDto configToRead = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BUSES)
                .customColumns(createCustomColumns())
                .build();

        UUID configUuid = saveAndReturnId(configToRead);

        SpreadsheetConfigDto receivedConfig = getSpreadsheetConfig(configUuid);

        assertThat(receivedConfig)
                .usingRecursiveComparison()
                .ignoringFields("id", "customColumns.id")
                .isEqualTo(configToRead);
        assertThat(receivedConfig.getId()).isEqualTo(configUuid);
        assertThat(receivedConfig.getCustomColumns()).allMatch(column -> column.getId() != null);
    }

    @Test
    void testReadNonExistent() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        mockMvc.perform(get(URI_SPREADSHEET_CONFIG_GET_PUT + nonExistentUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        SpreadsheetConfigDto configToUpdate = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BATTERIES)
                .customColumns(createCustomColumns())
                .build();

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigDto invalidUpdate = SpreadsheetConfigDto.builder()
                .id(configUuid)
                .sheetType(null)  // SheetType is required
                .customColumns(createUpdatedCustomColumns())
                .build();

        String invalidUpdateJson = mapper.writeValueAsString(invalidUpdate);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(invalidUpdateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        SpreadsheetConfigDto configToUpdate = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BATTERIES)
                .customColumns(createCustomColumns())
                .build();

        UUID configUuid = saveAndReturnId(configToUpdate);

        SpreadsheetConfigDto updatedConfig = SpreadsheetConfigDto.builder()
                .id(configUuid)
                .sheetType(SheetType.BUSES)
                .customColumns(createUpdatedCustomColumns())
                .build();

        String updatedConfigJson = mapper.writeValueAsString(updatedConfig);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid)
                        .content(updatedConfigJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigDto retrievedConfig = getSpreadsheetConfig(configUuid);

        assertThat(retrievedConfig)
                .usingRecursiveComparison()
                .ignoringFields("customColumns.id")
                .isEqualTo(updatedConfig);
    }

    @Test
    void testDelete() throws Exception {
        SpreadsheetConfigDto configToDelete = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BATTERIES)
                .customColumns(createCustomColumns())
                .build();

        UUID configUuid = saveAndReturnId(configToDelete);

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid))
                .andExpect(status().isNoContent());

        List<SpreadsheetConfigDto> storedConfigs = getAllSpreadsheetConfigs();

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
        SpreadsheetConfigDto config1 = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BATTERIES)
                .customColumns(createCustomColumns())
                .build();
        SpreadsheetConfigDto config2 = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BUSES)
                .customColumns(createUpdatedCustomColumns())
                .build();

        saveAndReturnId(config1);
        saveAndReturnId(config2);

        List<SpreadsheetConfigDto> receivedConfigs = getAllSpreadsheetConfigs();

        assertThat(receivedConfigs).hasSize(2);
    }

    @Test
    void testDuplicate() throws Exception {
        SpreadsheetConfigDto configToCreate = SpreadsheetConfigDto.builder()
                .sheetType(SheetType.BATTERIES)
                .customColumns(createCustomColumns())
                .build();
        UUID configUuid = postSpreadsheetConfig(configToCreate);

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE + "/duplicate")
                        .queryParam("duplicateFrom", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());

        UUID duplicatedConfigUuid = duplicateSpreadsheetConfig(configUuid);

        SpreadsheetConfigDto duplicatedConfig = getSpreadsheetConfig(duplicatedConfigUuid);
        assertThat(duplicatedConfig)
                .usingRecursiveComparison()
                .ignoringFields("id", "customColumns.id")
                .isEqualTo(configToCreate);
        assertThat(duplicatedConfig.getId()).isNotEqualTo(configUuid);
    }

    @Test
    void testDuplicateNonExistent() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_BASE + "/duplicate")
                        .queryParam("duplicateFrom", nonExistentUuid.toString()))
                .andExpect(status().isNotFound());
    }

    private List<CustomColumnDto> createCustomColumns() {
        return Arrays.asList(
                new CustomColumnDto(null, "cust_a", "cust_b + cust_c"),
                new CustomColumnDto(null, "cust_b", "var_minP + 1"),
                new CustomColumnDto(null, "cust_c", "cust_b + 1"),
                new CustomColumnDto(null, "cust_d", "5 + 2")
        );
    }

    private List<CustomColumnDto> createUpdatedCustomColumns() {
        return Arrays.asList(
                new CustomColumnDto(null, "cust_x", "cust_y * 2"),
                new CustomColumnDto(null, "cust_y", "var_maxP - 1"),
                new CustomColumnDto(null, "cust_z", "cust_x / 2")
        );
    }

    private SpreadsheetConfigDto getSpreadsheetConfig(UUID configUuid) throws Exception {
        MvcResult mvcGetResult = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_GET_PUT + configUuid))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                mvcGetResult.getResponse().getContentAsString(),
                SpreadsheetConfigDto.class);
    }

    private UUID postSpreadsheetConfig(SpreadsheetConfigDto configToCreate) throws Exception {
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

    private List<SpreadsheetConfigDto> getAllSpreadsheetConfigs() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_BASE))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<SpreadsheetConfigDto>>() { });
    }

    private UUID saveAndReturnId(SpreadsheetConfigDto config) {
        return spreadsheetConfigService.createSpreadsheetConfig(config);
    }
}
