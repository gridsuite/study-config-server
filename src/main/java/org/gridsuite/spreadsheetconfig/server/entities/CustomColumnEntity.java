/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * @author Achour BERRAHMA <achour.berrahma at rte-france.com>
 */
@Entity
@Table(name = "spreadsheet_custom_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(name = "formula", columnDefinition = "varchar(255)")
    private String formula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spreadsheet_config_id", nullable = false, foreignKey = @ForeignKey(name = "spreadsheet_config_id_fk_constraint"))
    private SpreadsheetConfigEntity spreadsheetConfig;
}
