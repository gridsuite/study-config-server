/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.gridsuite.studyconfig.server.dto.NadPositionsGenerationMode;
import org.gridsuite.studyconfig.server.dto.NetworkVisualizationParamInfos;
import org.gridsuite.studyconfig.server.entities.NetworkVisualizationParamEntity;
import org.gridsuite.studyconfig.server.mapper.NetworkVisualizationParamMapper;
import org.gridsuite.studyconfig.server.repositories.NetworkVisualizationParamRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author David BRAQUART <david.braquart at rte-france.com>
 */
@Service
@RequiredArgsConstructor
public class NetworkVisualizationsParamService {

    private final NetworkVisualizationParamRepository repository;

    @Value("${study-config.nad-positions-generation-default-mode:}")
    private NadPositionsGenerationMode nadPositionsGenerationDefaultMode;

    @Transactional
    public UUID createDefaultParameters() {
        NetworkVisualizationParamEntity entity = new NetworkVisualizationParamEntity();
        entity.setNadPositionsGenerationMode(nadPositionsGenerationDefaultMode.toString());
        return repository.save(entity).getId();
    }

    @Transactional
    public UUID createParameters(NetworkVisualizationParamInfos dto) {
        NetworkVisualizationParamEntity entity = NetworkVisualizationParamMapper.toEntity(dto);
        return repository.save(entity).getId();
    }

    @Transactional
    public UUID duplicateParameters(UUID id) {
        NetworkVisualizationParamEntity entity = findEntityById(id);
        NetworkVisualizationParamEntity duplicate = NetworkVisualizationParamEntity.builder()
                // Map
                .lineFullPath(entity.getLineFullPath())
                .lineParallelPath(entity.getLineParallelPath())
                .lineFlowMode(entity.getLineFlowMode())
                .mapManualRefresh(entity.getMapManualRefresh())
                .mapBaseMap(entity.getMapBaseMap())
                // SLD
                .diagonalLabel(entity.getDiagonalLabel())
                .centerLabel(entity.getCenterLabel())
                .substationLayout(entity.getSubstationLayout())
                .componentLibrary(entity.getComponentLibrary())
                // NAD
                .nadPositionsGenerationMode(entity.getNadPositionsGenerationMode())
                .nadPositionsConfigUuid(entity.getNadPositionsConfigUuid())
                .build();
        return repository.save(duplicate).getId();
    }

    @Transactional(readOnly = true)
    public NetworkVisualizationParamInfos getParameters(UUID id) {
        return NetworkVisualizationParamMapper.toDto(findEntityById(id), nadPositionsGenerationDefaultMode);
    }

    @Transactional
    public void updateParameters(UUID id, NetworkVisualizationParamInfos dto) {
        NetworkVisualizationParamEntity entity = findEntityById(id);
        NetworkVisualizationParamMapper.updateEntity(entity, dto);
    }

    @Transactional
    public void updateNadPositionsConfigUuid(UUID id, UUID nadPositionsConfigUuid) {
        NetworkVisualizationParamEntity entity = findEntityById(id);
        NetworkVisualizationParamMapper.updateNadPositionsConfigUuid(entity, nadPositionsConfigUuid);
    }

    @Transactional
    public void deleteParameters(UUID id) {
        if (!repository.existsById(id)) {
            throw entityNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private NetworkVisualizationParamEntity findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> entityNotFoundException(id));
    }

    private EntityNotFoundException entityNotFoundException(UUID id) {
        return new EntityNotFoundException("NetworkVisualizationParam not found with id: " + id);
    }
}
