/*
  Copyright (c) 2024, RTE (http://www.rte-france.com)
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.studylayout.StudyLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.AbstractDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.DiagramGridLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NadFromElementDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NetworkAreaDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.SubstationDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.VoltageLevelDiagramLayout;
import org.gridsuite.studyconfig.server.entities.studylayout.AbstractDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.DiagramGridLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.NadFromElementDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.NetworkAreaDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.StudyLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.SubstationDiagramLayoutEntity;
import org.gridsuite.studyconfig.server.entities.studylayout.VoltageLevelLayoutEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper for conversion between StudyLayout DTOs and Entities
 *
 * @author Generated based on StudyLayoutConversionService
 */
public final class StudyLayoutMapper {

    private StudyLayoutMapper() {
        // Private constructor to prevent instantiation
    }

    public static StudyLayoutEntity toEntity(StudyLayout dto) {
        return Optional.ofNullable(dto)
                .map(d -> StudyLayoutEntity.builder()
                        .diagramGridLayoutEntityList(convertDiagramLayouts(d.getDiagramLayoutParams(), StudyLayoutMapper::toDiagramLayoutEntity))
                        .build())
                .orElse(null);
    }

    public static StudyLayout toDto(StudyLayoutEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> StudyLayout.builder()
                        .diagramLayoutParams(convertDiagramLayouts(e.getDiagramGridLayoutEntityList(), StudyLayoutMapper::toDiagramLayoutDto))
                        .build())
                .orElse(null);
    }

    public static AbstractDiagramLayoutEntity toDiagramLayoutEntity(AbstractDiagramLayout dto) {
        return switch (dto) {
            case SubstationDiagramLayout s -> SubstationDiagramLayoutEntity.builder()
                    .diagramUuid(s.getDiagramUuid())
                    .substationId(s.getSubstationId())
                    .gridLayout(convertGridLayoutMap(s.getGridLayout(), StudyLayoutMapper::toGridLayoutEntity))
                    .build();
            case VoltageLevelDiagramLayout v -> VoltageLevelLayoutEntity.builder()
                    .diagramUuid(v.getDiagramUuid())
                    .voltageLevelId(v.getVoltageLevelId())
                    .gridLayout(convertGridLayoutMap(v.getGridLayout(), StudyLayoutMapper::toGridLayoutEntity))
                    .build();
            case NetworkAreaDiagramLayout n -> NetworkAreaDiagramLayoutEntity.builder()
                    .diagramUuid(n.getDiagramUuid())
                    .voltageLevelIds(n.getVoltageLevelIds())
                    .depth(n.getDepth())
                    .gridLayout(convertGridLayoutMap(n.getGridLayout(), StudyLayoutMapper::toGridLayoutEntity))
                    .build();
            case NadFromElementDiagramLayout nad -> NadFromElementDiagramLayoutEntity.builder()
                    .diagramUuid(nad.getDiagramUuid())
                    .elementName(nad.getElementName())
                    .elementType(nad.getElementType())
                    .elementUuid(nad.getElementUuid())
                    .gridLayout(convertGridLayoutMap(nad.getGridLayout(), StudyLayoutMapper::toGridLayoutEntity))
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
                    .gridLayout(convertGridLayoutMap(s.getGridLayout(), StudyLayoutMapper::toGridLayoutDto))
                    .build();
            case VoltageLevelLayoutEntity v -> VoltageLevelDiagramLayout.builder()
                    .diagramUuid(v.getDiagramUuid())
                    .voltageLevelId(v.getVoltageLevelId())
                    .gridLayout(convertGridLayoutMap(v.getGridLayout(), StudyLayoutMapper::toGridLayoutDto))
                    .build();
            case NetworkAreaDiagramLayoutEntity n -> NetworkAreaDiagramLayout.builder()
                    .diagramUuid(n.getDiagramUuid())
                    .voltageLevelIds(n.getVoltageLevelIds())
                    .depth(n.getDepth())
                    .gridLayout(convertGridLayoutMap(n.getGridLayout(), StudyLayoutMapper::toGridLayoutDto))
                    .build();
            case NadFromElementDiagramLayoutEntity nad -> NadFromElementDiagramLayout.builder()
                    .diagramUuid(nad.getDiagramUuid())
                    .elementName(nad.getElementName())
                    .elementType(nad.getElementType())
                    .elementUuid(nad.getElementUuid())
                    .gridLayout(convertGridLayoutMap(nad.getGridLayout(), StudyLayoutMapper::toGridLayoutDto))
                    .build();
            case null -> null;
            default -> throw new IllegalArgumentException("Unknown diagram layout entity type: " + entity.getClass());
        };
    }

    public static DiagramGridLayoutEntity toGridLayoutEntity(DiagramGridLayout dto) {
        return Optional.ofNullable(dto)
                .map(d -> DiagramGridLayoutEntity.builder()
                        .xPosition(d.getX())
                        .yPosition(d.getY())
                        .width(d.getW())
                        .height(d.getH())
                        .build())
                .orElse(null);
    }

    public static DiagramGridLayout toGridLayoutDto(DiagramGridLayoutEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> DiagramGridLayout.builder()
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

    private static <T, R> Map<String, R> convertGridLayoutMap(Map<String, T> sourceMap, Function<T, R> converter) {
        return Optional.ofNullable(sourceMap)
                .map(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> converter.apply(entry.getValue())
                        )))
                .orElse(null);
    }
}
