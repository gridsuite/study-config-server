/*
  Copyright (c) 2025, RTE (http://www.rte-france.com)
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gridsuite.studyconfig.server.dto.diagramgridlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.AbstractDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.DiagramPosition;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.NetworkAreaDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.SubstationDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.VoltageLevelDiagramLayout;
import org.gridsuite.studyconfig.server.dto.diagramgridlayout.diagramlayout.MapLayout;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.DiagramGridLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.AbstractDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.DiagramPositionEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.NetworkAreaDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.SubstationDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.VoltageLevelLayoutEntity;
import org.gridsuite.studyconfig.server.entities.diagramgridlayout.diagramlayout.MapLayoutEntity;

public final class DiagramGridLayoutMapper {

    private DiagramGridLayoutMapper() {
    }

    public static DiagramGridLayoutEntity toEntity(DiagramGridLayout dto) {
        return Optional.ofNullable(dto)
                .map(d -> DiagramGridLayoutEntity.builder()
                        .diagramLayouts(convertDiagramLayouts(d.getDiagramLayouts(), DiagramGridLayoutMapper::toDiagramLayoutEntity))
                        .build())
                .orElse(null);
    }

    public static DiagramGridLayout toDto(DiagramGridLayoutEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> DiagramGridLayout.builder()
                        .diagramLayouts(convertDiagramLayouts(e.getDiagramLayouts(), DiagramGridLayoutMapper::toDiagramLayoutDto))
                        .build())
                .orElse(null);
    }

    public static AbstractDiagramLayoutEntity toDiagramLayoutEntity(AbstractDiagramLayout dto) {
        return switch (dto) {
            case SubstationDiagramLayout s -> SubstationDiagramLayoutEntity.builder()
                    .diagramUuid(s.getDiagramUuid())
                    .substationId(s.getSubstationId())
                    .diagramPositions(convertDiagramPositionsMap(s.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionEntity))
                    .build();
            case VoltageLevelDiagramLayout v -> VoltageLevelLayoutEntity.builder()
                    .diagramUuid(v.getDiagramUuid())
                    .voltageLevelId(v.getVoltageLevelId())
                    .diagramPositions(convertDiagramPositionsMap(v.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionEntity))
                    .build();
            case NetworkAreaDiagramLayout v -> NetworkAreaDiagramLayoutEntity.builder()
                    .diagramUuid(v.getDiagramUuid())
                    .originalNadConfigUuid(v.getOriginalNadConfigUuid())
                    .currentNadConfigUuid(v.getCurrentNadConfigUuid())
                    .filterUuid(v.getFilterUuid())
                    .name(v.getName())
                    .diagramPositions(convertDiagramPositionsMap(v.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionEntity))
                    .build();
            case MapLayout m -> MapLayoutEntity.builder()
                    .diagramUuid(m.getDiagramUuid())
                    .diagramPositions(convertDiagramPositionsMap(m.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionEntity))
                    .build();
            case null -> null;
            default -> throw new IllegalArgumentException("Unknown diagram layout DTO type: " + dto.getClass());
        };
    }

    public static AbstractDiagramLayout toDiagramLayoutDto(AbstractDiagramLayoutEntity entity) {
        return switch (entity) {
            case SubstationDiagramLayoutEntity s -> SubstationDiagramLayout.builder()
                    .diagramUuid(s.getDiagramUuid())
                    .substationId(s.getSubstationId())
                    .diagramPositions(convertDiagramPositionsMap(s.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionDto))
                    .build();
            case VoltageLevelLayoutEntity v -> VoltageLevelDiagramLayout.builder()
                    .diagramUuid(v.getDiagramUuid())
                    .voltageLevelId(v.getVoltageLevelId())
                    .diagramPositions(convertDiagramPositionsMap(v.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionDto))
                    .build();
            case NetworkAreaDiagramLayoutEntity n -> NetworkAreaDiagramLayout.builder()
                    .diagramUuid(n.getDiagramUuid())
                    .originalNadConfigUuid(n.getOriginalNadConfigUuid())
                    .currentNadConfigUuid(n.getCurrentNadConfigUuid())
                    .filterUuid(n.getFilterUuid())
                    .name(n.getName())
                    .diagramPositions(convertDiagramPositionsMap(n.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionDto))
                    .build();
            case MapLayoutEntity m -> MapLayout.builder()
                    .diagramUuid(m.getDiagramUuid())
                    .diagramPositions(convertDiagramPositionsMap(m.getDiagramPositions(), DiagramGridLayoutMapper::toDiagramPositionDto))
                    .build();
            case null -> null;
            default -> throw new IllegalArgumentException("Unknown diagram layout entity type: " + entity.getClass());
        };
    }

    public static DiagramPositionEntity toDiagramPositionEntity(DiagramPosition dto) {
        return Optional.ofNullable(dto)
                .map(d -> DiagramPositionEntity.builder()
                        .xPosition(d.getX())
                        .yPosition(d.getY())
                        .width(d.getW())
                        .height(d.getH())
                        .build())
                .orElse(null);
    }

    public static DiagramPosition toDiagramPositionDto(DiagramPositionEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> DiagramPosition.builder()
                        .x(e.getXPosition())
                        .y(e.getYPosition())
                        .w(e.getWidth())
                        .h(e.getHeight())
                        .build())
                .orElse(null);
    }

    private static <T, R> List<R> convertDiagramLayouts(List<T> items, Function<T, R> converter) {
        return Optional.ofNullable(items)
                .map(list -> list.stream().map(converter).toList())
                .orElse(List.of());
    }

    private static <T, R> Map<String, R> convertDiagramPositionsMap(Map<String, T> sourceMap, Function<T, R> converter) {
        return Optional.ofNullable(sourceMap)
                .map(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> converter.apply(entry.getValue())
                        )))
                .orElse(null);
    }
}
