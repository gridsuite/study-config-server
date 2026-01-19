/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ayoub Labidi <ayoub.labidi at rte-france.com>
 */
@Service
public class SingleLineDiagramService {

    private static final String API_VERSION = "v1";
    private static final String NETWORK_AREA_DIAGRAM = "network-area-diagram";
    private static final String CONFIGS = "configs";
    private static final String CONFIG = "config";

    private final RestTemplate restTemplate;
    private String singleLineDiagramServerBaseUri;

    public SingleLineDiagramService(@Value("${gridsuite.services.single-line-diagram-server.base-uri:http://single-line-diagram-server/}") String singleLineDiagramServerBaseUri,
                                    RestTemplate restTemplate) {
        this.singleLineDiagramServerBaseUri = singleLineDiagramServerBaseUri;
        this.restTemplate = restTemplate;
    }

    public UUID createOrUpdateNadConfig(Map<String, Object> nadConfigData) {
        UUID id = nadConfigData.get("id") != null ? UUID.fromString(nadConfigData.get("id").toString()) : null;

        if (id != null) {
            String path = UriComponentsBuilder.newInstance()
                    .pathSegment(API_VERSION, NETWORK_AREA_DIAGRAM, CONFIG, id.toString())
                    .toUriString();
            restTemplate.put(singleLineDiagramServerBaseUri + path, nadConfigData);
            return id;
        } else {
            String path = UriComponentsBuilder.newInstance()
                    .pathSegment(API_VERSION, NETWORK_AREA_DIAGRAM, CONFIG)
                    .toUriString();
            return restTemplate.postForObject(singleLineDiagramServerBaseUri + path, nadConfigData, UUID.class);
        }
    }

    public void deleteNadConfig(UUID configUuid) {
        String path = UriComponentsBuilder.newInstance()
                .pathSegment(API_VERSION, NETWORK_AREA_DIAGRAM, CONFIG, configUuid.toString())
                .toUriString();
        restTemplate.delete(singleLineDiagramServerBaseUri + path);
    }

    public void deleteNadConfigs(List<UUID> configUuids) {
        if (configUuids == null || configUuids.isEmpty()) {
            return;
        }

        String path = UriComponentsBuilder.newInstance()
                .pathSegment(API_VERSION, NETWORK_AREA_DIAGRAM, CONFIGS)
                .toUriString();
        HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(configUuids);
        restTemplate.exchange(singleLineDiagramServerBaseUri + path, HttpMethod.DELETE, requestEntity, Void.class);
    }

    public UUID duplicateNadConfig(UUID sourceConfigUuid) {
        String path = UriComponentsBuilder.newInstance()
                .pathSegment(API_VERSION, NETWORK_AREA_DIAGRAM, CONFIGS)
                .queryParam("duplicateFrom", sourceConfigUuid)
                .toUriString();
        return restTemplate.postForObject(singleLineDiagramServerBaseUri + path, null, UUID.class);
    }
}
