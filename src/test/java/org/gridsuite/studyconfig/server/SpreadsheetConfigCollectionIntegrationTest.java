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
import org.gridsuite.studyconfig.server.dto.*;
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
import java.util.stream.Collectors;

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
    void testCreateCollectionWithFilteredConfigs() throws Exception {
        // Create a collection with configs that have filters
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(
                null,
                createSpreadsheetConfigsWithFilters(),
                null
        );

        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);
        SpreadsheetConfigCollectionInfos createdCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(createdCollection)
                .usingRecursiveComparison()
                .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id", "spreadsheetConfigs.globalFilters.uuid")
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
    void testUpdateCollectionWithFilteredConfigs() throws Exception {
        // Create a collection with configs that have filters
        SpreadsheetConfigCollectionInfos collectionToUpdate = new SpreadsheetConfigCollectionInfos(
                null,
                createSpreadsheetConfigsWithFilters(),
                null
        );

        UUID collectionUuid = saveAndReturnId(collectionToUpdate);

        // Update the collection with new configs that also have filters
        SpreadsheetConfigCollectionInfos updatedCollection = new SpreadsheetConfigCollectionInfos(
                collectionUuid,
                createUpdatedSpreadsheetConfigsWithFilters(),
                null
        );

        String updatedCollectionJson = mapper.writeValueAsString(updatedCollection);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid)
                        .content(updatedCollectionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigCollectionInfos retrievedCollection = getSpreadsheetConfigCollection(collectionUuid);

        assertThat(retrievedCollection)
                .usingRecursiveComparison()
                .ignoringFields("spreadsheetConfigs.columns.uuid", "spreadsheetConfigs.id", "spreadsheetConfigs.globalFilters.uuid")
                .ignoringExpectedNullFields()
                .isEqualTo(updatedCollection);
    }

    @Test
    void testAppendCollection() throws Exception {
        List<String> existingAliases = List.of("n1", "n2", "n3");
        SpreadsheetConfigCollectionInfos collectionToUpdate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), existingAliases);
        UUID collectionUuid = saveAndReturnId(collectionToUpdate);

        List<String> appendedAliases = List.of("n1", "n6", "n3");
        SpreadsheetConfigCollectionInfos appendedCollection = new SpreadsheetConfigCollectionInfos(null, createUpdatedSpreadsheetConfigs(), appendedAliases);
        UUID appendedCollectionUuid = saveAndReturnId(appendedCollection);

        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid + "/append?sourceCollection=" + appendedCollectionUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        SpreadsheetConfigCollectionInfos mergedCollection = getSpreadsheetConfigCollection(collectionUuid);

        // We have 3 new tabs coming from the appended collection, but 2 have been renamed to ensure name uniqueness.
        assertThat(mergedCollection.spreadsheetConfigs().stream().map(SpreadsheetConfigInfos::name).toList())
                .isEqualTo(List.of("TestSheet", "TestSheet1", "Generator", "TestSheet (2)", "TestSheet (1)"));
        // In the appended collection, we keep only the aliases from the appended collection
        assertThat(mergedCollection.nodeAliases()).isEqualTo(appendedAliases);
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
        SpreadsheetConfigCollectionInfos collectionToCreate = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigsWithFilters(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(collectionToCreate);

        UUID duplicatedCollectionUuid = duplicateSpreadsheetConfigCollection(collectionUuid);

        SpreadsheetConfigCollectionInfos duplicatedCollection = getSpreadsheetConfigCollection(duplicatedCollectionUuid);
        assertThat(duplicatedCollection)
            .usingRecursiveComparison()
            .ignoringFields("spreadsheetConfigs.columns.uuid", "id", "spreadsheetConfigs.id", "spreadsheetConfigs.globalFilters.uuid")
            .ignoringExpectedNullFields()
            .isEqualTo(collectionToCreate);
        assertThat(duplicatedCollection.id()).isNotEqualTo(collectionUuid);
    }

    @Test
    void testMergeModelsIntoNewCollection() throws Exception {
        // create a source collection to create N configs
        SpreadsheetConfigCollectionInfos sourceCollection = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigsWithAliases(), List.of("sourceAlias"));
        UUID collectionUuid = postSpreadsheetConfigCollection(sourceCollection);
        List<SpreadsheetConfigInfos> sourceConfigs = getSpreadsheetConfigCollection(collectionUuid).spreadsheetConfigs();
        List<UUID> configIds = sourceConfigs.stream().map(SpreadsheetConfigInfos::id).toList();

        // create a second collection, duplicating and merging these N source Configs
        UUID mergedCollectionUuid = postMergeSpreadsheetConfigsIntoCollection(configIds);
        assertThat(mergedCollectionUuid).isNotEqualTo(collectionUuid);

        SpreadsheetConfigCollectionInfos mergedCollection = getSpreadsheetConfigCollection(mergedCollectionUuid);
        List<UUID> duplicatedConfigIds = mergedCollection.spreadsheetConfigs().stream().map(SpreadsheetConfigInfos::id).toList();
        assertThat(duplicatedConfigIds).hasSameSizeAs(configIds);
        assertThat(duplicatedConfigIds.stream().sorted().toList()).isNotEqualTo(configIds.stream().sorted().toList());

        // dont compare aliases, merged collection aliases are computed
        assertThat(mergedCollection)
                .usingRecursiveComparison()
                .ignoringFields("id", "nodeAliases", "spreadsheetConfigs.columns.uuid", "spreadsheetConfigs.id")
                .ignoringExpectedNullFields()
                .isEqualTo(sourceCollection);

        // merged aliases must be unique
        List<String> expectedUniqueAliases = sourceConfigs.stream().map(SpreadsheetConfigInfos::nodeAliases).flatMap(Collection::stream).collect(Collectors.toSet()).stream().toList();
        assertThat(mergedCollection.nodeAliases()).isEqualTo(expectedUniqueAliases);
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

        List<ColumnInfos> columnInfos = List.of(
                new ColumnInfos(null, "new_col", ColumnType.NUMBER, 1, "formula", "[\"dep\"]", true, new ColumnFilterInfos(null, "idNew", null, null, null, null))
        );
        SpreadsheetConfigInfos newConfig = new SpreadsheetConfigInfos(null, "NewSheet", SheetType.BATTERY, columnInfos, null, List.of());

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
        UUID configIdToRemove = createdCollection.spreadsheetConfigs().getFirst().id();

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
        SpreadsheetConfigInfos newConfig = new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, List.of(), null, List.of());

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

    @Test
    void testReplaceAllSpreadsheetConfigs() throws Exception {
        // Create a first collection with initial configs
        SpreadsheetConfigCollectionInfos initialCollection = new SpreadsheetConfigCollectionInfos(null, createSpreadsheetConfigs(), null);
        UUID collectionUuid = postSpreadsheetConfigCollection(initialCollection);

        // Get the initial config IDs
        List<UUID> initialConfigIds = getSpreadsheetConfigCollection(collectionUuid)
                .spreadsheetConfigs()
                .stream()
                .map(SpreadsheetConfigInfos::id)
                .toList();

        // Create a second collection with different configs
        SpreadsheetConfigCollectionInfos sourceCollection = new SpreadsheetConfigCollectionInfos(null, createUpdatedSpreadsheetConfigs(), null);
        UUID sourceCollectionUuid = postSpreadsheetConfigCollection(sourceCollection);

        // Get the config IDs from the source collection
        List<UUID> sourceConfigIds = getSpreadsheetConfigCollection(sourceCollectionUuid)
                .spreadsheetConfigs()
                .stream()
                .map(SpreadsheetConfigInfos::id)
                .toList();

        // Call the replace-all endpoint
        String configIdsJson = mapper.writeValueAsString(sourceConfigIds);
        mockMvc.perform(put(URI_SPREADSHEET_CONFIG_COLLECTION_BASE + "/" + collectionUuid + "/spreadsheet-configs/replace-all")
                .content(configIdsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the collection now has the new configs
        SpreadsheetConfigCollectionInfos updatedCollection = getSpreadsheetConfigCollection(collectionUuid);
        List<UUID> updatedConfigIds = updatedCollection.spreadsheetConfigs()
                .stream()
                .map(SpreadsheetConfigInfos::id)
                .toList();

        // Check that the initial and updated collections have different configs
        assertThat(updatedConfigIds)
                .isNotEqualTo(initialConfigIds)
                .hasSize(sourceConfigIds.size());
    }

    private List<SpreadsheetConfigInfos> createSpreadsheetConfigsWithAliases() {
        List<ColumnInfos> columnInfos = Arrays.asList(
                new ColumnInfos(null, "cust_a", ColumnType.NUMBER, 1, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", true, new ColumnFilterInfos(null, "idA", null, null, null, null)),
                new ColumnInfos(null, "cust_b", ColumnType.TEXT, null, "var_minP + 1", null, true, new ColumnFilterInfos(null, "idB", null, null, null, null))
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, columnInfos, null, List.of("a1", "a2")),
                new SpreadsheetConfigInfos(null, "TestSheet1", SheetType.GENERATOR, columnInfos, null, List.of("a1", "a2", "a3")),
                new SpreadsheetConfigInfos(null, "TestSheet2", SheetType.GENERATOR, columnInfos, null, List.of("a2", "a4")),
                new SpreadsheetConfigInfos(null, "TestSheet3", SheetType.GENERATOR, columnInfos, null, List.of()),
                new SpreadsheetConfigInfos(null, "TestSheet4", SheetType.GENERATOR, columnInfos, null, List.of("alias"))
        );
    }

    private List<SpreadsheetConfigInfos> createSpreadsheetConfigs() {
        List<ColumnInfos> columnInfos = Arrays.asList(
            new ColumnInfos(null, "cust_a", ColumnType.NUMBER, 1, "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", true,
                    new ColumnFilterInfos(null, "idA", null, null, null, null)),
            new ColumnInfos(null, "cust_b", ColumnType.TEXT, null, "var_minP + 1", null, true,
                    new ColumnFilterInfos(null, "idB", null, null, null, null))
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, columnInfos, null, List.of()),
                new SpreadsheetConfigInfos(null, "TestSheet1", SheetType.GENERATOR, columnInfos, null, List.of())
        );
    }

    private List<SpreadsheetConfigInfos> createSpreadsheetConfigsWithFilters() {
        List<ColumnInfos> columnsConfig1 = Arrays.asList(
                new ColumnInfos(null, "id", ColumnType.TEXT, null, "id", "[\"id\"]", true, new ColumnFilterInfos(null, "id",
                        "text", "equals", "test-value", null)),
                new ColumnInfos(null, "name", ColumnType.TEXT, null, "name", "[\"name\"]", true,
                        new ColumnFilterInfos(null, "name", "text", "contains", "name-value", null)),
                new ColumnInfos(null, "country1", ColumnType.ENUM, null, "country1", "[\"country1\"]", true,
                        new ColumnFilterInfos(null, "country1", null, null, null, null)),
                new ColumnInfos(null, "voltage", ColumnType.NUMBER, 1, "voltage", "[\"voltage\"]", true,
                        new ColumnFilterInfos(null, "voltage", "number", "greaterThan", "100", 0.5))
        );

        List<GlobalFilterInfos> globalFiltersConfig1 = Arrays.asList(
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Global Filter 1").recent(false).build(),
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Global Filter 2").recent(false).build()
        );

        List<ColumnInfos> columnsConfig2 = Arrays.asList(
                new ColumnInfos(
                        null, "id", ColumnType.TEXT, null, "id", "[\"id\"]", true,
                        new ColumnFilterInfos(null, "id", "text", "contains", "other-value", null)
                ),
                new ColumnInfos(
                        null, "type", ColumnType.ENUM, null, "type", "[\"type\"]", true,
                        new ColumnFilterInfos(null, "type", null, null, null, null)
                ),
                new ColumnInfos(
                        null, "power", ColumnType.NUMBER, 1, "power", "[\"power\"]", true,
                        new ColumnFilterInfos(null, "power", "number", "lessThan", "50", 0.1)
                )
        );

        List<GlobalFilterInfos> globalFiltersConfig2 = List.of(
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Another Global Filter").recent(false).build()
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, columnsConfig1, globalFiltersConfig1, List.of()),
                new SpreadsheetConfigInfos(null, "TestSheet2", SheetType.LOAD, columnsConfig2, globalFiltersConfig2, List.of())
        );
    }

    private List<SpreadsheetConfigInfos> createUpdatedSpreadsheetConfigs() {
        List<ColumnInfos> columnInfos = Arrays.asList(
                new ColumnInfos(
                        null, "cust_a", ColumnType.NUMBER, 1,
                        "cust_b + cust_c", "[\"cust_b\", \"cust_c\"]", true,
                        new ColumnFilterInfos(null, "cust_a", null, null, null, null)
                ),
                new ColumnInfos(
                        null, "cust_b", ColumnType.TEXT, null,
                        "var_minP + 2", null, true,
                        new ColumnFilterInfos(null, "cust_b", null, null, null, null)
                ),
                new ColumnInfos(
                        null, "cust_c", ColumnType.ENUM, null,
                        "cust_b + 2", "[\"cust_b\"]", true,
                        new ColumnFilterInfos(null, "cust_c", null, null, null, null)
                ),
                new ColumnInfos(
                        null, "cust_d", ColumnType.NUMBER, 0,
                        "5 + 1", null, true,
                        new ColumnFilterInfos(null, "cust_d", null, null, null, null)
                )
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "Generator", SheetType.GENERATOR, columnInfos, null, List.of()),
                new SpreadsheetConfigInfos(null, "TestSheet", SheetType.GENERATOR, columnInfos, null, List.of()),
                new SpreadsheetConfigInfos(null, "TestSheet (1)", SheetType.BATTERY, columnInfos, null, List.of())
        );
    }

    private List<SpreadsheetConfigInfos> createUpdatedSpreadsheetConfigsWithFilters() {
        List<ColumnInfos> columnsConfig1 = Arrays.asList(
                new ColumnInfos(
                        null, "id", ColumnType.TEXT, null, "id", "[\"id\"]", true,
                        new ColumnFilterInfos(null, "id", "text", "startsWith", "new-prefix", null)
                ),
                new ColumnInfos(
                        null, "updated", ColumnType.TEXT, null, "updated", "[\"updated\"]", true,
                        new ColumnFilterInfos(null, "updated", null, null, null, null)
                )
        );

        List<GlobalFilterInfos> globalFiltersConfig1 = Arrays.asList(
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Updated Filter 1").recent(false).build(),
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Updated Filter 2").recent(false).build(),
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Updated Filter 3").recent(false).build()
        );

        List<ColumnInfos> columnsConfig2 = Arrays.asList(
                new ColumnInfos(
                        null, "id", ColumnType.TEXT, null,
                        "id", "[\"id\"]", true,
                        new ColumnFilterInfos(null, "id", "text", "endsWith", "suffix", null)
                ),
                new ColumnInfos(
                        null, "other", ColumnType.NUMBER, 2,
                        "other", "[\"other\"]", true,
                        new ColumnFilterInfos(null, "other", "number", "between", "10,20", null)
                )
        );

        List<GlobalFilterInfos> globalFiltersConfig2 = List.of(
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Updated Other Filter").recent(false).build()
        );

        List<ColumnInfos> columnsConfig3 = Arrays.asList(
                new ColumnInfos(
                        null, "id", ColumnType.TEXT, null,
                        "id", "[\"id\"]", true,
                        new ColumnFilterInfos(null, "id", "text", "contains", "middle", null)
                ),
                new ColumnInfos(
                        null, "third", ColumnType.BOOLEAN, null,
                        "third", "[\"third\"]", true,
                        new ColumnFilterInfos(null, "third", "boolean", "equals", "true", null)
                )
        );

        List<GlobalFilterInfos> globalFiltersConfig3 = List.of(
                GlobalFilterInfos.builder().uuid(UUID.randomUUID()).filterType("country").label("Third Config Filter").recent(false).build()
        );

        return List.of(
                new SpreadsheetConfigInfos(null, "Updated1", SheetType.BATTERY, columnsConfig1, globalFiltersConfig1, List.of()),
                new SpreadsheetConfigInfos(null, "Updated2", SheetType.LINE, columnsConfig2, globalFiltersConfig2, List.of()),
                new SpreadsheetConfigInfos(null, "Added3", SheetType.BUS, columnsConfig3, globalFiltersConfig3, List.of())
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
