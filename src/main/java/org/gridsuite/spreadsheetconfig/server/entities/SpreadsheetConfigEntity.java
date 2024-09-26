/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.spreadsheetconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;
import org.gridsuite.spreadsheetconfig.server.constants.SheetType;

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

    @Column(name = "sheet_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SheetType sheetType;

    @OneToMany(mappedBy = "spreadsheetConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustomColumnEntity> customColumns = new ArrayList<>();

}
