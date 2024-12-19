/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.MapParamInfos;
import org.gridsuite.studyconfig.server.dto.NetworkAreaDiagramParamInfos;
import org.gridsuite.studyconfig.server.dto.NetworkVisualizationParamInfos;
import org.gridsuite.studyconfig.server.dto.SingleLineDiagramParamInfos;
import org.gridsuite.studyconfig.server.repositories.NetworkVisualizationParamRepository;
import org.gridsuite.studyconfig.server.service.NetworkVisualizationsParamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NetworkVisualizationParamsIntegrationTest {

    private static final String URI_NETWORK_VISUALIZATION_PARAM_BASE = "/v1/network-visualizations-params";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private NetworkVisualizationsParamService networkVisualizationsParamService;

    @Autowired
    private NetworkVisualizationParamRepository networkVisualizationParamRepository;

    @AfterEach
    public void tearDown() {
        networkVisualizationParamRepository.deleteAll();
    }

    @Test
    void testCreateParams() throws Exception {
        NetworkVisualizationParamInfos paramsToCreate = createDto();
        UUID paramsUuid = postCreateParams(paramsToCreate);
        // read new object, and compare
        NetworkVisualizationParamInfos createdParams = getParams(paramsUuid);
        assertThat(createdParams)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(paramsToCreate);
        assertThat(createdParams.id()).isNotNull();
    }

    @Test
    void testReadParams() throws Exception {
        NetworkVisualizationParamInfos paramsToRead = createDto();
        UUID paramsUuid = saveAndReturnId(paramsToRead);
        // read new object, and compare
        NetworkVisualizationParamInfos readParams = getParams(paramsUuid);
        assertThat(readParams)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(paramsToRead);
        assertThat(readParams.id()).isEqualTo(paramsUuid);
    }

    @Test
    void testUpdateParams() throws Exception {
        NetworkVisualizationParamInfos paramsToUpdate = createDto();
        UUID paramsUuid = saveAndReturnId(paramsToUpdate);

        NetworkVisualizationParamInfos updatedCollection = createDtoForUpdate(paramsUuid);
        String updatedCollectionJson = mapper.writeValueAsString(updatedCollection);
        mockMvc.perform(put(URI_NETWORK_VISUALIZATION_PARAM_BASE + "/" + paramsUuid)
                        .content(updatedCollectionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        NetworkVisualizationParamInfos retrievedParams = getParams(paramsUuid);
        assertThat(retrievedParams)
                .usingRecursiveComparison()
                .isEqualTo(updatedCollection);
    }

    @Test
    void testDeleteParams() throws Exception {
        NetworkVisualizationParamInfos paramsToDelete = createDto();
        UUID paramsUuid = saveAndReturnId(paramsToDelete);
        assertThat(networkVisualizationParamRepository.existsById(paramsUuid)).isTrue();

        mockMvc.perform(delete(URI_NETWORK_VISUALIZATION_PARAM_BASE + "/" + paramsUuid))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(URI_NETWORK_VISUALIZATION_PARAM_BASE + "/" + paramsUuid))
                .andExpect(status().isNotFound());
        assertThat(networkVisualizationParamRepository.existsById(paramsUuid)).isFalse();
    }

    @Test
    void testDuplicateParams() throws Exception {
        NetworkVisualizationParamInfos paramsToCreate = createDto();
        UUID paramsUuid = postCreateParams(paramsToCreate);

        MvcResult mvcPostResult = mockMvc.perform(post(URI_NETWORK_VISUALIZATION_PARAM_BASE + "/duplicate")
                        .queryParam("duplicateFrom", paramsUuid.toString()))
                .andExpect(status().isCreated())
                .andReturn();
        UUID duplicatedParamsUuid = mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);

        NetworkVisualizationParamInfos duplicatedParams = getParams(duplicatedParamsUuid);
        assertThat(duplicatedParams)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(paramsToCreate);
        assertThat(duplicatedParams.id()).isNotEqualTo(paramsUuid);

        assertThat(networkVisualizationParamRepository.existsById(paramsUuid)).isTrue();
        assertThat(networkVisualizationParamRepository.existsById(duplicatedParams.id())).isTrue();
    }

    private NetworkVisualizationParamInfos getParams(UUID paramsUuid) throws Exception {
        MvcResult mvcGetResult = mockMvc.perform(get(URI_NETWORK_VISUALIZATION_PARAM_BASE + "/" + paramsUuid))
                .andExpect(status().isOk())
                .andReturn();

        return mapper.readValue(
                mvcGetResult.getResponse().getContentAsString(),
                NetworkVisualizationParamInfos.class);
    }

    private UUID postCreateParams(NetworkVisualizationParamInfos paramsToCreate) throws Exception {
        String collectionToCreateJson = mapper.writeValueAsString(paramsToCreate);
        MvcResult mvcPostResult = mockMvc.perform(post(URI_NETWORK_VISUALIZATION_PARAM_BASE)
                        .content(collectionToCreateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        return mapper.readValue(mvcPostResult.getResponse().getContentAsString(), UUID.class);
    }

    private NetworkVisualizationParamInfos createDto() {
        return new NetworkVisualizationParamInfos(null,
                new MapParamInfos(true, false, "flow", "color", 80, true, "base"),
                new SingleLineDiagramParamInfos(false, false, "layout", "lib"),
                new NetworkAreaDiagramParamInfos(true));
    }

    private NetworkVisualizationParamInfos createDtoForUpdate(UUID id) {
        return new NetworkVisualizationParamInfos(id,
                new MapParamInfos(false, true, "flow2", "color2", 99, false, "base2"),
                new SingleLineDiagramParamInfos(true, true, "layout2", "lib2"),
                new NetworkAreaDiagramParamInfos(false));
    }

    private UUID saveAndReturnId(NetworkVisualizationParamInfos dto) {
        return networkVisualizationsParamService.createParameters(dto);
    }
}
