/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import org.gridsuite.studyconfig.server.service.SingleLineDiagramService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Caroline Jeandat {@literal <caroline.jeandat at rte-france.com>}
 */
@RestClientTest(SingleLineDiagramService.class)
@ContextConfiguration(classes = { RestTemplateConfig.class, SingleLineDiagramService.class })
@TestPropertySource(properties = "gridsuite.services.single-line-diagram-server.base-uri=http://single-line-diagram-server/")
class SingleLineDiagramServiceTest {
    private static final String BASE_URI = "http://single-line-diagram-server/";

    @Autowired
    private SingleLineDiagramService singleLineDiagramService;

    @Autowired
    private MockRestServiceServer server;

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void createNadConfigShouldPostAndReturnId() {
        UUID id = UUID.randomUUID();
        Map<String, Object> nadConfigData = new HashMap<>();
        nadConfigData.put("name", "test-config");

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(BASE_URI + "v1/network-area-diagram/config"))
                .andRespond(withSuccess("\"" + id + "\"", MediaType.APPLICATION_JSON));

        UUID result = singleLineDiagramService.createOrUpdateNadConfig(nadConfigData);

        assertThat(result).isEqualTo(id);
    }

    @Test
    void updateNadConfigShouldPut() {
        UUID id = UUID.randomUUID();
        Map<String, Object> nadConfigData = new HashMap<>();
        nadConfigData.put("id", id.toString());
        nadConfigData.put("name", "updated-config");

        server.expect(method(HttpMethod.PUT))
                .andExpect(requestTo(BASE_URI + "v1/network-area-diagram/config/" + id))
                .andRespond(withSuccess());

        UUID result = singleLineDiagramService.createOrUpdateNadConfig(nadConfigData);

        assertThat(result).isEqualTo(id);
    }

    @Test
    void deleteNadConfigShouldDelete() {
        UUID id = UUID.randomUUID();

        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestTo(BASE_URI + "v1/network-area-diagram/config/" + id))
                .andRespond(withSuccess());

        singleLineDiagramService.deleteNadConfig(id);
    }

    @Test
    void deleteNadConfigsShouldDeleteAll() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(id1, id2);

        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestTo(BASE_URI + "v1/network-area-diagram/configs"))
                .andExpect(content().json("[\"" + id1 + "\",\"" + id2 + "\"]"))
                .andRespond(withSuccess());

        singleLineDiagramService.deleteNadConfigs(ids);
    }

    @Test
    void duplicateNadConfigShouldPostDuplicate() {
        UUID sourceId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(BASE_URI + "v1/network-area-diagram/config/" + sourceId + "/duplicate"))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("\"" + newId + "\"", MediaType.APPLICATION_JSON));

        UUID result = singleLineDiagramService.duplicateNadConfig(sourceId);

        assertThat(result).isEqualTo(newId);
    }
}
