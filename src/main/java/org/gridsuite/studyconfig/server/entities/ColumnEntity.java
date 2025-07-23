/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import java.util.UUID;

import org.gridsuite.studyconfig.server.constants.ColumnType;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Entity
@Table(name = "spreadsheet_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ColumnType type;

    @Column(name = "precision", columnDefinition = "integer")
    private Integer precision;

    @Column(name = "formula", columnDefinition = "CLOB")
    private String formula;

    @Column(name = "dependencies", columnDefinition = "CLOB")
    private String dependencies;

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

    @Column(name = "visible", nullable = false)
    @Builder.Default
    private boolean visible = true;
}
