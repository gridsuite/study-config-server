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

import java.util.EnumMap;
import java.util.Map;
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
@Table(name = "computation_result_filters")
public class ComputationResultFiltersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "computation_result_filters_id", foreignKey = @ForeignKey(name = "fk_computation_result_filters"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "computation_type")
    @Builder.Default
    private Map<ComputationType, ComputationResultFilterEntity> computationResultFilter = new EnumMap<>(ComputationType.class);

}
