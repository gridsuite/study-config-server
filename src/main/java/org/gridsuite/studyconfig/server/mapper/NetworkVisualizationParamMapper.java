/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.entities.NetworkVisualizationParamEntity;

import java.util.UUID;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
public final class NetworkVisualizationParamMapper {

    private NetworkVisualizationParamMapper() {
    }

    public static NetworkVisualizationParamInfos toDto(NetworkVisualizationParamEntity entity) {
        return new NetworkVisualizationParamInfos(
                entity.getId(),
                new MapParamInfos(
                        entity.getLineFullPath(),
                        entity.getLineParallelPath(),
                        entity.getLineFlowMode(),
                        entity.getMapManualRefresh(),
                        entity.getMapBaseMap()),
                new SingleLineDiagramParamInfos(
                        entity.getDiagonalLabel(),
                        entity.getCenterLabel(),
                        entity.getSubstationLayout(),
                        entity.getComponentLibrary()
                ),
                new NetworkAreaDiagramParamInfos(NadPositionsGenerationMode.valueOf(entity.getNadPositionsGenerationMode()), entity.getNadPositionsConfigUuid())
        );
    }

    public static NetworkVisualizationParamEntity toEntity(NetworkVisualizationParamInfos dto) {
        NetworkVisualizationParamEntity entity = new NetworkVisualizationParamEntity();
        updateEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(NetworkVisualizationParamEntity entity, NetworkVisualizationParamInfos dto) {
        // Map
        entity.setLineFullPath(dto.mapParameters().lineFullPath());
        entity.setLineParallelPath(dto.mapParameters().lineParallelPath());
        entity.setLineFlowMode(dto.mapParameters().lineFlowMode());
        entity.setMapManualRefresh(dto.mapParameters().mapManualRefresh());
        entity.setMapBaseMap(dto.mapParameters().mapBaseMap());
        // SLD
        entity.setDiagonalLabel(dto.singleLineDiagramParameters().diagonalLabel());
        entity.setCenterLabel(dto.singleLineDiagramParameters().centerLabel());
        entity.setSubstationLayout(dto.singleLineDiagramParameters().substationLayout());
        entity.setComponentLibrary(dto.singleLineDiagramParameters().componentLibrary());
        // NAD
        entity.setNadPositionsConfigUuid(dto.networkAreaDiagramParameters().nadPositionsConfigUuid());
        entity.setNadPositionsGenerationMode(dto.networkAreaDiagramParameters().nadPositionsGenerationMode().name());
    }

    public static void updateNadPositionsConfigUuid(NetworkVisualizationParamEntity entity, UUID nadPositionsConfigUuid) {
        entity.setNadPositionsConfigUuid(nadPositionsConfigUuid);
    }
}
