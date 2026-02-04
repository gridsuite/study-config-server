/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities.computationresult;

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
@Table(name = "computation_result_filters")
public class FiltersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "computation_result_filters_id", foreignKey = @ForeignKey(name = "fk_computation_result_filters_type"))
    @Builder.Default
    private List<FilterTypeEntity> computationResultFilter = new ArrayList<>();
}
