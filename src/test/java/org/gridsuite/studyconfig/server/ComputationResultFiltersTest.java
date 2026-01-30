/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.studyconfig.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gridsuite.studyconfig.server.dto.*;
import org.gridsuite.studyconfig.server.repositories.ComputationResultFiltersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rehili Ghazwa <ghazwa.rehili at rte-france.com>
 */
@SpringBootTest
@AutoConfigureMockMvc
class ComputationResultFiltersTest {

    private static final String BASE_URI = "/v1/computation-result-filters";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ComputationResultFiltersRepository computationResultFiltersRepository;

    @AfterEach
    void tearDown() {
        computationResultFiltersRepository.deleteAll();
    }

    private List<GlobalFilterInfos> createGlobalFilters() {
        return List.of(GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Filter 1").recent(false).build(),
                GlobalFilterInfos.builder().id(UUID.randomUUID()).filterType("country").label("Filter 2").recent(false).build());
    }

    private ComputationResultColumnFilterInfos createColumnFilter() {
        return new ComputationResultColumnFilterInfos("subjectId", new ColumnFilterInfos("number", "greaterThan", "10", 0.5));
    }

    @Test
    void testCreateDefaultComputationResultFilters() throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URI + "/default"))
                .andExpect(status().isCreated())
                .andReturn();
        UUID rootId = mapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        assertThat(rootId).isNotNull();
        assertThat(computationResultFiltersRepository.findById(rootId)).isPresent();
    }

    @Test
    void testGetComputationResultFilters() throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URI + "/default")).andReturn();
        UUID rootId = mapper.readValue(result.getResponse().getContentAsString(), UUID.class);
        mockMvc.perform(post(BASE_URI + "/" + rootId + "/" + "LoadFlow" + "/global-filters")
                        .content(mapper.writeValueAsString(createGlobalFilters()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(BASE_URI + "/" + rootId + "/" + "LoadFlow" + "/" + "limitViolation" + "/columns")
                        .content(mapper.writeValueAsString(createColumnFilter()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        result = mockMvc.perform(get(BASE_URI + "/" + rootId + "/LoadFlow/limitViolation")).andExpect(status().isOk()).andReturn();
        List<ComputationResultColumnFilterInfos> infos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() { });
        assertThat(infos).hasSize(1);
        ComputationResultColumnFilterInfos info = infos.get(0);
        assertThat(info.id()).isEqualTo("subjectId");
        assertThat(info.columnFilterInfos().filterValue()).isEqualTo("10");
        assertThat(info.columnFilterInfos().filterType()).isEqualTo("greaterThan");
        assertThat(info.columnFilterInfos().filterDataType()).isEqualTo("number");

    }
}
