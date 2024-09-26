/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.constants;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
public enum SheetType {
    SUBSTATIONS,
    VOLTAGE_LEVELS,
    LINES,
    TWO_WINDINGS_TRANSFORMERS,
    THREE_WINDINGS_TRANSFORMERS,
    GENERATORS,
    LOADS,
    SHUNT_COMPENSATORS,
    STATIC_VAR_COMPENSATORS,
    BATTERIES,
    HVDC_LINES,
    LCC_CONVERTER_STATIONS,
    VSC_CONVERTER_STATIONS,
    DANGLING_LINES,
    BUSES,
    TIE_LINES
}
