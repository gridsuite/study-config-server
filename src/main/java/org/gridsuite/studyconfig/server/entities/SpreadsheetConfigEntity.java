/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import org.gridsuite.studyconfig.server.constants.SheetType;
import org.gridsuite.studyconfig.server.constants.SortDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "spreadsheet_config")
public class SpreadsheetConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sheet_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SheetType sheetType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "spreadsheet_config_id", foreignKey = @ForeignKey(name = "fk_spreadsheet_config_column"))
    @OrderColumn(name = "column_order")
    @Builder.Default
    private List<ColumnEntity> columns = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "spreadsheet_config_id", foreignKey = @ForeignKey(name = "fk_global_filter_spreadsheet_config"))
    @Builder.Default
    private List<GlobalFilterEntity> globalFilters = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "config_node_aliases", foreignKey = @ForeignKey(name = "fk_spreadsheet_config_node_aliases"))
    private List<String> nodeAliases;

    @Column(name = "sort_column_id")
    private String sortColumnId;

    @Column(name = "sort_direction")
    @Enumerated(EnumType.STRING)
    private SortDirection sortDirection;

    public void resetFilters() {
        this.globalFilters.clear();
        this.columns.forEach(column -> {
            if (column.getColumnFilter() != null) {
                column.getColumnFilter().resetFilter();
            }
        });
    }
}
