/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.constants;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */

// Must be in-sync with EQUIPMENT_TYPES in spreadsheet front component
public enum SheetType {
    BATTERY,
    BRANCH,
    BUS,
    BUSBAR_SECTION,
    DANGLING_LINE,
    GENERATOR,
    HVDC_LINE,
    LCC_CONVERTER_STATION,
    LINE,
    LOAD,
    SHUNT_COMPENSATOR,
    STATIC_VAR_COMPENSATOR,
    SUBSTATION,
    THREE_WINDINGS_TRANSFORMER,
    TIE_LINE,
    TWO_WINDINGS_TRANSFORMER,
    VOLTAGE_LEVEL,
    VSC_CONVERTER_STATION
}

