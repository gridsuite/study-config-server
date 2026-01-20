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
import org.gridsuite.studyconfig.server.dto.workspace.PanelInfos;
import org.gridsuite.studyconfig.server.dto.workspace.SLDPanelInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sld_panel")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_sld_panel_panel"))
public class SLDPanelEntity extends PanelEntity {

    @Column(name = "equipment_id", nullable = false)
    private String equipmentId;

    @Column(name = "parent_nad_panel_id")
    private UUID parentNadPanelId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "sld_panel_navigation_history",
        joinColumns = @JoinColumn(name = "panel_id", foreignKey = @ForeignKey(name = "fk_sld_panel_navigation_history")),
        indexes = @Index(name = "idx_sld_panel_navigation_history_panel_id", columnList = "panel_id")
    )
    @Column(name = "voltage_level_id")
    @OrderColumn(name = "position")
    private List<String> navigationHistory = new ArrayList<>();

    public SLDPanelEntity(SLDPanelInfos dto) {
        super(dto);
        initEntity(dto);
    }

    @Override
    protected void initEntity(PanelInfos dto) {
        super.initEntity(dto);
        SLDPanelInfos sldDto = (SLDPanelInfos) dto;
        equipmentId = sldDto.getEquipmentId();
        parentNadPanelId = sldDto.getParentNadPanelId();
        if (sldDto.getNavigationHistory() != null) {
            navigationHistory = new ArrayList<>(sldDto.getNavigationHistory());
        }
    }

    @Override
    public SLDPanelInfos toDto() {
        SLDPanelInfos dto = new SLDPanelInfos();
        iniDto(dto);
        dto.setEquipmentId(getEquipmentId());
        dto.setParentNadPanelId(getParentNadPanelId());
        dto.setNavigationHistory(getNavigationHistory());
        return dto;
    }
}
