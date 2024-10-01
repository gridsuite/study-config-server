/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gridsuite.spreadsheetconfig.server.SpreadsheetConfigApi;
import org.gridsuite.spreadsheetconfig.server.dto.MetadataInfos;
import org.gridsuite.spreadsheetconfig.server.dto.SpreadsheetConfigInfos;
import org.gridsuite.spreadsheetconfig.server.service.SpreadsheetConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + SpreadsheetConfigApi.API_VERSION + "/spreadsheet-configs")
@RequiredArgsConstructor
@Tag(name = "Spreadsheet Config", description = "Spreadsheet Configuration API")
public class SpreadsheetConfigController {

    private final SpreadsheetConfigService spreadsheetConfigService;

    public static final String DUPLICATE_FROM = "duplicateFrom";

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new spreadsheet configuration",
            description = "Creates a new spreadsheet configuration and returns its ID")
    @ApiResponse(responseCode = "201", description = "Configuration created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createSpreadsheetConfig(@Parameter(description = "Configuration to save") @Valid @RequestBody SpreadsheetConfigInfos dto) {
        UUID id = spreadsheetConfigService.createSpreadsheetConfig(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(value = "/duplicate", params = { DUPLICATE_FROM })
    @Operation(summary = "Duplicate a spreadsheet configuration",
            description = "Creates a copy of an existing spreadsheet configuration")
    @ApiResponse(responseCode = "201", description = "Configuration duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "404", description = "Configuration not found")
    public ResponseEntity<UUID> duplicateSpreadsheetConfig(@Parameter(description = "UUID of the configuration to duplicate") @RequestParam(name = DUPLICATE_FROM) UUID id) {
        UUID newId = spreadsheetConfigService.duplicateSpreadsheetConfig(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a spreadsheet configuration",
            description = "Retrieves a spreadsheet configuration by its ID")
    @ApiResponse(responseCode = "200", description = "Configuration found",
            content = @Content(schema = @Schema(implementation = SpreadsheetConfigInfos.class)))
    @ApiResponse(responseCode = "404", description = "Configuration not found")
    public ResponseEntity<SpreadsheetConfigInfos> getSpreadsheetConfig(
            @Parameter(description = "ID of the configuration to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(spreadsheetConfigService.getSpreadsheetConfig(id));
    }

    @GetMapping("/metadata")
    @Operation(summary = "Get spreadsheet configurations metadata",
            description = "Retrieves metadata of spreadsheet configurations by their IDs")
    @ApiResponse(responseCode = "200", description = "Metadata found",
            content = @Content(schema = @Schema(implementation = MetadataInfos.class)))
    public ResponseEntity<List<MetadataInfos>> getSpreadsheetConfigsMetadata(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(spreadsheetConfigService.getSpreadsheetConfigsMetadata(ids));
    }

    @GetMapping
    @Operation(summary = "Get all spreadsheet configurations",
            description = "Retrieves all spreadsheet configurations")
    @ApiResponse(responseCode = "200", description = "List of configurations",
            content = @Content(schema = @Schema(implementation = SpreadsheetConfigInfos.class)))
    public ResponseEntity<List<SpreadsheetConfigInfos>> getAllSpreadsheetConfigs() {
        return ResponseEntity.ok(spreadsheetConfigService.getAllSpreadsheetConfigs());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a spreadsheet configuration",
            description = "Updates an existing spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Configuration updated")
    @ApiResponse(responseCode = "404", description = "Configuration not found")
    public ResponseEntity<Void> updateSpreadsheetConfig(
            @Parameter(description = "ID of the configuration to update") @PathVariable UUID id,
            @Valid @RequestBody SpreadsheetConfigInfos dto) {
        spreadsheetConfigService.updateSpreadsheetConfig(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a spreadsheet configuration",
            description = "Deletes an existing spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Configuration deleted")
    @ApiResponse(responseCode = "404", description = "Configuration not found")
    public ResponseEntity<Void> deleteSpreadsheetConfig(
            @Parameter(description = "ID of the configuration to delete") @PathVariable UUID id) {
        spreadsheetConfigService.deleteSpreadsheetConfig(id);
        return ResponseEntity.noContent().build();
    }
}
