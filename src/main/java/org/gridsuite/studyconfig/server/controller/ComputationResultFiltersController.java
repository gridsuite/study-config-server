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
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.dto.ComputationResultColumnFilterInfos;
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

    @GetMapping("/{computationResultFiltersId}/{computationType}")
    @Operation(summary = "Get a computation result global filters",
            description = "Fetches the computation result global filters for a given computation type")
    @ApiResponse(responseCode = "200", description = "Computation result global filters found",
            content = @Content(schema = @Schema(implementation = GlobalFilterInfos.class)))
    @ApiResponse(responseCode = "404", description = "Computation result global filters not found")
    public ResponseEntity<List<GlobalFilterInfos>> getComputingResultGlobalFilters(
            @Parameter(description = "Computation result filters Id") @PathVariable UUID computationResultFiltersId,
            @Parameter(description = "Computation type") @PathVariable String computationType) {
        return ResponseEntity.ok(computationGlobalFiltersService.getComputingResultGlobalFilters(computationResultFiltersId, computationType));
    }

    @GetMapping("/{computationResultFiltersId}/{computationType}/{computationSubType}")
    @Operation(summary = "Get a computation result column filters",
            description = "Fetches the computation result column filters for a given computation type and subtype")
    @ApiResponse(responseCode = "200", description = "Computation result column filters found",
            content = @Content(schema = @Schema(implementation = ComputationResultColumnFilterInfos.class)))
    @ApiResponse(responseCode = "404", description = "Computation result column filters not found")
    public ResponseEntity<List<ComputationResultColumnFilterInfos>> getComputingResultColumnFilters(
            @Parameter(description = "Computation result filters Id") @PathVariable UUID computationResultFiltersId,
            @Parameter(description = "Computation type") @PathVariable String computationType,
            @Parameter(description = "Computation subtype") @PathVariable String computationSubType) {
        return ResponseEntity.ok(computationGlobalFiltersService.getComputingResultColumnFilters(computationResultFiltersId, computationType, computationSubType));
    }

    @PostMapping("/{computationResultFiltersId}/{computationType}/global-filters")
    @Operation(summary = "Set global filters",
            description = "Replaces all existing global filters with the provided list for a computation result")
    @ApiResponse(responseCode = "204", description = "Global filters set successfully")
    @ApiResponse(responseCode = "404", description = "Computation result filters not found")
    public ResponseEntity<Void> setGlobalFiltersForComputingResult(
            @PathVariable UUID computationResultFiltersId,
            @PathVariable String computationType,
            @Valid @RequestBody List<GlobalFilterInfos> filters) {
        computationGlobalFiltersService.setGlobalFiltersForComputationResult(computationResultFiltersId, computationType, filters);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{computationResultFiltersId}/{computationType}/{computationSubType}/columns")
    @Operation(summary = "Update a column", description = "Updates an existing column")
    @ApiResponse(responseCode = "204", description = "Column updated")
    public ResponseEntity<Void> updateColumn(
            @PathVariable UUID computationResultFiltersId,
            @PathVariable String computationType,
            @PathVariable String computationSubType,
            @Valid @RequestBody ComputationResultColumnFilterInfos dto) {
        computationGlobalFiltersService.updateColumn(computationResultFiltersId, computationType, computationSubType, dto);
        return ResponseEntity.noContent().build();
    }
}
