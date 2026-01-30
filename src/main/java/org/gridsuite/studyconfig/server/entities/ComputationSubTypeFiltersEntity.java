/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "computation_sub_type_filters", uniqueConstraints = @UniqueConstraint(columnNames = {"computation_type_filters_id", "computation_sub_type"}))
public class ComputationSubTypeFiltersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "computation_sub_type", nullable = false, unique = true)
    private String computationSubType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "computation_type_filters_id", nullable = false)
    private ComputationTypeFiltersEntity computationType;

    @OneToMany(mappedBy = "computationSubType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("columnOrder ASC")
    private List<ComputationResultColumnFilterEntity> columns = new ArrayList<>();
}
