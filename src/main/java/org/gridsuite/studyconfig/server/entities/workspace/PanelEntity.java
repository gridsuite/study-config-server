/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.workspace;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.AbstractManuallyAssignedIdentifierEntity;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "panel")
@Inheritance(strategy = InheritanceType.JOINED)
public class PanelEntity extends AbstractManuallyAssignedIdentifierEntity<UUID> {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PanelType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "position_x", nullable = false)
    private double positionX;

    @Column(name = "position_y", nullable = false)
    private double positionY;

    @Column(name = "size_width", nullable = false)
    private double sizeWidth;

    @Column(name = "size_height", nullable = false)
    private double sizeHeight;

    @Column(name = "is_minimized", nullable = false)
    private boolean minimized;

    @Column(name = "is_maximized", nullable = false)
    private boolean maximized;

    @Column(name = "is_pinned", nullable = false)
    private boolean pinned;

    @Column(name = "restore_position_x")
    private Double restorePositionX;

    @Column(name = "restore_position_y")
    private Double restorePositionY;

    @Column(name = "restore_size_width")
    private Double restoreSizeWidth;

    @Column(name = "restore_size_height")
    private Double restoreSizeHeight;

    public static PanelEntity toEntity(PanelInfos dto) {
        PanelEntity entity = switch (dto) {
            case NADPanelInfos nad -> new NADPanelEntity(nad);
            case SLDPanelInfos sld -> new SLDPanelEntity(sld);
            default -> new PanelEntity(dto);
        };
        // Ensure ID is set for entities that require manual assignment
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return entity;
    }

    public PanelEntity(PanelInfos dto) {
        id = dto.getId();
        type = dto.getType();
        initEntity(dto);
    }

    public void update(PanelInfos dto) {
        initEntity(dto);
    }

    protected void initEntity(PanelInfos dto) {
        title = dto.getTitle();
        positionX = dto.getPosition().x();
        positionY = dto.getPosition().y();
        sizeWidth = dto.getSize().width();
        sizeHeight = dto.getSize().height();
        minimized = dto.isMinimized();
        maximized = dto.isMaximized();
        pinned = dto.isPinned();
        if (dto.getRestorePosition() != null) {
            restorePositionX = dto.getRestorePosition().x();
            restorePositionY = dto.getRestorePosition().y();
        }
        if (dto.getRestoreSize() != null) {
            restoreSizeWidth = dto.getRestoreSize().width();
            restoreSizeHeight = dto.getRestoreSize().height();
        }
    }

    protected void iniDto(PanelInfos dto) {
        dto.setId(getId());
        dto.setType(getType());
        dto.setTitle(getTitle());
        dto.setPosition(new PanelPositionInfos(getPositionX(), getPositionY()));
        dto.setSize(new PanelSizeInfos(getSizeWidth(), getSizeHeight()));
        dto.setMinimized(isMinimized());
        dto.setMaximized(isMaximized());
        dto.setPinned(isPinned());
        if (getRestorePositionX() != null) {
            dto.setRestorePosition(new PanelPositionInfos(getRestorePositionX(), getRestorePositionY()));
        }
        if (getRestoreSizeWidth() != null) {
            dto.setRestoreSize(new PanelSizeInfos(getRestoreSizeWidth(), getRestoreSizeHeight()));
        }
    }

    public PanelInfos toDto() {
        PanelInfos dto = new PanelInfos();
        iniDto(dto);
        return dto;
    }

    public PanelEntity duplicate() {
        PanelInfos dto = this.toDto();
        dto.setId(null);
        return PanelEntity.toEntity(dto);
    }
}
