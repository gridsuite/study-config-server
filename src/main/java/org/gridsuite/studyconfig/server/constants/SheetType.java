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
    SUBSTATION,
    VOLTAGE_LEVEL,
    LINE,
    TWO_WINDINGS_TRANSFORMER,
    THREE_WINDINGS_TRANSFORMER,
    GENERATOR,
    LOAD,
    SHUNT_COMPENSATOR,
    STATIC_VAR_COMPENSATOR,
    BATTERY,
    HVDC_LINE,
    LCC_CONVERTER_STATION,
    VSC_CONVERTER_STATION,
    DANGLING_LINE,
    BUS,
    TIE_LINE,
    BUSBAR_SECTION
}

