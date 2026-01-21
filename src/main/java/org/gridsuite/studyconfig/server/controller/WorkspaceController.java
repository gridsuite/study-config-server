/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
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
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.StudyConfigApi;
import org.gridsuite.studyconfig.server.dto.workspace.WorkspaceInfos;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspaceEntity;
import org.gridsuite.studyconfig.server.service.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/workspaces")
@RequiredArgsConstructor
@Tag(name = "Standalone Workspaces", description = "Standalone workspace API for GridExplore")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public static final String DUPLICATE_FROM = "duplicateFrom";

    @GetMapping("/{workspaceId}")
    @Operation(summary = "Get a standalone workspace",
            description = "Retrieves a standalone workspace by its ID")
    @ApiResponse(responseCode = "200", description = "Workspace found",
            content = @Content(schema = @Schema(implementation = WorkspaceInfos.class)))
    @ApiResponse(responseCode = "404", description = "Workspace not found")
    public ResponseEntity<WorkspaceInfos> getWorkspace(
            @Parameter(description = "ID of the workspace to retrieve") @PathVariable UUID workspaceId) {
        return ResponseEntity.ok(
            workspaceService.getWorkspace(workspaceId)
                .map(WorkspaceEntity::toDto)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId))
        );
    }

    @PostMapping(params = { DUPLICATE_FROM })
    @Operation(summary = "Duplicate a standalone workspace",
            description = "Creates a standalone workspace by duplicating an existing workspace")
    @ApiResponse(responseCode = "201", description = "Workspace duplicated",
            content = @Content(schema = @Schema(implementation = UUID.class)))
    public ResponseEntity<UUID> duplicateWorkspace(
            @Parameter(description = "UUID of the workspace to duplicate") @RequestParam(name = DUPLICATE_FROM) UUID sourceWorkspaceId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(workspaceService.duplicateWorkspace(sourceWorkspaceId).getId());
    }

    @PutMapping("/{workspaceId}/replace")
    @Operation(summary = "Replace a standalone workspace",
            description = "Replaces a standalone workspace with a copy of another workspace")
    @ApiResponse(responseCode = "204", description = "Workspace replaced")
    public ResponseEntity<Void> replaceWorkspace(
            @Parameter(description = "ID of the workspace to replace") @PathVariable UUID workspaceId,
            @Parameter(description = "UUID of the source workspace") @RequestParam(name = DUPLICATE_FROM) UUID sourceWorkspaceId) {
        workspaceService.replaceWorkspace(workspaceId, sourceWorkspaceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "Delete a standalone workspace")
    @ApiResponse(responseCode = "204", description = "Workspace deleted")
    public ResponseEntity<Void> deleteWorkspace(
            @Parameter(description = "ID of the workspace to delete") @PathVariable UUID workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }
}
