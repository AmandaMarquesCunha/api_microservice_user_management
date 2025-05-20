package com.br.api_microservice_user_management.unit.controller;

import com.br.api_microservice_user_management.controller.UserController;
import com.br.api_microservice_user_management.service.dto.UserDTO;
import com.br.api_microservice_user_management.service.users.UserService;
import com.br.api_microservice_user_management.utils.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private Page<UserDTO> userPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .role(UserRole.USER)
                .build();
        userPage = new PageImpl<>(Collections.singletonList(userDTO));
    }

    @Test
    void createUser_success() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);
        ResponseEntity<UserDTO> response = userController.createUser(userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void getAllUsers_success() {
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(0, 10, "name", "asc");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getUserById_success() {
        when(userService.getUserById(1L)).thenReturn(userDTO);
        ResponseEntity<UserDTO> response = userController.getUserById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updateUser_success() {
        UserDTO updatedDTO = UserDTO.builder()
                .id(1L)
                .name("Updated Name")
                .email("updated@email.com")
                .role(UserRole.ADMIN)
                .build();
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(updatedDTO);
        ResponseEntity<UserDTO> response = userController.updateUser(1L, updatedDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedDTO, response.getBody());
    }

    @Test
    void changeUserRole_success() {
        UserDTO updatedDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .role(UserRole.ADMIN)
                .build();
        when(userService.updateUserRole(1L, UserRole.ADMIN)).thenReturn(updatedDTO);
        ResponseEntity<UserDTO> response = userController.changeUserRole(1L, UserRole.ADMIN);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedDTO, response.getBody());
    }

    @Test
    void deleteUser_success() {
        doNothing().when(userService).deleteUser(1L);
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService).deleteUser(1L);
    }
}

