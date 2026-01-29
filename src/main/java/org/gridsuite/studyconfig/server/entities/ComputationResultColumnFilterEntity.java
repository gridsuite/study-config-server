/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Entity
@Table(name = "computation_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ComputationResultColumnFilterEntity extends AbstractColumnFilter {

    @Column(name = "computation_column_id", nullable = false, columnDefinition = "varchar(255)")
    private String computationColumnId;
}
