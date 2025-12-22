/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.workspace;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.entities.AbstractManuallyAssignedIdentifierEntity;

import java.util.UUID;

@SuperBuilder
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

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "is_minimized", nullable = false)
    private boolean isMinimized;

    @Column(name = "is_maximized", nullable = false)
    private boolean isMaximized;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed;

    @Column(name = "restore_position_x")
    private Double restorePositionX;

    @Column(name = "restore_position_y")
    private Double restorePositionY;

    @Column(name = "restore_size_width")
    private Double restoreSizeWidth;

    @Column(name = "restore_size_height")
    private Double restoreSizeHeight;
}
