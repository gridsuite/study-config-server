/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gridsuite.studyconfig.server.constants.ColumnType;
import org.gridsuite.studyconfig.server.constants.SheetType;
import org.gridsuite.studyconfig.server.dto.ColumnInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigCollectionInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.repositories.SpreadsheetConfigCollectionRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
class SpreadsheetConfigCollectionIntegrationTest {

    private static final String URI_SPREADSHEET_CONFIG_COLLECTION_BASE = "/v1/spreadsheet-config-collections";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SpreadsheetConfigService spreadsheetConfigCollectionService;

    @Autowired
    private SpreadsheetConfigCollectionRepository spreadsheetConfigCollectionRepository;

    @AfterEach
    void tearDown() {
        spreadsheetConfigCollectionRepository.deleteAll();
    }

    @Test
    void testCreateCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs());

        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);
        SpreadsheetConfigCollectionInfos createdCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(createdCollection)
                .usingRecursiveComparison()
                .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
                .isEqualTo(collectionToCreate);
        assertThat(createdCollection.id()).isNotNull();
    }

    @Test
    void testReadCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToRead = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs());

        UUID collectionUuid = saveAndReturnId(collectionToRead);

        SpreadsheetConfigCollectionInfos receivedCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(receivedCollection)
                .usingRecursiveComparison()
                .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
                .isEqualTo(collectionToRead);
        assertThat(receivedCollection.id()).isEqualTo(collectionUuid);
    }

    @Test
    void testUpdateCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToUpdate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs());

        UUID collectionUuid = saveAndReturnId(collectionToUpdate);

        SpreadsheetConfigCollectionInfos updatedCollection = new SpreadsheetConfigCollectionInfos(collectionUuid, createUpdatedSpreadsheetConfigs());

        String updatedCollectionJson = mapper.writeValueAsString(updatedCollection);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid)
                        .content(updatedCollectionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigCollectionInfos retrievedCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(retrievedCollection)
                .usingRecursiveComparison()
                .ignoringFields("spreadsheetConfigs.columns.uuid", "spreadsheetConfigs.id")
                .isEqualTo(updatedCollection);
    }

    @Test
    void testDeleteCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToDelete = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs());

        UUID collectionUuid = saveAndReturnId(collectionToDelete);

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDuplicateCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs());
        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);

        UUID duplicatedCollectionUuid = duplicateSpreadsheetConfigCollection(collectionUuid);

        SpreadsheetConfigCollectionInfos duplicatedCollection = getSpreadsheetConfigCollection(duplicatedCollectionUuid);
        assertThat(duplicatedCollection)
                .usingRecursiveComparison()
                .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
                .isEqualTo(collectionToCreate);
        assertThat(duplicatedCollection.id()).isNotEqualTo(collectionUuid);
    }

    @Test
    void testCreateDefaultCollection() throws Exception {
        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/default"))
                .andExpect(status().isCreated())
                .andReturn();

        UUID defaultCollectionUuid = mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);

        SpreadsheetConfigCollectionInfos defaultCollection = getSpreadsheetConfigCollection(defaultCollectionUuid);
        assertThat(defaultCollection.id()).isEqualTo(defaultCollectionUuid);
    }

    private List<SpreadsheetConfigInfos> createSpreadsheetConfigs() {
        List<ColumnInfos> columnInfos = Arrays.asList(
            new ColumnInfos(null, "cust_a", ColumnType.NUMBER, 1, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", "idA"),
            new ColumnInfos(null, "cust_b", ColumnType.TEXT, null, "var_minP + 1", null, "idB")
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, columnInfos),
                new SpreadsheetConfigInfos(null, "TestSheet1", SheetType.GENERATOR, columnInfos)
        );
    }

    private List<SpreadsheetConfigInfos> createUpdatedSpreadsheetConfigs() {
        List<ColumnInfos> columnInfos = Arrays.asList(
            new ColumnInfos(null, "cust_a", ColumnType.NUMBER, 1, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", "idA"),
            new ColumnInfos(null, "cust_b", ColumnType.TEXT, null, "var_minP + 2", null, "idB"),
            new ColumnInfos(null, "cust_c", ColumnType.ENUM, null, "cust_b + 2", "[\"cust_b\"]", "idC"),
            new ColumnInfos(null, "cust_d", ColumnType.NUMBER, 0, "5 + 1", null, "idD")
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "Generator", SheetType.GENERATOR, columnInfos),
                new SpreadsheetConfigInfos(null, "Generator1", SheetType.GENERATOR, columnInfos),
                new SpreadsheetConfigInfos(null, "Battery", SheetType.BATTERY, columnInfos)
        );
    }

    private SpreadsheetConfigCollectionInfos getSpreadsheetConfigCollection(UUID collectionUuid) throws Exception {
        MvcResult mvcGetResult = mockMvc.perform(get(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                mvcGetResult.getResponse().getContentAsString(),
                SpreadsheetConfigCollectionInfos.class);
    }

    private UUID postSpreadsheetConfigCollection(SpreadsheetConfigCollectionInfos collectionToCreate) throws Exception {
        String collectionToCreateJson = mapper.writeValueAsString(collectionToCreate);

        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE)
                        .content(collectionToCreateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private UUID duplicateSpreadsheetConfigCollection(UUID collectionUuid) throws Exception {
        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/duplicate")
                        .queryParam("duplicateFrom", collectionUuid.toString()))
                .andExpect(status().isCreated())
                .andReturn();

        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private UUID saveAndReturnId(SpreadsheetConfigCollectionInfos collection) {
        return spreadsheetConfigCollectionService.createSpreadsheetConfigCollection(collection);
    }
}
