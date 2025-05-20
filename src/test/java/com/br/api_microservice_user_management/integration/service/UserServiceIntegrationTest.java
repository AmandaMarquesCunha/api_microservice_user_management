package com.br.api_microservice_user_management.integration.service;

import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.service.dto.UserDTO;
import com.br.api_microservice_user_management.service.users.UserService;
import com.br.api_microservice_user_management.utils.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDTO createSampleUserDTO(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Usuário Teste");
        userDTO.setEmail(email);
        userDTO.setPassword("senha123");
        userDTO.setRole(UserRole.USER);
        return userDTO;
    }

    @Test
    void testCreateUser_Success() {
        UserDTO userDTO = createSampleUserDTO("teste@exemplo.com");
        UserDTO created = userService.createUser(userDTO);
        assertNotNull(created.getId());
        assertEquals("teste@exemplo.com", created.getEmail());
        assertEquals(UserRole.USER, created.getRole());
        assertNotEquals("senha123", created.getPassword()); // senha deve estar criptografada
    }

    @Test
    void testCreateUser_EmailAlreadyUsed() {
        UserDTO userDTO = createSampleUserDTO("duplicado@exemplo.com");
        userService.createUser(userDTO);
        UserDTO userDTO2 = createSampleUserDTO("duplicado@exemplo.com");
        assertThrows(Exception.class, () -> userService.createUser(userDTO2));
    }

    @Test
    void testGetAllUsers() {
        userRepository.deleteAll(); // Garante que o banco está limpo
        userService.createUser(createSampleUserDTO("a@a.com"));
        userService.createUser(createSampleUserDTO("b@b.com"));
        Page<UserDTO> page = userService.getAllUsers(PageRequest.of(0, 10));
        assertEquals(2, page.getTotalElements(), "O número de usuários retornados deve ser 2");
    }

    @Test
    void testGetUserById_Success() {
        UserDTO userDTO = userService.createUser(createSampleUserDTO("id@exemplo.com"));
        UserDTO found = userService.getUserById(userDTO.getId());
        assertEquals(userDTO.getEmail(), found.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        assertThrows(Exception.class, () -> userService.getUserById(999L));
    }

    @Test
    void testUpdateUser_Success() {
        UserDTO userDTO = userService.createUser(createSampleUserDTO("update@exemplo.com"));
        userDTO.setName("Novo Nome");
        userDTO.setPassword("novaSenha");
        UserDTO updated = userService.updateUser(userDTO.getId(), userDTO);
        assertEquals("Novo Nome", updated.getName());
        assertNotEquals("novaSenha", updated.getPassword());
    }

    @Test
    void testUpdateUser_EmailAlreadyUsed() {
        userService.createUser(createSampleUserDTO("um@exemplo.com"));
        UserDTO user2 = userService.createUser(createSampleUserDTO("dois@exemplo.com"));
        user2.setEmail("um@exemplo.com");
        assertThrows(Exception.class, () -> userService.updateUser(user2.getId(), user2));
    }

    @Test
    void testDeleteUser_Success() {
        UserDTO userDTO = userService.createUser(createSampleUserDTO("del@exemplo.com"));
        userService.deleteUser(userDTO.getId());
        assertEquals(0, userRepository.count());
    }

    @Test
    void testDeleteUser_NotFound() {
        assertThrows(Exception.class, () -> userService.deleteUser(12345L));
    }

    @Test
    void testUpdateUserRole_Success() {
        UserDTO userDTO = userService.createUser(createSampleUserDTO("role@exemplo.com"));
        UserDTO updated = userService.updateUserRole(userDTO.getId(), UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, updated.getRole());
    }

    @Test
    void testUpdateUserRole_NotFound() {
        assertThrows(Exception.class, () -> userService.updateUserRole(99999L, UserRole.ADMIN));
    }
}

