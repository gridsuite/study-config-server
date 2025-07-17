/*
  Copyright (c) 2024, RTE (http://www.rte-france.com)
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NadFromElementDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.NetworkAreaDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.SubstationDiagramLayout;
import org.gridsuite.studyconfig.server.dto.studylayout.diagramlayout.VoltageLevelDiagramLayout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson configuration for diagram layout polymorphic serialization.
 * This external configuration avoids circular dependencies between the abstract base class
 * and its subtypes by registering the type mappings outside the class hierarchy.
 */
@Configuration
public class DiagramLayoutJacksonConfiguration {

    @Bean
    public Module diagramLayoutTypeModule() {
        SimpleModule module = new SimpleModule("DiagramLayoutTypeModule");

        // Register subtypes for polymorphic serialization/deserialization
        // This approach avoids circular dependencies that would occur with @JsonSubTypes
        module.registerSubtypes(
            new NamedType(SubstationDiagramLayout.class, "substation"),
            new NamedType(VoltageLevelDiagramLayout.class, "voltage-level"),
            new NamedType(NetworkAreaDiagramLayout.class, "network-area-diagram"),
            new NamedType(NadFromElementDiagramLayout.class, "nad-from-element")
        );

        return module;
    }
}
