package com.br.api_microservice_user_management.unit.service;

import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.exception.EmailAlreadyUsedException;
import com.br.api_microservice_user_management.exception.UserNotFoundException;
import com.br.api_microservice_user_management.mapper.UserMapper;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.service.dto.UserDTO;
import com.br.api_microservice_user_management.service.users.UserService;
import com.br.api_microservice_user_management.utils.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .password("encodedPass")
                .role(UserRole.USER)
                .build();
        userDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .password("plainPass")
                .role(UserRole.USER)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void createUser_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);
        assertNotNull(result);
        assertEquals(userDTO.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_emailAlreadyUsed() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(EmailAlreadyUsedException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void getAllUsers_success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);
        Page<UserDTO> result = userService.getAllUsers(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        UserDTO result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(userDTO.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_success() {
        UserDTO updateDTO = UserDTO.builder()
                .id(1L)
                .name("Updated Name")
                .email("updated@email.com")
                .password("newPass")
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(updateDTO);
        UserDTO result = userService.updateUser(1L, updateDTO);
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
    }

    @Test
    void updateUser_emailAlreadyUsed() {
        User anotherUser = User.builder().id(2L).email("other@email.com").build();
        UserDTO updateDTO = UserDTO.builder()
                .id(1L)
                .name("Updated Name")
                .email("other@email.com")
                .password("newPass")
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(anotherUser));
        assertThrows(EmailAlreadyUsedException.class, () -> userService.updateUser(1L, updateDTO));
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void updateUserRole_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);
        UserDTO result = userService.updateUserRole(1L, UserRole.ADMIN);
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserRole_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUserRole(1L, UserRole.ADMIN));
    }
}

