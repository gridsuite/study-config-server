package org.gridsuite.studyconfig.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.StudyConfigApi;
import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;
import org.gridsuite.studyconfig.server.service.StudyLayoutService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/" + StudyConfigApi.API_VERSION + "/study-layout")
@RequiredArgsConstructor
@Tag(name = "Study Layout Config", description = "Study Layout Configuration API")
public class StudyLayoutController {
    private final StudyLayoutService studyLayoutService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Save study layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The newly created study layout UUID returned")})
    public ResponseEntity<UUID> saveStudyLayout(@RequestBody StudyLayout studyLayout) {
        return ResponseEntity.ok().body(studyLayoutService.saveStudyLayout(studyLayout));
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update study layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The study layout has been updated")})
    public ResponseEntity<Void> updateStudyLayout(@PathVariable("id") UUID studyLayoutUuid,
                                                  @RequestBody StudyLayout studyLayout) {
        studyLayoutService.updateStudyLayout(studyLayoutUuid, studyLayout);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get study layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The study layout is returned")})
    public ResponseEntity<StudyLayout> getStudyLayout(
        @PathVariable("id") UUID studyLayoutUuid) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(studyLayoutService.getByStudyLayoutUuid(studyLayoutUuid));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete study layout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The study layout is deleted")})
    public ResponseEntity<StudyLayout> deleteStudyLayout(
        @PathVariable("id") UUID studyLayoutUuid) {
        studyLayoutService.deleteStudyLayout(studyLayoutUuid);
        return ResponseEntity.ok().build();
    }
}
