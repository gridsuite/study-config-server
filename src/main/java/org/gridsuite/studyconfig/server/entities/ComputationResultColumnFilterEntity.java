/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@Entity
@Table(name = "computation_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComputationResultColumnFilterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "columnId", nullable = false, columnDefinition = "varchar(255)")
    private String id;

    @Column(name = "filter_data_type", columnDefinition = "varchar(255)")
    private String filterDataType;

    @Column(name = "filter_type", columnDefinition = "varchar(255)")
    private String filterType;

    @Column(name = "filter_value", columnDefinition = "CLOB")
    private String filterValue;

    @Column(name = "filter_tolerance")
    private Double filterTolerance;
}
