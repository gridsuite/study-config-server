/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gridsuite.studyconfig.server.constants.ColumnType;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Entity
@Table(name = "spreadsheet_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SpreadsheetColumnFilterEntity extends AbstractColumnFilterAssignmentEntity {

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

    @Column(nullable = false)
    @Builder.Default
    private boolean visible = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spreadsheet_config_id", nullable = false, foreignKey = @ForeignKey(name = "fk_spreadsheet_column"))
    private SpreadsheetConfigEntity spreadsheetConfig;

    public void resetColumn() {
        if (filter != null) {
            filter.resetColumnFilter();
        }
    }

    public SpreadsheetColumnFilterEntity copy() {
        return SpreadsheetColumnFilterEntity.builder()
                .name(getName())
                .type(getType())
                .precision(getPrecision())
                .formula(getFormula())
                .dependencies(getDependencies())
                .visible(isVisible())
                .id(getId())
                .columnOrder(getColumnOrder())
                .filter(getFilter() != null ? getFilter().copy() : null)
                .build();
    }
}
