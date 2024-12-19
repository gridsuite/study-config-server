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
import org.gridsuite.spreadsheetconfig.server.dto.NetworkVisualizationParamInfos;
import org.gridsuite.spreadsheetconfig.server.service.NetworkVisualizationsParamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + SpreadsheetConfigApi.API_VERSION + "/network-visualizations-params")
@RequiredArgsConstructor
@Tag(name = "Network Visualizations Params", description = "Network Visualizations Parameters API")
public class NetworkVisualizationsParamController {

    private final NetworkVisualizationsParamService service;

    public static final String DUPLICATE_FROM = "duplicateFrom";

    @PostMapping(value = "/default")
    @Operation(summary = "Create new default parameters",
            description = "Creates default network visualizations parameters and returns new ID")
    @ApiResponse(responseCode = "201", description = "Default parameters created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createDefaultParameters() {
        UUID id = service.createDefaultParameters();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new parameters",
            description = "Creates new network visualizations parameters and returns new ID")
    @ApiResponse(responseCode = "201", description = "Parameters created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createParameters(@Parameter(description = "Parameters to save") @Valid @RequestBody NetworkVisualizationParamInfos dto) {
        UUID id = service.createParameters(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(value = "/duplicate", params = { DUPLICATE_FROM })
    @Operation(summary = "Duplicate parameters",
            description = "Creates a copy of existing network visualizations parameters")
    @ApiResponse(responseCode = "201", description = "Parameters duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "404", description = "Parameters not found")
    public ResponseEntity<UUID> duplicateParameters(@Parameter(description = "UUID of the parameters to duplicate") @RequestParam(name = DUPLICATE_FROM) UUID id) {
        UUID newId = service.duplicateParameters(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parameters",
            description = "Retrieves existing network visualizations parameters by its ID")
    @ApiResponse(responseCode = "200", description = "Parameters found",
            content = @Content(schema = @Schema(implementation = NetworkVisualizationParamInfos.class)))
    @ApiResponse(responseCode = "404", description = "Parameters not found")
    public ResponseEntity<NetworkVisualizationParamInfos> getParameters(
            @Parameter(description = "ID of parameters to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(service.getParameters(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parameters",
            description = "Updates existing network visualizations parameters")
    @ApiResponse(responseCode = "204", description = "Parameters updated")
    @ApiResponse(responseCode = "404", description = "Parameters not found")
    public ResponseEntity<Void> updateParameters(
            @Parameter(description = "ID of the parameters to update") @PathVariable UUID id,
            @Valid @RequestBody NetworkVisualizationParamInfos dto) {
        service.updateParameters(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete parameters",
            description = "Deletes existing network visualizations parameters")
    @ApiResponse(responseCode = "204", description = "Parameters deleted")
    @ApiResponse(responseCode = "404", description = "Parameters not found")
    public ResponseEntity<Void> deleteParameters(
            @Parameter(description = "ID of the parameters to delete") @PathVariable UUID id) {
        service.deleteParameters(id);
        return ResponseEntity.noContent().build();
    }
}
