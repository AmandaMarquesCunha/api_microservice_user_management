package com.br.api_microservice_user_management.integration.controller;

import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.service.dto.UserDTO;
import com.br.api_microservice_user_management.utils.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .name("Usuário Teste")
                .email("user@exemplo.com")
                .password("123456")
                .role(UserRole.USER)
                .build();
        user = userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "admin@exemplo.com", roles = {"ADMIN"})
    void testCreateUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("Novo Usuário")
                .email("novo@exemplo.com")
                .password("abcdef")
                .role(UserRole.USER)
                .build();
        mockMvc.perform(post("/users/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("novo@exemplo.com"));
    }

    @Test
    @WithMockUser(username = "admin@exemplo.com", roles = {"ADMIN"})
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users/v1/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/users/v1/list/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    @WithUserDetails("user@exemplo.com")
    void testUpdateUser() throws Exception {
        UserDTO updateDTO = UserDTO.builder()
                .name("Usuário Atualizado")
                .email("user@exemplo.com")
                .password("123456")
                .role(UserRole.USER)
                .build();
        try {
            mockMvc.perform(put("/users/v1/update/" + user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Usuário Atualizado"));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @WithMockUser(username = "admin@exemplo.com", roles = {"ADMIN"})
    void testChangeUserRole() throws Exception {
        mockMvc.perform(put("/users/v1/" + user.getId() + "/role")
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin@exemplo.com", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/v1/delete/" + user.getId()))
                .andExpect(status().isNoContent());
    }
}

