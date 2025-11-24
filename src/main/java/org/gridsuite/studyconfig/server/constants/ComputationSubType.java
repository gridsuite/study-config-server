/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.constants;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */

public enum ComputationSubType {
    LOADFLOW_CURRENT_LIMIT_VIOLATION,
    LOADFLOW_VOLTAGE_LIMIT_VIOLATION,
    SECURITY_ANALYSIS_RESULT_N,
    SECURITY_ANALYSIS_RESULT_NMK,
    SHORT_CIRCUIT_ALL_BUSES,
    DYNAMIC_SIMULATION,
    SENSITIVITY_AT_NODE_N,
    SENSITIVITY_AT_NODE_NMK,
    SENSITIVITY_IN_DELTA_A_N,
    SENSITIVITY_IN_DELTA_MW_N,
    SENSITIVITY_IN_DELTA_A_NMK,
    SENSITIVITY_IN_DELTA_MW_NMK,
    DYNAMIC_SECURITY_ANALYSIS,
    SHORT_CIRCUIT_ONE_BUS,
    STATE_ESTIMATION,
    VOLTAGE_INITIALIZATION,
    PCCMIN_RESULT
}

