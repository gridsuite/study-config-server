/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ayoub LABIDI <ayoub.labidi at rte-france.com>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "spreadsheet_config_collection")
public class SpreadsheetConfigCollectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "collection_id", foreignKey = @ForeignKey(name = "fk_spreadsheet_config_collection"))
    @OrderColumn(name = "config_order")
    @Builder.Default
    private List<SpreadsheetConfigEntity> spreadsheetConfigs = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "node_aliases", foreignKey = @ForeignKey(name = "fk_spreadsheet_config_collection_node_aliases"))
    private List<String> nodeAliases;
}
