/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import org.gridsuite.studyconfig.server.constants.ComputationSubType;

import java.util.*;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "computation_result_filter")
public class ComputationResultFilterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "computation_result_filter_id", foreignKey = @ForeignKey(name = "fk_columns_filters_computation_result"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "computation_sub_type")
    @Builder.Default
    private Map<ComputationSubType, ColumnsFiltersEntity> columnsFilters = new EnumMap<>(ComputationSubType.class);

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "computation_result_global_filter",
            joinColumns = @JoinColumn(
                    name = "computation_result_filter_id",
                    foreignKey = @ForeignKey(name = "fk_computation_result_filter_global_filter_computation_result_filter")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "global_filter_id",
                    foreignKey = @ForeignKey(name = "fk_computation_result_filter_global_filter_global_filter")
            )
    )
    @Builder.Default
    private List<GlobalFilterEntity> globalFilters = new ArrayList<>();
}
