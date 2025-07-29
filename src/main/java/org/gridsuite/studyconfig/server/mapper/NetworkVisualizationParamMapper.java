/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.MapParamInfos;
import org.gridsuite.studyconfig.server.dto.NetworkAreaDiagramParamInfos;
import org.gridsuite.studyconfig.server.dto.NetworkVisualizationParamInfos;
import org.gridsuite.studyconfig.server.dto.SingleLineDiagramParamInfos;
import org.gridsuite.studyconfig.server.entities.NetworkVisualizationParamEntity;

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
                new NetworkAreaDiagramParamInfos(entity.getNadGenerationMode(), entity.getNadConfigUuid())
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
        entity.setNadConfigUuid(dto.networkAreaDiagramParameters().nadConfigUuid());
        entity.setNadGenerationMode(dto.networkAreaDiagramParameters().nadGenerationMode());
    }
}
