/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomColumnEmbeddable {

    @Column(name = "name", nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(name = "formula", columnDefinition = "CLOB")
    private String formula;

    @Column(name = "dependencies", columnDefinition = "CLOB")
    private String dependencies;

    @Column(name = "columnId", nullable = false, columnDefinition = "varchar(255)")
    private String id;
}
