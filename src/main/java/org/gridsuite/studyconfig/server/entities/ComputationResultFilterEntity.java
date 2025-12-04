/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import org.gridsuite.studyconfig.server.constants.ComputationType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "computation_type")
    private ComputationType computationType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "computation_result_id", foreignKey = @ForeignKey(name = "fk_columns_filters_computation_result"))
    @OrderColumn(name = "column_order")
    @Builder.Default
    private List<ColumnsFiltersEntity> columnsFilters = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "computation_result_global_filter",
            joinColumns = @JoinColumn(name = "computation_result_id"),
            inverseJoinColumns = @JoinColumn(name = "global_filter_id")
    )
    @Builder.Default
    private List<GlobalFilterEntity> globalFilters = new ArrayList<>();
}
