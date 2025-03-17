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

import java.util.*;

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
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);

        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);
        SpreadsheetConfigCollectionInfos createdCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(createdCollection)
            .usingRecursiveComparison()
            .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
            .ignoringExpectedNullFields()
            .isEqualTo(collectionToCreate);
        assertThat(createdCollection.id()).isNotNull();
    }

    @Test
    void testCreateCollectionWithAliases() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), List.of("alias1", "alias2", "alias3"));

        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);
        SpreadsheetConfigCollectionInfos createdCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(createdCollection)
            .usingRecursiveComparison()
            .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
            .ignoringExpectedNullFields()
            .isEqualTo(collectionToCreate);
        assertThat(createdCollection.id()).isNotNull();
    }

    @Test
    void testReadCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToRead = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);

        UUID collectionUuid = saveAndReturnId(collectionToRead);

        SpreadsheetConfigCollectionInfos receivedCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(receivedCollection)
            .usingRecursiveComparison()
            .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
            .ignoringExpectedNullFields()
            .isEqualTo(collectionToRead);
        assertThat(receivedCollection.id()).isEqualTo(collectionUuid);
    }

    @Test
    void testUpdateCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToUpdate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);

        UUID collectionUuid = saveAndReturnId(collectionToUpdate);

        SpreadsheetConfigCollectionInfos updatedCollection = new SpreadsheetConfigCollectionInfos(collectionUuid, createUpdatedSpreadsheetConfigs(), null);

        String updatedCollectionJson = mapper.writeValueAsString(updatedCollection);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid)
                        .content(updatedCollectionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigCollectionInfos retrievedCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(retrievedCollection)
            .usingRecursiveComparison()
            .ignoringFields("spreadsheetConfigs.columns.uuid", "spreadsheetConfigs.id")
            .ignoringExpectedNullFields()
            .isEqualTo(updatedCollection);
    }

    @Test
    void testDeleteCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToDelete = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);

        UUID collectionUuid = saveAndReturnId(collectionToDelete);

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDuplicateCollection() throws Exception {
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);

        UUID duplicatedCollectionUuid = duplicateSpreadsheetConfigCollection(collectionUuid);

        SpreadsheetConfigCollectionInfos duplicatedCollection = getSpreadsheetConfigCollection(duplicatedCollectionUuid);
        assertThat(duplicatedCollection)
            .usingRecursiveComparison()
            .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id")
            .ignoringExpectedNullFields()
            .isEqualTo(collectionToCreate);
        assertThat(duplicatedCollection.id()).isNotEqualTo(collectionUuid);
    }

    @Test
    void testMergeModelsIntoNewCollection() throws Exception {
        // create a first collection with 2 configs
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);
        List<UUID> configIds = getSpreadsheetConfigCollection(collectionUuid).spreadsheetConfigs().stream().map(SpreadsheetConfigInfos::id).toList();
        assertThat(configIds).hasSize(2);
        // create a second collection duplicating + merging these existing Configs
        UUID mergedCollectionUuid = postMergeSpreadsheetConfigsIntoCollection(configIds);
        List<UUID> duplicatedConfigIds = getSpreadsheetConfigCollection(mergedCollectionUuid).spreadsheetConfigs().stream().map(SpreadsheetConfigInfos::id).toList();

        assertThat(mergedCollectionUuid).isNotEqualTo(collectionUuid);
        assertThat(duplicatedConfigIds).hasSameSizeAs(configIds);
        assertThat(duplicatedConfigIds.stream().sorted().toList()).isNotEqualTo(configIds.stream().sorted().toList());
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

    @Test
    void testAddSpreadsheetConfigToCollection() throws Exception {
        SpreadsheetConfigCollectionInfos initialCollection = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(initialCollection);

        List<ColumnInfos> columnInfos = Arrays.asList(
            new ColumnInfos(null, "new_col", ColumnType.NUMBER, 1, "formula", "[\"dep\"]", "idNew")
        );
        SpreadsheetConfigInfos newConfig = new SpreadsheetConfigInfos(null, "NewSheet", SheetType.BATTERY, columnInfos);

        String newConfigJson = mapper.writeValueAsString(newConfig);
        MvcResult mvcResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid + "/spreadsheet-configs")
                .content(newConfigJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        UUID newConfigId = mapper.readValue(mvcResult.getResponse().getContentAsString(), UUID.class);
        assertThat(newConfigId).isNotNull();

        SpreadsheetConfigCollectionInfos updatedCollection = getSpreadsheetConfigCollection(collectionUuid);
        assertThat(updatedCollection.spreadsheetConfigs()).hasSize(initialCollection.spreadsheetConfigs().size() + 1);
        assertThat(updatedCollection.spreadsheetConfigs())
                .anyMatch(config -> config.name().equals("NewSheet") && config.sheetType() == SheetType.BATTERY);
    }

    @Test
    void testRemoveSpreadsheetConfigFromCollection() throws Exception {
        SpreadsheetConfigCollectionInfos initialCollection = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(initialCollection);

        SpreadsheetConfigCollectionInfos createdCollection = getSpreadsheetConfigCollection(collectionUuid);
        UUID configIdToRemove = createdCollection.spreadsheetConfigs().get(0).id();

        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid + "/spreadsheet-configs/" + configIdToRemove))
                .andExpect(status().isNoContent());

        SpreadsheetConfigCollectionInfos updatedCollection = getSpreadsheetConfigCollection(collectionUuid);
        assertThat(updatedCollection.spreadsheetConfigs()).hasSize(initialCollection.spreadsheetConfigs().size() - 1);
        assertThat(updatedCollection.spreadsheetConfigs())
                .noneMatch(config -> config.id().equals(configIdToRemove));
    }

    @Test
    void testAddSpreadsheetConfigToNonExistentCollection() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        SpreadsheetConfigInfos newConfig = new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, List.of());

        String newConfigJson = mapper.writeValueAsString(newConfig);
        mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + nonExistentUuid + "/spreadsheet-configs")
                .content(newConfigJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveNonExistentSpreadsheetConfig() throws Exception {
        SpreadsheetConfigCollectionInfos collection = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(collection);

        UUID nonExistentConfigId = UUID.randomUUID();
        mockMvc.perform(delete(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid + "/spreadsheet-configs/" + nonExistentConfigId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testReorderSpreadsheetConfigs() throws Exception {
        // Create a collection with multiple configs
        SpreadsheetConfigCollectionInfos collection = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionId = postSpreadsheetConfigCollection(collection);

        // Get the created collection to get the config IDs
        SpreadsheetConfigCollectionInfos createdCollection = getSpreadsheetConfigCollection(collectionId);
        List<UUID> configIds = createdCollection.spreadsheetConfigs().stream()
                .map(SpreadsheetConfigInfos::id)
                .toList();

        // Create a new order (reverse the existing order)
        List<UUID> newOrder = new ArrayList<>(configIds);
        Collections.reverse(newOrder);

        // Send the reorder request
        String newOrderJson = mapper.writeValueAsString(newOrder);
        mockMvc.perform(put("/v1/spreadsheet-config-collections/" + collectionId + "/reorder")
                        .content(newOrderJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Get the collection again and verify the order has changed
        SpreadsheetConfigCollectionInfos updatedCollection = getSpreadsheetConfigCollection(collectionId);
        List<UUID> updatedConfigIds = updatedCollection.spreadsheetConfigs().stream()
                .map(SpreadsheetConfigInfos::id)
                .toList();

        assertThat(updatedConfigIds).isEqualTo(newOrder);
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

    private UUID postMergeSpreadsheetConfigsIntoCollection(List<UUID> configIds) throws Exception {
        String configIdsJson = mapper.writeValueAsString(configIds);

        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/merge")
                        .content(configIdsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private UUID duplicateSpreadsheetConfigCollection(UUID collectionUuid) throws Exception {
        MvcResult mvcPostResult = mockMvc.perform(post(URI_SPREADSHEET_CONFIG_COLLECTION_BASE)
                        .queryParam("duplicateFrom", collectionUuid.toString()))
                .andExpect(status().isCreated())
                .andReturn();

        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private UUID saveAndReturnId(SpreadsheetConfigCollectionInfos collection) {
        return spreadsheetConfigCollectionService.createSpreadsheetConfigCollection(collection);
    }
}
