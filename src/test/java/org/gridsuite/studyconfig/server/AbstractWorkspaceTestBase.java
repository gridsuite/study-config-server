/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import org.gridsuite.studyconfig.server.dto.workspace.*;
import org.gridsuite.studyconfig.server.entities.workspace.PanelType;
import org.gridsuite.studyconfig.server.entities.workspace.WorkspacesConfigEntity;
import org.gridsuite.studyconfig.server.repositories.WorkspaceRepository;
import org.gridsuite.studyconfig.server.repositories.WorkspacesConfigRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@SpringBootTest
public abstract class AbstractWorkspaceTestBase {

    @Autowired
    protected WorkspaceRepository workspaceRepository;

    @Autowired
    protected WorkspacesConfigRepository workspacesConfigRepository;

    protected static final String WORKSPACE_EMPTY = "Empty Workspace";
    protected static final String WORKSPACE_WITH_PANELS = "Workspace with Panels";
    protected static final String WORKSPACE_WITH_NAD = "Workspace with NAD";

    protected UUID configId;
    protected UUID emptyWorkspaceId;
    protected UUID workspaceWithPanelsId;
    protected UUID workspaceWithNadId;

    @BeforeEach
    void setUpBase() {
        WorkspacesConfigEntity configEntity = workspacesConfigRepository.save(createTestConfigEntity());
        configId = configEntity.getId();

        List<WorkspaceInfos> workspaces = configEntity.toDto().workspaces();
        emptyWorkspaceId = workspaces.get(0).id();
        workspaceWithPanelsId = workspaces.get(1).id();
        workspaceWithNadId = workspaces.get(2).id();
    }

    @AfterEach
    void tearDownBase() {
        workspacesConfigRepository.deleteAll();
        workspaceRepository.deleteAll();
    }

    protected WorkspacesConfigInfos createTestWorkspacesConfig() {
        WorkspaceInfos emptyWorkspace = new WorkspaceInfos(null, WORKSPACE_EMPTY, List.of());

        WorkspaceInfos workspaceWithPanels = new WorkspaceInfos(
            null,
            WORKSPACE_WITH_PANELS,
            List.of(
                createPanel(PanelType.TREE, "Tree Panel"),
                createPanel(PanelType.SPREADSHEET, "Spreadsheet Panel")
            )
        );

        UUID nadConfigId = UUID.randomUUID();
        NADPanelInfos nadPanel = createNadPanel("NAD Panel", nadConfigId);
        SLDPanelInfos sldPanel = createSldPanel("SLD Panel", "vl1", nadPanel.getId());

        WorkspaceInfos workspaceWithNad = new WorkspaceInfos(
            null,
            WORKSPACE_WITH_NAD,
            List.of(nadPanel, sldPanel)
        );

        return new WorkspacesConfigInfos(null, List.of(emptyWorkspace, workspaceWithPanels, workspaceWithNad));
    }

    protected WorkspacesConfigEntity createTestConfigEntity() {
        return new WorkspacesConfigEntity(createTestWorkspacesConfig());
    }

    protected PanelInfos createPanel(PanelType type, String title) {
        PanelInfos panel = new PanelInfos();
        panel.setId(UUID.randomUUID());
        panel.setType(type);
        panel.setTitle(title);
        panel.setPosition(new PanelPositionInfos(0.0, 0.0));
        panel.setSize(new PanelSizeInfos(0.5, 1.0));
        panel.setMinimized(false);
        panel.setMaximized(false);
        panel.setPinned(false);
        return panel;
    }

    protected NADPanelInfos createNadPanel(String title, UUID nadConfigId) {
        NADPanelInfos nadPanel = new NADPanelInfos();
        nadPanel.setId(UUID.randomUUID());
        nadPanel.setType(PanelType.NAD);
        nadPanel.setTitle(title);
        nadPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        nadPanel.setSize(new PanelSizeInfos(1.0, 1.0));
        nadPanel.setMinimized(false);
        nadPanel.setMaximized(false);
        nadPanel.setPinned(false);
        nadPanel.setCurrentNadConfigUuid(nadConfigId);
        return nadPanel;
    }

    protected SLDPanelInfos createSldPanel(String title, String equipmentId, UUID parentNadPanelId) {
        SLDPanelInfos sldPanel = new SLDPanelInfos();
        sldPanel.setId(UUID.randomUUID());
        sldPanel.setType(PanelType.SLD_VOLTAGE_LEVEL);
        sldPanel.setTitle(title);
        sldPanel.setPosition(new PanelPositionInfos(0.0, 0.0));
        sldPanel.setSize(new PanelSizeInfos(1.0, 1.0));
        sldPanel.setMinimized(false);
        sldPanel.setMaximized(false);
        sldPanel.setPinned(false);
        sldPanel.setEquipmentId(equipmentId);
        sldPanel.setParentNadPanelId(parentNadPanelId);
        return sldPanel;
    }
}
