/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.StudyConfigApi;
import org.gridsuite.studyconfig.server.dto.ColumnFilterInfos;
import org.gridsuite.studyconfig.server.dto.ComputationResultFiltersInfos;
import org.gridsuite.studyconfig.server.dto.GlobalFilterInfos;
import org.gridsuite.studyconfig.server.service.ComputationResultFiltersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/computation-result-filters")
@RequiredArgsConstructor
public class ComputationResultFiltersController {

    private final ComputationResultFiltersService computationGlobalFiltersService;

    @PostMapping(value = "/default")
    @Operation(summary = "Create a default computation result filters",
            description = "Creates a default computation result filters")
    @ApiResponse(responseCode = "201", description = "Default computation result filters created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createDefaultComputationResultFilters() {
        UUID id = computationGlobalFiltersService.createDefaultComputingResultFilters();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a computation result filters",
            description = "Retrieves a computation result filters by its ID")
    @ApiResponse(responseCode = "200", description = "Computation result filters found",
            content = @Content(schema = @Schema(implementation = ComputationResultFiltersInfos.class)))
    @ApiResponse(responseCode = "404", description = "Computation result filters not found")
    public ResponseEntity<ComputationResultFiltersInfos> getComputingResultFilters(
            @Parameter(description = "ID of the computation result filters to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(computationGlobalFiltersService.getComputingResultFilters(id));
    }

    @PostMapping("/{id}/global-filters")
    @Operation(summary = "Set global filters",
            description = "Replaces all existing global filters with the provided list for a computation result")
    @ApiResponse(responseCode = "204", description = "Global filters set successfully")
    @ApiResponse(responseCode = "404", description = "Computation result filters not found")
    public ResponseEntity<Void> setGlobalFiltersForComputingResult(
            @Parameter(description = "ID of the spreadsheet config") @PathVariable UUID id,
            @Valid @RequestBody List<GlobalFilterInfos> filters) {
        computationGlobalFiltersService.setGlobalFiltersForComputationResult(id, filters);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/columns/{columnId}")
    @Operation(summary = "Update a column", description = "Updates an existing column")
    @ApiResponse(responseCode = "204", description = "Column updated")
    public ResponseEntity<Void> updateColumn(
            @Parameter(description = "ID of the computation config") @PathVariable UUID id,
            @Parameter(description = "ID of the column to update") @PathVariable UUID columnId,
            @Valid @RequestBody ColumnFilterInfos dto) {
        computationGlobalFiltersService.updateColumn(id, columnId, dto);
        return ResponseEntity.noContent().build();
    }

}
