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
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.service.SpreadsheetConfigService;
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
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/spreadsheet-configs")
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

    @PostMapping(params = { DUPLICATE_FROM })
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

    @PutMapping("/{id}/sort")
    @Operation(summary = "Update a spreadsheet configuration sort",
            description = "Updates an existing spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Configuration updated")
    @ApiResponse(responseCode = "404", description = "Configuration not found")
    public ResponseEntity<Void> updateSpreadsheetConfigSort(
            @Parameter(description = "ID of the configuration to update") @PathVariable UUID id,
            @Valid @RequestBody SortConfig dto) {
        spreadsheetConfigService.updateSpreadsheetConfigSort(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/name")
    @Operation(summary = "Rename a spreadsheet configuration",
            description = "Updates the name of an existing spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Configuration renamed")
    @ApiResponse(responseCode = "404", description = "Configuration not found")
    public ResponseEntity<Void> renameSpreadsheetConfig(
            @Parameter(description = "ID of the configuration to rename") @PathVariable UUID id,
            @Parameter(description = "New name for the configuration") @RequestBody String name) {
        spreadsheetConfigService.renameSpreadsheetConfig(id, name);
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

    @GetMapping("/{id}/columns/{columnId}")
    @Operation(summary = "Get a column", description = "Retrieves a column by its ID")
    @ApiResponse(responseCode = "200", description = "Column found")
    @ApiResponse(responseCode = "404", description = "Column not found")
    public ResponseEntity<SpreadsheetColumnInfos> getColumn(
                    @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
                    @Parameter(description = "ID of the column to retrieve") @PathVariable UUID columnId) {
        return ResponseEntity.ok(spreadsheetConfigService.getColumn(id, columnId));
    }

    @PostMapping("/{id}/columns")
    @Operation(summary = "Create a column", description = "Creates a new column")
    @ApiResponse(responseCode = "201", description = "Column created")
    public ResponseEntity<UUID> createColumn(
                    @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
                    @Valid @RequestBody SpreadsheetColumnInfos dto) {
        UUID columnId = spreadsheetConfigService.createColumn(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(columnId);
    }

    @PutMapping("/{id}/columns/{columnId}")
    @Operation(summary = "Update a column", description = "Updates an existing column")
    @ApiResponse(responseCode = "204", description = "Column updated")
    public ResponseEntity<Void> updateColumn(
                    @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
                    @Parameter(description = "ID of the column to update") @PathVariable UUID columnId,
                    @Valid @RequestBody SpreadsheetColumnInfos dto) {
        spreadsheetConfigService.updateColumn(id, columnId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/columns/{columnId}")
    @Operation(summary = "Delete a column", description = "Deletes an existing column")
    @ApiResponse(responseCode = "204", description = "Column deleted")
    @ApiResponse(responseCode = "404", description = "Column not found")
    public ResponseEntity<Void> deleteColumn(
                    @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
                    @Parameter(description = "ID of the column to delete") @PathVariable UUID columnId) {
        spreadsheetConfigService.deleteColumn(id, columnId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/columns/{columnId}/duplicate")
    @Operation(summary = "Duplicate a column", description = "Duplicate a column and place it after the source column")
    @ApiResponse(responseCode = "204", description = "Column duplicated")
    @ApiResponse(responseCode = "404", description = "Column not found")
    public ResponseEntity<Void> duplicateColumn(
            @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
            @Parameter(description = "ID of the column to duplicate") @PathVariable UUID columnId) {
        spreadsheetConfigService.duplicateColumn(id, columnId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/columns/reorder")
    @Operation(summary = "Reorder columns", description = "Reorders the columns of a spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Columns reordered")
    public ResponseEntity<Void> reorderColumns(
                    @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
                    @Parameter(description = "New order of column IDs") @RequestBody List<UUID> columnOrder) {
        spreadsheetConfigService.reorderColumns(id, columnOrder);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/columns/states")
    @Operation(summary = "Update column states",
            description = "Updates the visibility and order of columns in a spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Column states updated successfully")
    @ApiResponse(responseCode = "404", description = "Spreadsheet configuration not found")
    @ApiResponse(responseCode = "400", description = "Invalid column state data")
    public ResponseEntity<Void> updateColumnStates(
            @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
            @Parameter(description = "List of column state updates")
            @Valid @RequestBody List<ColumnStateUpdateInfos> columnStates) {
        spreadsheetConfigService.updateColumnStates(id, columnStates);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/global-filters")
    @Operation(summary = "Set global filters",
            description = "Replaces all existing global filters with the provided list for a spreadsheet configuration")
    @ApiResponse(responseCode = "204", description = "Global filters set successfully")
    @ApiResponse(responseCode = "404", description = "Spreadsheet configuration not found")
    public ResponseEntity<Void> setGlobalFiltersForSpreadsheetConfig(
            @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
            @Valid @RequestBody List<GlobalFilterInfos> filters) {
        spreadsheetConfigService.setGlobalFiltersForSpreadsheetConfig(id, filters);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reset-filters")
    @Operation(summary = "Reset global and column filters",
            description = "Reset all columns filters in a spreadsheet configuration as well as the global filter")
    @ApiResponse(responseCode = "204", description = "Filters reset successfully")
    @ApiResponse(responseCode = "404", description = "Spreadsheet configuration not found")
    public ResponseEntity<Void> resetFilters(
            @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id) {
        spreadsheetConfigService.resetSpreadsheetConfigFilters(id);
        return ResponseEntity.noContent().build();
    }

}
