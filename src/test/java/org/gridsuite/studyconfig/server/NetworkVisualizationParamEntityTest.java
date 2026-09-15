/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import org.gridsuite.studyconfig.server.entities.NetworkVisualizationParamEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Antoine Bouhours <antoine.bouhours at rte-france.com>
 */
class NetworkVisualizationParamEntityTest {

    @Test
    void testDefaultValues() {
        NetworkVisualizationParamEntity entity = new NetworkVisualizationParamEntity();

        assertThat(entity)
                .satisfies(e -> {
                    assertThat(e.getLineFullPath()).isFalse();
                    assertThat(e.getLineParallelPath()).isTrue();
                    assertThat(e.getLineFlowMode()).isEqualTo("feeders");
                    assertThat(e.getMapManualRefresh()).isTrue();
                    assertThat(e.getMapBaseMap()).isNull();
                    assertThat(e.getDiagonalLabel()).isTrue();
                    assertThat(e.getCenterLabel()).isFalse();
                    assertThat(e.getSubstationLayout()).isEqualTo("horizontal");
                    assertThat(e.getComponentLibrary()).isEqualTo("GridSuiteAndConvergence");
                    assertThat(e.getNadPositionsGenerationMode()).isNull();
                    assertThat(e.getMeasurements()).isFalse();
                    assertThat(e.getObservability()).isFalse();
                });
    }
}
