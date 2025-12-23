/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import org.gridsuite.studyconfig.server.constants.ColumnType;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Entity
@Table(name = "spreadsheet_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpreadsheetColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ColumnType type;

    @Column(columnDefinition = "integer")
    private Integer precision;

    @Column(columnDefinition = "CLOB")
    private String formula;

    @Column(columnDefinition = "CLOB")
    private String dependencies;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "filter_id")
    private ColumnFilterEntity columnFilter;

    @Column(nullable = false)
    @Builder.Default
    private boolean visible = true;

    public void resetColumn() {
        if (columnFilter != null) {
            columnFilter.resetColumnFilter();
        }
    }

    public SpreadsheetColumnEntity copy() {
        return SpreadsheetColumnEntity.builder()
                .name(getName())
                .type(getType())
                .precision(getPrecision())
                .formula(getFormula())
                .dependencies(getDependencies())
                .columnFilter(getColumnFilter() != null ? getColumnFilter().copy() : null)
                .build();
    }
}
