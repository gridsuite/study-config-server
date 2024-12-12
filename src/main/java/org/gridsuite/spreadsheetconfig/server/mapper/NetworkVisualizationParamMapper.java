/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.mapper;

import org.gridsuite.spreadsheetconfig.server.dto.MapParamInfos;
import org.gridsuite.spreadsheetconfig.server.dto.NetworkAreaDiagramParamInfos;
import org.gridsuite.spreadsheetconfig.server.dto.NetworkVisualizationParamInfos;
import org.gridsuite.spreadsheetconfig.server.dto.SingleLineDiagramParamInfos;
import org.gridsuite.spreadsheetconfig.server.entities.NetworkVisualizationParamEntity;

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
                        entity.getLineFlowColorMode(),
                        entity.getLineFlowAlertThreshold(),
                        entity.getMapManualRefresh(),
                        entity.getMapBaseMap()),
                new SingleLineDiagramParamInfos(
                        entity.getDiagonalLabel(),
                        entity.getCenterLabel(),
                        entity.getSubstationLayout(),
                        entity.getComponentLibrary()
                ),
                new NetworkAreaDiagramParamInfos(entity.getInitNadWithGeoData())
        );
    }

    public static NetworkVisualizationParamEntity toEntity(NetworkVisualizationParamInfos dto) {
        NetworkVisualizationParamEntity entity = new NetworkVisualizationParamEntity();
        updateEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(NetworkVisualizationParamEntity entity, NetworkVisualizationParamInfos dto) {
        // Map
        entity.setLineFullPath(dto.mapParamInfos().lineFullPath());
        entity.setLineParallelPath(dto.mapParamInfos().lineParallelPath());
        entity.setLineFlowMode(dto.mapParamInfos().lineFlowMode());
        entity.setLineFlowColorMode(dto.mapParamInfos().lineFlowColorMode());
        entity.setLineFlowAlertThreshold(dto.mapParamInfos().lineFlowAlertThreshold());
        entity.setMapManualRefresh(dto.mapParamInfos().mapManualRefresh());
        entity.setMapBaseMap(dto.mapParamInfos().mapBaseMap());
        // SLD
        entity.setDiagonalLabel(dto.singleLineDiagramParamInfos().diagonalLabel());
        entity.setCenterLabel(dto.singleLineDiagramParamInfos().centerLabel());
        entity.setSubstationLayout(dto.singleLineDiagramParamInfos().substationLayout());
        entity.setComponentLibrary(dto.singleLineDiagramParamInfos().componentLibrary());
        // NAD
        entity.setInitNadWithGeoData(dto.networkAreaDiagramParamInfos().initNadWithGeoData());
    }
}
