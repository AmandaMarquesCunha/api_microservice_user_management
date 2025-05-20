package com.br.api_microservice_user_management.integration.controller;

import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.utils.AddressType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WithMockUser(username = "user@exemplo.com", roles = {"USER"})
@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        addressDTO = AddressDTO.builder()
                .zipCode("01001000")
                .number("123")
                .complement("Apto 1")
                .type(AddressType.RESIDENTIAL)
                .build();
    }

    @Test
    void testCreateAddress() throws Exception {
        mockMvc.perform(post("/addresses/v1/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testGetAllAddresses() throws Exception {
        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetAddressById_NotFound() throws Exception {
        mockMvc.perform(get("/addresses/v1/99999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetAddressByCep() throws Exception {
        mockMvc.perform(get("/addresses/v1/cep/01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zipCode", is("01001-000")));
    }
}

