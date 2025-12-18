/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.mapper;

import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.workspace.*;

import java.util.ArrayList;

public final class WorkspaceMapper {

    private WorkspaceMapper() {
    }

    public static WorkspacesConfigInfos toDto(WorkspacesConfigEntity entity) {
        return new WorkspacesConfigInfos(
            entity.getId(),
            entity.getWorkspaces().stream()
                .map(WorkspaceMapper::toWorkspaceDto)
                .toList()
        );
    }

    public static WorkspacesConfigEntity toEntity(WorkspacesConfigInfos dto) {
        WorkspacesConfigEntity entity = WorkspacesConfigEntity.builder()
            .id(dto.id())
            .build();

        if (dto.workspaces() != null) {
            entity.setWorkspaces(dto.workspaces().stream()
                .map(WorkspaceMapper::toWorkspaceEntity)
                .toList());
        }

        return entity;
    }

    public static WorkspaceInfos toWorkspaceDto(WorkspaceEntity entity) {
        return new WorkspaceInfos(
            entity.getId(),
            entity.getName(),
            entity.getPanels().stream()
                .map(WorkspaceMapper::toPanelDto)
                .toList()
        );
    }

    public static WorkspaceEntity toWorkspaceEntity(WorkspaceInfos dto) {
        WorkspaceEntity entity = WorkspaceEntity.builder()
            .id(dto.id())
            .name(dto.name())
            .build();

        if (dto.panels() != null) {
            entity.setPanels(dto.panels().stream()
                .map(WorkspaceMapper::toPanelEntity)
                .toList());
        }

        return entity;
    }

    public static PanelInfos toPanelDto(PanelEntity entity) {
        PanelMetadataInfos metadata = null;

        if (entity.getMetadata() instanceof NADPanelMetadataEntity nadEntity) {
            metadata = new NADPanelMetadataInfos(
                nadEntity.getNadConfigUuid(),
                nadEntity.getFilterUuid(),
                nadEntity.getCurrentFilterUuid(),
                nadEntity.getSavedWorkspaceConfigUuid(),
                nadEntity.getVoltageLevelToOmitIds(),
                nadEntity.getNadNavigationHistory()
            );
        } else if (entity.getMetadata() instanceof SLDPanelMetadataEntity sldEntity) {
            metadata = new SLDPanelMetadataInfos(
                sldEntity.getDiagramId(),
                sldEntity.getParentNadPanelId(),
                sldEntity.getSldNavigationHistory()
            );
        }

        return new PanelInfos(
            entity.getId(),
            entity.getType(),
            entity.getTitle(),
            new PanelPositionInfos(entity.getPositionX(), entity.getPositionY()),
            new PanelSizeInfos(entity.getSizeWidth(), entity.getSizeHeight()),
            entity.getOrderIndex(),
            entity.isMinimized(),
            entity.isMaximized(),
            entity.isPinned(),
            entity.isClosed(),
            entity.getRestorePositionX() != null ?
                new PanelPositionInfos(entity.getRestorePositionX(), entity.getRestorePositionY()) : null,
            entity.getRestoreSizeWidth() != null ?
                new PanelSizeInfos(entity.getRestoreSizeWidth(), entity.getRestoreSizeHeight()) : null,
            metadata
        );
    }

    public static PanelEntity toPanelEntity(PanelInfos dto) {
        PanelEntity entity = PanelEntity.builder()
            .id(dto.id())
            .type(dto.type())
            .title(dto.title())
            .positionX(dto.position().x())
            .positionY(dto.position().y())
            .sizeWidth(dto.size().width())
            .sizeHeight(dto.size().height())
            .orderIndex(dto.orderIndex())
            .isMinimized(dto.isMinimized())
            .isMaximized(dto.isMaximized())
            .isPinned(dto.isPinned())
            .isClosed(dto.isClosed())
            .build();

        setRestorePositionIfPresent(entity, dto);
        setRestoreSizeIfPresent(entity, dto);
        setMetadataIfPresent(entity, dto);

        return entity;
    }

    private static void setRestorePositionIfPresent(PanelEntity entity, PanelInfos dto) {
        if (dto.restorePosition() != null) {
            entity.setRestorePositionX(dto.restorePosition().x());
            entity.setRestorePositionY(dto.restorePosition().y());
        }
    }

    private static void setRestoreSizeIfPresent(PanelEntity entity, PanelInfos dto) {
        if (dto.restoreSize() != null) {
            entity.setRestoreSizeWidth(dto.restoreSize().width());
            entity.setRestoreSizeHeight(dto.restoreSize().height());
        }
    }

    private static void setMetadataIfPresent(PanelEntity entity, PanelInfos dto) {
        if (dto.metadata() != null) {
            switch (dto.metadata()) {
                case NADPanelMetadataInfos nad -> {
                    NADPanelMetadataEntity nadEntity = new NADPanelMetadataEntity();
                    nadEntity.setNadConfigUuid(nad.nadConfigUuid());
                    nadEntity.setFilterUuid(nad.filterUuid());
                    nadEntity.setCurrentFilterUuid(nad.currentFilterUuid());
                    nadEntity.setSavedWorkspaceConfigUuid(nad.savedWorkspaceConfigUuid());
                    if (nad.voltageLevelToOmitIds() != null) {
                        nadEntity.setVoltageLevelToOmitIds(new ArrayList<>(nad.voltageLevelToOmitIds()));
                    }
                    if (nad.nadNavigationHistory() != null) {
                        nadEntity.setNadNavigationHistory(new ArrayList<>(nad.nadNavigationHistory()));
                    }
                    entity.setMetadata(nadEntity);
                }
                case SLDPanelMetadataInfos sld -> {
                    SLDPanelMetadataEntity sldEntity = new SLDPanelMetadataEntity();
                    sldEntity.setDiagramId(sld.diagramId());
                    sldEntity.setParentNadPanelId(sld.parentNadPanelId());
                    if (sld.sldNavigationHistory() != null) {
                        sldEntity.setSldNavigationHistory(new ArrayList<>(sld.sldNavigationHistory()));
                    }
                    entity.setMetadata(sldEntity);
                }
            }
        }
    }
}
