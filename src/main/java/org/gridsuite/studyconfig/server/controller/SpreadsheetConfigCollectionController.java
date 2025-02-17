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

    @PostMapping(value = "/collect", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new spreadsheet configuration collection collecting and duplicating a list of existing configurations",
            description = "Creates a new spreadsheet configuration collection and returns its ID")
    @ApiResponse(responseCode = "201", description = "Configuration collection created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createSpreadsheetConfigCollectionFromConfigs(@Parameter(description = "Configurations to duplicate and collect") @Valid @RequestBody List<UUID> configUuids) {
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

    @PostMapping(value = "/duplicate", params = { "duplicateFrom" })
    @Operation(summary = "Duplicate a spreadsheet configuration collection",
            description = "Creates a copy of an existing spreadsheet configuration collection")
    @ApiResponse(responseCode = "201", description = "Configuration collection duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "404", description = "Configuration collection not found")
    public ResponseEntity<UUID> duplicateSpreadsheetConfigCollection(@Parameter(description = "UUID of the configuration collection to duplicate") @RequestParam(name = "duplicateFrom") UUID id) {
        UUID newId = spreadsheetConfigService.duplicateSpreadsheetConfigCollection(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }
}
