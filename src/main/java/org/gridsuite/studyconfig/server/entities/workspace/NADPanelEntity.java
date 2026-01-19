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
import org.gridsuite.studyconfig.server.dto.workspace.NADPanelInfos;
import org.gridsuite.studyconfig.server.dto.workspace.PanelInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "nad_panel")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_nad_panel_panel"))
public class NADPanelEntity extends PanelEntity {

    @Column(name = "nad_config_uuid")
    private UUID nadConfigUuid;

    @Column(name = "filter_uuid")
    private UUID filterUuid;

    @Column(name = "current_filter_uuid")
    private UUID currentFilterUuid;

    @Column(name = "saved_workspace_config_uuid")
    private UUID savedWorkspaceConfigUuid;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "nad_panel_voltage_level_to_omit",
        joinColumns = @JoinColumn(name = "panel_id", foreignKey = @ForeignKey(name = "fk_nad_panel_voltage_level_to_omit")),
        indexes = @Index(name = "idx_nad_panel_voltage_level_to_omit_panel_id", columnList = "panel_id")
    )
    @Column(name = "voltage_level_id")
    private List<String> voltageLevelToOmitIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "nad_panel_initial_voltage_levels",
        joinColumns = @JoinColumn(name = "panel_id", foreignKey = @ForeignKey(name = "fk_nad_panel_initial_voltage_levels")),
        indexes = @Index(name = "idx_nad_panel_initial_voltage_levels_panel_id", columnList = "panel_id")
    )
    @Column(name = "voltage_level_id")
    private List<String> initialVoltageLevelIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "nad_panel_navigation_history",
        joinColumns = @JoinColumn(name = "panel_id", foreignKey = @ForeignKey(name = "fk_nad_panel_navigation_history")),
        indexes = @Index(name = "idx_nad_panel_navigation_history_panel_id", columnList = "panel_id")
    )
    @Column(name = "voltage_level_id")
    @OrderColumn(name = "position")
    private List<String> navigationHistory = new ArrayList<>();

    public NADPanelEntity(NADPanelInfos dto) {
        super(dto);
        initEntity(dto);
    }

    @Override
    protected void initEntity(PanelInfos dto) {
        super.initEntity(dto);
        NADPanelInfos nadDto = (NADPanelInfos) dto;
        nadConfigUuid = nadDto.getNadConfigUuid();
        filterUuid = nadDto.getFilterUuid();
        currentFilterUuid = nadDto.getCurrentFilterUuid();
        savedWorkspaceConfigUuid = nadDto.getSavedWorkspaceConfigUuid();
        if (nadDto.getVoltageLevelToOmitIds() != null) {
            voltageLevelToOmitIds = new ArrayList<>(nadDto.getVoltageLevelToOmitIds());
        }
        if (nadDto.getInitialVoltageLevelIds() != null) {
            initialVoltageLevelIds = new ArrayList<>(nadDto.getInitialVoltageLevelIds());
        }
        if (nadDto.getNavigationHistory() != null) {
            navigationHistory = new ArrayList<>(nadDto.getNavigationHistory());
        }
    }

    @Override
    public NADPanelInfos toDto() {
        NADPanelInfos dto = new NADPanelInfos();
        iniDto(dto);
        dto.setNadConfigUuid(getNadConfigUuid());
        dto.setFilterUuid(getFilterUuid());
        dto.setCurrentFilterUuid(getCurrentFilterUuid());
        dto.setSavedWorkspaceConfigUuid(getSavedWorkspaceConfigUuid());
        dto.setVoltageLevelToOmitIds(getVoltageLevelToOmitIds());
        dto.setInitialVoltageLevelIds(getInitialVoltageLevelIds());
        dto.setNavigationHistory(getNavigationHistory());
        return dto;
    }
}
