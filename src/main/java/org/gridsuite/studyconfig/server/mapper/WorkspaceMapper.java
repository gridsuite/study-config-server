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
import java.util.List;

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
        return switch (entity) {
            case NADPanelEntity nad -> {
                NADPanelInfos dto = new NADPanelInfos();
                copyBaseFields(entity, dto);
                dto.setNadConfigUuid(nad.getNadConfigUuid());
                dto.setFilterUuid(nad.getFilterUuid());
                dto.setCurrentFilterUuid(nad.getCurrentFilterUuid());
                dto.setSavedWorkspaceConfigUuid(nad.getSavedWorkspaceConfigUuid());
                dto.setVoltageLevelToOmitIds(nad.getVoltageLevelToOmitIds());
                dto.setNavigationHistory(nad.getNavigationHistory());
                yield dto;
            }
            case SLDPanelEntity sld -> {
                SLDPanelInfos dto = new SLDPanelInfos();
                copyBaseFields(entity, dto);
                dto.setDiagramId(sld.getDiagramId());
                dto.setParentNadPanelId(sld.getParentNadPanelId());
                dto.setNavigationHistory(sld.getNavigationHistory());
                yield dto;
            }
            default -> {
                PanelInfos dto = new PanelInfos();
                copyBaseFields(entity, dto);
                yield dto;
            }
        };
    }

    private static void copyBaseFields(PanelEntity entity, PanelInfos dto) {
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setTitle(entity.getTitle());
        dto.setPosition(new PanelPositionInfos(entity.getPositionX(), entity.getPositionY()));
        dto.setSize(new PanelSizeInfos(entity.getSizeWidth(), entity.getSizeHeight()));
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setMinimized(entity.isMinimized());
        dto.setMaximized(entity.isMaximized());
        dto.setPinned(entity.isPinned());
        dto.setClosed(entity.isClosed());
        if (entity.getRestorePositionX() != null) {
            dto.setRestorePosition(new PanelPositionInfos(entity.getRestorePositionX(), entity.getRestorePositionY()));
        }
        if (entity.getRestoreSizeWidth() != null) {
            dto.setRestoreSize(new PanelSizeInfos(entity.getRestoreSizeWidth(), entity.getRestoreSizeHeight()));
        }
    }

    public static PanelEntity toPanelEntity(PanelInfos dto) {
        return switch (dto) {
            case NADPanelInfos nad -> {
                NADPanelEntity entity = NADPanelEntity.builder()
                    .nadConfigUuid(nad.getNadConfigUuid())
                    .filterUuid(nad.getFilterUuid())
                    .currentFilterUuid(nad.getCurrentFilterUuid())
                    .savedWorkspaceConfigUuid(nad.getSavedWorkspaceConfigUuid())
                    .build();

                copyBaseFieldsToEntity(entity, nad);
                entity.setType(PanelType.NAD);
                copyNADLists(nad.getVoltageLevelToOmitIds(), nad.getNavigationHistory(), entity);

                yield entity;
            }
            case SLDPanelInfos sld -> {
                SLDPanelEntity entity = SLDPanelEntity.builder()
                    .diagramId(sld.getDiagramId())
                    .parentNadPanelId(sld.getParentNadPanelId())
                    .build();

                copyBaseFieldsToEntity(entity, sld);
                entity.setType(sld.getType());
                copyNavigationList(sld.getNavigationHistory(), entity);

                yield entity;
            }
            default -> {
                PanelEntity entity = PanelEntity.builder().build();
                copyBaseFieldsToEntity(entity, dto);
                entity.setType(dto.getType());
                yield entity;
            }
        };
    }

    private static void setRestoreFields(PanelEntity entity, PanelInfos dto) {
        if (dto.getRestorePosition() != null) {
            entity.setRestorePositionX(dto.getRestorePosition().x());
            entity.setRestorePositionY(dto.getRestorePosition().y());
        }
        if (dto.getRestoreSize() != null) {
            entity.setRestoreSizeWidth(dto.getRestoreSize().width());
            entity.setRestoreSizeHeight(dto.getRestoreSize().height());
        }
    }

    private static void copyBaseFieldsToEntity(PanelEntity entity, PanelInfos dto) {
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setPositionX(dto.getPosition().x());
        entity.setPositionY(dto.getPosition().y());
        entity.setSizeWidth(dto.getSize().width());
        entity.setSizeHeight(dto.getSize().height());
        entity.setOrderIndex(dto.getOrderIndex());
        entity.setMinimized(dto.isMinimized());
        entity.setMaximized(dto.isMaximized());
        entity.setPinned(dto.isPinned());
        entity.setClosed(dto.isClosed());
        setRestoreFields(entity, dto);
    }

    private static void copyNADLists(List<String> voltageLevelIds, List<String> navigationHistory, NADPanelEntity entity) {
        if (voltageLevelIds != null) {
            entity.setVoltageLevelToOmitIds(new ArrayList<>(voltageLevelIds));
        }
        if (navigationHistory != null) {
            entity.setNavigationHistory(new ArrayList<>(navigationHistory));
        }
    }

    private static void copyNavigationList(List<String> navigationHistory, SLDPanelEntity entity) {
        if (navigationHistory != null) {
            entity.setNavigationHistory(new ArrayList<>(navigationHistory));
        }
    }

    public static void copyFieldsFromEntityToEntity(PanelEntity source, PanelEntity target) {
        target.setType(source.getType());
        target.setTitle(source.getTitle());
        target.setPositionX(source.getPositionX());
        target.setPositionY(source.getPositionY());
        target.setSizeWidth(source.getSizeWidth());
        target.setSizeHeight(source.getSizeHeight());
        target.setOrderIndex(source.getOrderIndex());
        target.setMinimized(source.isMinimized());
        target.setMaximized(source.isMaximized());
        target.setPinned(source.isPinned());
        target.setClosed(source.isClosed());
        target.setRestorePositionX(source.getRestorePositionX());
        target.setRestorePositionY(source.getRestorePositionY());
        target.setRestoreSizeWidth(source.getRestoreSizeWidth());
        target.setRestoreSizeHeight(source.getRestoreSizeHeight());

        if (source instanceof NADPanelEntity nadSource && target instanceof NADPanelEntity nadTarget) {
            nadTarget.setNadConfigUuid(nadSource.getNadConfigUuid());
            nadTarget.setFilterUuid(nadSource.getFilterUuid());
            nadTarget.setCurrentFilterUuid(nadSource.getCurrentFilterUuid());
            nadTarget.setSavedWorkspaceConfigUuid(nadSource.getSavedWorkspaceConfigUuid());
            nadTarget.setVoltageLevelToOmitIds(nadSource.getVoltageLevelToOmitIds());
            nadTarget.setNavigationHistory(nadSource.getNavigationHistory());
        } else if (source instanceof SLDPanelEntity sldSource && target instanceof SLDPanelEntity sldTarget) {
            sldTarget.setDiagramId(sldSource.getDiagramId());
            sldTarget.setParentNadPanelId(sldSource.getParentNadPanelId());
            sldTarget.setNavigationHistory(sldSource.getNavigationHistory());
        }
    }
}
