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
    ONE_BUS,
    ALL_BUS,
    PCCMIN_RESULT,
    SECURITY_ANALYSIS_RESULT_N,
    SECURITY_ANALYSIS_RESULT_N_K,
    SENSITIVITY_AT_NODE_N,
    SENSITIVITY_AT_NODE_N_K,
    SENSITIVITY_IN_DELTA_A_N,
    SENSITIVITY_IN_DELTA_A_N_K,
    SENSITIVITY_IN_DELTA_MW_N,
    SENSITIVITY_IN_DELTA_MW_N_K,
    STATEESTIMATION_QUALITY_CRITERION,
    STATEESTIMATION_QUALITY_PER_REGION,
    VOLTAGE_INITIALIZATION,
}

