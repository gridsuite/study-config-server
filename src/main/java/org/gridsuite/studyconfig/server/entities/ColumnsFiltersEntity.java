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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Entity
@Table(name = "columns_filters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnsFiltersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "filter_tab")
    private ComputationSubType computationSubType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "column_filters_id", foreignKey = @ForeignKey(name = "fk_column_filters"))
    @OrderColumn(name = "column_order")
    @Builder.Default
    private List<ColumnEntity> columns = new ArrayList<>();
}
