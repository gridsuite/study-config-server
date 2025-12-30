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
    @CollectionTable(name = "nad_panel_voltage_level_to_omit", joinColumns = @JoinColumn(name = "panel_id"))
    @Column(name = "voltage_level_id")
    private List<String> voltageLevelToOmitIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nad_panel_navigation_history", joinColumns = @JoinColumn(name = "panel_id"))
    @Column(name = "voltage_level_id")
    @OrderColumn(name = "position")
    private List<String> navigationHistory = new ArrayList<>();

    public NADPanelEntity(NADPanelInfos dto) {
        super(dto);
        initEntity(dto);
    }

    @Override
    public void update(PanelInfos dto) {
        super.update(dto);
        initEntity((NADPanelInfos) dto);
    }

    private void initEntity(NADPanelInfos dto) {
        nadConfigUuid = dto.getNadConfigUuid();
        filterUuid = dto.getFilterUuid();
        currentFilterUuid = dto.getCurrentFilterUuid();
        savedWorkspaceConfigUuid = dto.getSavedWorkspaceConfigUuid();
        if (dto.getVoltageLevelToOmitIds() != null) {
            voltageLevelToOmitIds = new ArrayList<>(dto.getVoltageLevelToOmitIds());
        }
        if (dto.getNavigationHistory() != null) {
            navigationHistory = new ArrayList<>(dto.getNavigationHistory());
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
        dto.setNavigationHistory(getNavigationHistory());
        return dto;
    }
}
