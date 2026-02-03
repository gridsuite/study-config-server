/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "computation_type_filters")
public class ComputationTypeFiltersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "computation_type", nullable = false)
    private String computationType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "computation_type_filters_id", foreignKey = @ForeignKey(name = "fk_computation_type_global_filters"))
    @Builder.Default
    private List<GlobalFilterEntity> globalFilters = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "computation_sub_type_filters_id", foreignKey = @ForeignKey(name = "fk_computation_sub_type_column_filters"))
    @Builder.Default
    private List<ComputationSubTypeFiltersEntity> computationSubTypes = new ArrayList<>();
}
