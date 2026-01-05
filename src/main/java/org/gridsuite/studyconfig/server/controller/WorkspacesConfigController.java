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
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceMetadata;
import org.gridsuite.studyconfig.server.dto.workspace.PanelInfos;
import org.gridsuite.studyconfig.server.service.WorkspacesConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/workspaces-configs")
@RequiredArgsConstructor
@Tag(name = "Workspaces configs", description = "Workspaces configs API")
public class WorkspacesConfigController {

    private final WorkspacesConfigService workspacesConfigService;

    public static final String DUPLICATE_FROM = "duplicateFrom";

    @PostMapping(params = { DUPLICATE_FROM })
    @Operation(summary = "Duplicate a workspaces config",
            description = "Creates a copy of an existing workspaces config")
    @ApiResponse(responseCode = "201", description = "Workspaces config duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    @ApiResponse(responseCode = "404", description = "Workspaces config not found")
    public ResponseEntity<UUID> duplicateWorkspacesConfig(
            @Parameter(description = "UUID of the workspaces config to duplicate") @RequestParam(name = DUPLICATE_FROM) UUID id,
            @Parameter(description = "Mapping of old NAD config UUIDs to new ones") @RequestBody(required = false) Map<UUID, UUID> nadConfigMapping) {
        UUID newId = workspacesConfigService.duplicateWorkspacesConfig(id, nadConfigMapping != null ? nadConfigMapping : Map.of());
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

    @PostMapping(value = "/default")
    @Operation(summary = "Create a default workspaces config",
            description = "Creates a default workspaces config with 3 workspaces")
    @ApiResponse(responseCode = "201", description = "Default workspaces config created",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> createDefaultWorkspacesConfig() {
        UUID id = workspacesConfigService.createDefaultWorkspacesConfig();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workspaces config",
            description = "Deletes an existing workspaces config")
    @ApiResponse(responseCode = "204", description = "Workspaces config deleted")
    @ApiResponse(responseCode = "404", description = "Workspaces config not found")
    public ResponseEntity<Void> deleteWorkspacesConfig(
            @Parameter(description = "ID of the workspaces config to delete") @PathVariable UUID id) {
        workspacesConfigService.deleteWorkspacesConfig(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/workspaces")
    @Operation(summary = "Get workspaces metadata",
            description = "Retrieves lightweight metadata for all workspaces (id, name, panel count)")
    @ApiResponse(responseCode = "200", description = "Workspaces metadata retrieved")
    @ApiResponse(responseCode = "404", description = "Workspaces config not found")
    public ResponseEntity<List<WorkspaceMetadata>> getWorkspaces(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id) {
        return ResponseEntity.ok(workspacesConfigService.getWorkspacesMetadata(id));
    }

    @PutMapping("/{id}/workspaces/{workspaceId}/name")
    @Operation(summary = "Rename a workspace", description = "Updates the name of a workspace")
    @ApiResponse(responseCode = "204", description = "Workspace renamed")
    @ApiResponse(responseCode = "404", description = "Workspace not found")
    public ResponseEntity<Void> renameWorkspace(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @RequestBody String name) {
        workspacesConfigService.renameWorkspace(id, workspaceId, name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/workspaces/{workspaceId}")
    @Operation(summary = "Get a workspace", description = "Retrieves a workspace by its ID")
    @ApiResponse(responseCode = "200", description = "Workspace found")
    @ApiResponse(responseCode = "404", description = "Workspace not found")
    public ResponseEntity<WorkspaceInfos> getWorkspace(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId) {
        return ResponseEntity.ok(workspacesConfigService.getWorkspace(id, workspaceId));
    }

    @GetMapping("/{id}/workspaces/{workspaceId}/panels")
    @Operation(summary = "Get panels", description = "Retrieves all panels or specific panels by IDs")
    @ApiResponse(responseCode = "200", description = "Panels found")
    public ResponseEntity<List<PanelInfos>> getPanels(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "Optional list of panel IDs to retrieve") @RequestParam(required = false) List<UUID> ids) {
        return ResponseEntity.ok(workspacesConfigService.getPanels(id, workspaceId, ids));
    }

    @PostMapping("/{id}/workspaces/{workspaceId}/panels")
    @Operation(summary = "Create or update panels",
            description = "Creates new panels or updates existing ones based on panel ID presence")
    @ApiResponse(responseCode = "204", description = "Panels created or updated")
    public ResponseEntity<Void> createOrUpdatePanels(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "List of panels") @Valid @RequestBody List<PanelInfos> panels) {
        workspacesConfigService.createOrUpdatePanels(id, workspaceId, panels);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/workspaces/{workspaceId}/panels")
    @Operation(summary = "Delete panels", description = "Deletes panels by their IDs")
    @ApiResponse(responseCode = "204", description = "Panels deleted")
    @ApiResponse(responseCode = "404", description = "Panels not found")
    public ResponseEntity<Void> deletePanels(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id,
            @Parameter(description = "ID of the workspace") @PathVariable UUID workspaceId,
            @Parameter(description = "List of panel IDs to delete") @Valid @RequestBody List<UUID> panelIds) {
        workspacesConfigService.deletePanels(id, workspaceId, panelIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/saved-nad-config-uuids")
    @Operation(summary = "Get all saved NAD config UUIDs",
            description = "Returns all saved NAD config UUIDs from all workspace panels")
    @ApiResponse(responseCode = "200", description = "Saved NAD config UUIDs retrieved")
    @ApiResponse(responseCode = "404", description = "Workspaces config not found")
    public ResponseEntity<List<UUID>> getAllSavedNadConfigUuids(
            @Parameter(description = "ID of the workspaces config") @PathVariable UUID id) {
        List<UUID> uuids = workspacesConfigService.getAllSavedNadConfigUuids(id);
        return ResponseEntity.ok(uuids);
    }

}
