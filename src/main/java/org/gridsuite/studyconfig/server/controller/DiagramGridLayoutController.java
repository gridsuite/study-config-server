/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gridsuite.studyconfig.server.StudyConfigApi;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.service.DiagramGridLayoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/diagram-grid-layout")
@RequiredArgsConstructor
@Tag(name = "Diagram Grid Layout Config", description = "Diagram Grid Layout Configuration API")
public class DiagramGridLayoutController {
    private final DiagramGridLayoutService diagramGridLayoutService;

    public static final String DUPLICATE_FROM = "duplicateFrom";

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Save diagram grid layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The newly created diagram grid layout UUID returned")})
    public ResponseEntity<UUID> saveDiagramGridLayout(@RequestBody DiagramGridLayout diagramGridLayout) {
        return ResponseEntity.ok().body(diagramGridLayoutService.saveDiagramGridLayout(diagramGridLayout));
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update diagram grid layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The diagram grid layout has been updated")})
    public ResponseEntity<Void> updateDiagramGridLayout(@PathVariable("id") UUID diagramGridLayoutUuid,
                                                  @RequestBody DiagramGridLayout diagramGridLayout) {
        diagramGridLayoutService.updateDiagramGridLayout(diagramGridLayoutUuid, diagramGridLayout);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get diagram grid layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The diagram grid layout is returned")})
    public ResponseEntity<DiagramGridLayout> getDiagramGridLayout(
        @PathVariable("id") UUID diagramGridLayoutUuid) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(diagramGridLayoutService.getByDiagramGridLayoutUuid(diagramGridLayoutUuid));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete diagram grid layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The diagram grid layout is deleted")})
    public ResponseEntity<DiagramGridLayout> deleteDiagramGridLayout(
        @PathVariable("id") UUID diagramGridLayoutUuid) {
        diagramGridLayoutService.deleteDiagramGridLayout(diagramGridLayoutUuid);
        return ResponseEntity.ok().build();
    }

    @PostMapping(params = { DUPLICATE_FROM })
    @Operation(summary = "Duplicate diagram grid layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "The diagram grid layout is duplicated")})
    public ResponseEntity<UUID> duplicateDiagramGridLayout(@RequestParam(name = DUPLICATE_FROM) UUID diagramGridLayoutUuid) {
        UUID newId = diagramGridLayoutService.duplicateDiagramGridLayout(diagramGridLayoutUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

}
