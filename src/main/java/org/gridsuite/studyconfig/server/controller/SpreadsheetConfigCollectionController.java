/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.StudyConfigApi;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigCollectionInfos;
import org.gridsuite.studyconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.studyconfig.server.service.SpreadsheetConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/spreadsheet-config-collections")
@RequiredArgsConstructor
@Tag(name = "Spreadsheet Config Collection", description = "Spreadsheet Configuration Collection API")
public class SpreadsheetConfigCollectionController {

    private final SpreadsheetConfigService spreadsheetConfigService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new spreadsheet configuration collection",
            description = "Creates a new spreadsheet configuration collection and returns its ID")
    @ApiResponse(responseCode = "201", description = "Configuration collection created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createSpreadsheetConfigCollection(@Parameter(description = "Configuration collection to save") @Valid @RequestBody SpreadsheetConfigCollectionInfos dto) {
        UUID id = spreadsheetConfigService.createSpreadsheetConfigCollection(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(value = "/merge", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new spreadsheet configuration collection duplicating and merging a list of existing configurations",
            description = "Creates a new spreadsheet configuration collection and returns its ID")
    @ApiResponse(responseCode = "201", description = "Configuration collection created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createSpreadsheetConfigCollectionFromConfigs(@Parameter(description = "Configurations to duplicate and merge") @Valid @RequestBody List<UUID> configUuids) {
        UUID id = spreadsheetConfigService.createSpreadsheetConfigCollectionFromConfigs(configUuids);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a spreadsheet configuration collection",
            description = "Retrieves a spreadsheet configuration collection by its ID")
    @ApiResponse(responseCode = "200", description = "Configuration collection found",
            content = @Content(schema = @Schema(implementation = SpreadsheetConfigCollectionInfos.class)))
    @ApiResponse(responseCode = "404", description = "Configuration collection not found")
    public ResponseEntity<SpreadsheetConfigCollectionInfos> getSpreadsheetConfigCollection(
            @Parameter(description = "ID of the configuration collection to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(spreadsheetConfigService.getSpreadsheetConfigCollection(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a spreadsheet configuration collection",
            description = "Deletes an existing spreadsheet configuration collection")
    @ApiResponse(responseCode = "204", description = "Configuration collection deleted")
    @ApiResponse(responseCode = "404", description = "Configuration collection not found")
    public ResponseEntity<Void> deleteSpreadsheetConfigCollection(
            @Parameter(description = "ID of the configuration collection to delete") @PathVariable UUID id) {
        spreadsheetConfigService.deleteSpreadsheetConfigCollection(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a spreadsheet configuration collection",
            description = "Updates an existing spreadsheet configuration collection")
    @ApiResponse(responseCode = "204", description = "Configuration collection updated")
    @ApiResponse(responseCode = "404", description = "Configuration collection not found")
    public ResponseEntity<Void> updateSpreadsheetConfigCollection(
            @Parameter(description = "ID of the configuration collection to update") @PathVariable UUID id,
            @Valid @RequestBody SpreadsheetConfigCollectionInfos dto) {
        spreadsheetConfigService.updateSpreadsheetConfigCollection(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/spreadsheet-configs/replace-all")
    @Operation(summary = "Replace all spreadsheet configs in a collection",
            description = "Updates a collection with a list of existing spreadsheet configs, replacing any previous configs")
    @ApiResponse(responseCode = "204", description = "Collection updated with configs")
    @ApiResponse(responseCode = "404", description = "Collection or one of the configs not found")
    public ResponseEntity<Void> updateSpreadsheetConfigCollectionWithConfigs(
            @Parameter(description = "ID of the configuration collection") @PathVariable UUID id,
            @Parameter(description = "List of spreadsheet config UUIDs to replace the collection's configs")
            @Valid @RequestBody List<UUID> configIds) {
        spreadsheetConfigService.updateSpreadsheetConfigCollectionWithConfigs(id, configIds);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/append")
    @Operation(summary = "Append a collection to another collection",
            description = "")
    @ApiResponse(responseCode = "204", description = "Collection updated")
    @ApiResponse(responseCode = "404", description = "One of the collections not found")
    public ResponseEntity<Void> appendSpreadsheetConfigCollection(
            @Parameter(description = "ID of the configuration collection to update") @PathVariable UUID id,
            @Parameter(description = "ID of the configuration collection to be appended") @RequestParam(name = "sourceCollection") UUID sourceCollectionId) {
        spreadsheetConfigService.appendSpreadsheetConfigCollection(id, sourceCollectionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(params = { "duplicateFrom" })
    @Operation(summary = "Duplicate a spreadsheet configuration collection",
            description = "Creates a copy of an existing spreadsheet configuration collection")
    @ApiResponse(responseCode = "201", description = "Configuration collection duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "404", description = "Configuration collection not found")
    public ResponseEntity<UUID> duplicateSpreadsheetConfigCollection(@Parameter(description = "UUID of the configuration collection to duplicate") @RequestParam(name = "duplicateFrom") UUID id) {
        UUID newId = spreadsheetConfigService.duplicateSpreadsheetConfigCollection(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

    @PostMapping(value = "/default")
    @Operation(summary = "Create a default spreadsheet configuration collection",
            description = "Creates a default spreadsheet configuration collection")
    @ApiResponse(responseCode = "201", description = "Default configuration collection created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createDefaultSpreadsheetConfigCollection() {
        UUID id = spreadsheetConfigService.createDefaultSpreadsheetConfigCollection();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // spreadsheet-configs endpoints
    @PostMapping("/{id}/spreadsheet-configs")
    @Operation(summary = "Add a spreadsheet configuration to a collection",
            description = "Adds a new spreadsheet configuration to a collection")
    @ApiResponse(responseCode = "204", description = "Configuration added")
    @ApiResponse(responseCode = "404", description = "Configuration collection not found")
    public ResponseEntity<UUID> addSpreadsheetConfigToCollection(
            @Parameter(description = "ID of the configuration collection") @PathVariable UUID id,
            @Parameter(description = "Configuration to add") @Valid @RequestBody SpreadsheetConfigInfos dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spreadsheetConfigService.addSpreadsheetConfigToCollection(id, dto));
    }

    @DeleteMapping("/{id}/spreadsheet-configs/{configId}")
    @Operation(summary = "Remove a spreadsheet configuration from a collection",
            description = "Removes an existing spreadsheet configuration from a collection")
    @ApiResponse(responseCode = "204", description = "Configuration removed")
    @ApiResponse(responseCode = "404", description = "Configuration collection or configuration not found")
    public ResponseEntity<Void> removeSpreadsheetConfigFromCollection(
            @Parameter(description = "ID of the configuration collection") @PathVariable UUID id,
            @Parameter(description = "ID of the configuration to remove") @PathVariable UUID configId) {
        spreadsheetConfigService.removeSpreadsheetConfigFromCollection(id, configId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reorder")
    @Operation(summary = "Reorder spreadsheet configs in a collection",
            description = "Updates the order of spreadsheet configs within a collection")
    @ApiResponse(responseCode = "204", description = "Order updated")
    @ApiResponse(responseCode = "404", description = "Collection not found")
    public ResponseEntity<Void> reorderSpreadsheetConfigs(
            @Parameter(description = "ID of the configuration collection") @PathVariable UUID id,
            @Valid @RequestBody List<UUID> newOrder) {
        spreadsheetConfigService.reorderSpreadsheetConfigs(id, newOrder);
        return ResponseEntity.noContent().build();
    }

}
