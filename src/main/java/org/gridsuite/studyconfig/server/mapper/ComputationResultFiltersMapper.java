/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.entities.*;

import java.util.List;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
public final class ComputationResultFiltersMapper {

    private ComputationResultFiltersMapper() { }

    public static ComputationResultFiltersInfos toDto(ComputationResultFiltersEntity entity) {
        List<ComputationTypeFiltersInfos> typeDTOs = entity.getComputationResultFilter().stream()
                .map(ComputationResultFiltersMapper::toTypeDto)
                .toList();

        return new ComputationResultFiltersInfos(
                entity.getId(),
                typeDTOs
        );
    }

    private static ComputationTypeFiltersInfos toTypeDto(ComputationTypeFiltersEntity typeEntity) {
        List<GlobalFilterInfos> globalFilters = typeEntity.getGlobalFilters().stream()
                .map(CommonFiltersMapper::toGlobalFilterDto)
                .toList();

        List<ComputationSubTypeFilterInfos> subTypeDTOs = typeEntity.getComputationSubTypeResultFilter().stream()
                .map(ComputationResultFiltersMapper::toSubTypeDto)
                .toList();

        return new ComputationTypeFiltersInfos(
                typeEntity.getId(),
                typeEntity.getComputationType(),
                globalFilters,
                subTypeDTOs
        );
    }

    private static ComputationSubTypeFilterInfos toSubTypeDto(ComputationSubTypeFiltersEntity entity) {
        List<ColumnFilterInfos> columns = entity.getColumns().stream()
                .map(CommonFiltersMapper::toColumnFilterDto)
                .toList();

        return new ComputationSubTypeFilterInfos(
                entity.getId(),
                entity.getComputationSubType(),
                columns
        );
    }
}
