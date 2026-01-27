/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Entity
@Table(name = "computation_result_column_filter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ComputationResultColumnFilterEntity extends AbstractColumnFilterAssignmentEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "computation_sub_type_filters_id", nullable = false, foreignKey = @ForeignKey(name = "fk_computation_column"))
    private ComputationSubTypeFiltersEntity computationSubType;
}
