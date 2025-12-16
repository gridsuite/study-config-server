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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.StudyConfigApi;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceCollectionInfos;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;
import org.gridsuite.studyconfig.server.dto.workspace.PanelInfos;
import org.gridsuite.studyconfig.server.service.WorkspaceCollectionService;
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
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/workspace-collections")
@RequiredArgsConstructor
@Tag(name = "Workspace Collection", description = "Workspace Collection API")
public class WorkspaceCollectionController {

    private final WorkspaceCollectionService workspaceCollectionService;

    public static final String DUPLICATE_FROM = "duplicateFrom";

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new workspace collection",
            description = "Creates a new workspace collection and returns its ID")
    @ApiResponse(responseCode = "201", description = "Workspace collection created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createWorkspaceCollection(
            @Parameter(description = "Workspace collection to save") @Valid @RequestBody WorkspaceCollectionInfos dto) {
        UUID id = workspaceCollectionService.createWorkspaceCollection(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(params = { DUPLICATE_FROM })
    @Operation(summary = "Duplicate a workspace collection",
            description = "Creates a copy of an existing workspace collection")
    @ApiResponse(responseCode = "201", description = "Workspace collection duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "404", description = "Workspace collection not found")
    public ResponseEntity<UUID> duplicateWorkspaceCollection(
            @Parameter(description = "UUID of the workspace collection to duplicate") @RequestParam(name = DUPLICATE_FROM) UUID id) {
        UUID newId = workspaceCollectionService.duplicateWorkspaceCollection(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

    @PostMapping(value = "/default")
    @Operation(summary = "Create a default workspace collection",
            description = "Creates a default workspace collection with 3 workspaces")
    @ApiResponse(responseCode = "201", description = "Default workspace collection created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createDefaultWorkspaceCollection() {
        UUID id = workspaceCollectionService.createDefaultWorkspaceCollection();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a workspace collection",
            description = "Retrieves a workspace collection by its ID")
    @ApiResponse(responseCode = "200", description = "Workspace collection found",
            content = @Content(schema = @Schema(implementation = WorkspaceCollectionInfos.class)))
    @ApiResponse(responseCode = "404", description = "Workspace collection not found")
    public ResponseEntity<WorkspaceCollectionInfos> getWorkspaceCollection(
            @Parameter(description = "ID of the workspace collection to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(workspaceCollectionService.getWorkspaceCollection(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a workspace collection",
            description = "Updates an existing workspace collection")
    @ApiResponse(responseCode = "204", description = "Workspace collection updated")
    @ApiResponse(responseCode = "404", description = "Workspace collection not found")
    public ResponseEntity<Void> updateWorkspaceCollection(
            @Parameter(description = "ID of the workspace collection to update") @PathVariable UUID id,
            @Valid @RequestBody WorkspaceCollectionInfos dto) {
        workspaceCollectionService.updateWorkspaceCollection(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workspace collection",
            description = "Deletes an existing workspace collection")
    @ApiResponse(responseCode = "204", description = "Workspace collection deleted")
    @ApiResponse(responseCode = "404", description = "Workspace collection not found")
    public ResponseEntity<Void> deleteWorkspaceCollection(
            @Parameter(description = "ID of the workspace collection to delete") @PathVariable UUID id) {
        workspaceCollectionService.deleteWorkspaceCollection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/workspaces/{workspaceId}")
    @Operation(summary = "Get a workspace", description = "Retrieves a workspace by its ID")
    @ApiResponse(responseCode = "200", description = "Workspace found")
    @ApiResponse(responseCode = "404", description = "Workspace not found")
    public ResponseEntity<WorkspaceInfos> getWorkspace(
            @Parameter(description = "ID of the workspace collection") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId) {
        return ResponseEntity.ok(workspaceCollectionService.getWorkspace(id, workspaceId));
    }

    @PutMapping("/{id}/workspaces/{workspaceId}")
    @Operation(summary = "Update a workspace", description = "Updates an existing workspace")
    @ApiResponse(responseCode = "204", description = "Workspace updated")
    @ApiResponse(responseCode = "404", description = "Workspace not found")
    public ResponseEntity<Void> updateWorkspace(
            @Parameter(description = "ID of the workspace collection") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Valid @RequestBody WorkspaceInfos dto) {
        workspaceCollectionService.updateWorkspace(id, workspaceId, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/workspaces/{workspaceId}/panels")
    @Operation(summary = "Get panels", description = "Retrieves all panels or specific panels by IDs")
    @ApiResponse(responseCode = "200", description = "Panels found")
    public ResponseEntity<List<PanelInfos>> getPanels(
            @Parameter(description = "ID of the workspace collection") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "Optional list of panel IDs to retrieve") @RequestParam(required = false) List<UUID> ids) {
        return ResponseEntity.ok(workspaceCollectionService.getPanels(id, workspaceId, ids));
    }

    @GetMapping("/{id}/workspaces/{workspaceId}/panels/{panelId}")
    @Operation(summary = "Get a panel", description = "Retrieves a panel by its ID")
    @ApiResponse(responseCode = "200", description = "Panel found")
    @ApiResponse(responseCode = "404", description = "Panel not found")
    public ResponseEntity<PanelInfos> getPanel(
            @Parameter(description = "ID of the workspace collection") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "ID of the panel") @PathVariable UUID panelId) {
        return ResponseEntity.ok(workspaceCollectionService.getPanel(id, workspaceId, panelId));
    }

    @PostMapping("/{id}/workspaces/{workspaceId}/panels")
    @Operation(summary = "Create or update panels",
            description = "Creates new panels or updates existing ones based on panel ID presence")
    @ApiResponse(responseCode = "204", description = "Panels created or updated")
    public ResponseEntity<Void> createOrUpdatePanels(
            @Parameter(description = "ID of the workspace collection") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "List of panels") @Valid @RequestBody List<PanelInfos> panels) {
        workspaceCollectionService.createOrUpdatePanels(id, workspaceId, panels);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/workspaces/{workspaceId}/panels")
    @Operation(summary = "Delete panels", description = "Deletes panels by their IDs")
    @ApiResponse(responseCode = "204", description = "Panels deleted")
    @ApiResponse(responseCode = "404", description = "Panels not found")
    public ResponseEntity<Void> deletePanels(
            @Parameter(description = "ID of the workspace collection") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "List of panel IDs to delete") @Valid @RequestBody List<UUID> panelIds) {
        workspaceCollectionService.deletePanels(id, workspaceId, panelIds);
        return ResponseEntity.noContent().build();
    }

}
