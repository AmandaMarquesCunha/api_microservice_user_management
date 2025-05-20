package com.br.api_microservice_user_management.service.users;

import com.br.api_microservice_user_management.exception.EmailAlreadyUsedException;
import com.br.api_microservice_user_management.exception.UserNotFoundException;
import com.br.api_microservice_user_management.service.dto.UserDTO;
import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.mapper.UserMapper;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.utils.UserRole;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário, validando unicidade do e-mail e criptografando a senha.
     */
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("E-mail já está em uso.");
        }
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encryptedPassword);
        if (userDTO.getRole() == null) {
            userDTO.setRole(UserRole.USER);
        }
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        logger.info("Usuário criado com id {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    /**
     * Lista todos os usuários paginados.
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDTO);
    }

    /**
     * Busca usuário por id.
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        return userMapper.toDTO(user);
    }

    /**
     * Busca usuário por e-mail.
     */
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        return userMapper.toDTO(user);
    }

    /**
     * Atualiza dados do usuário, validando e-mail e senha.
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("E-mail já está em uso.");
        }
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setRole(userDTO.getRole());
        User updatedUser = userRepository.save(user);
        logger.info("Usuário atualizado com id {}", updatedUser.getId());
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Remove usuário por id.
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        userRepository.delete(user);
        logger.info("Usuário removido com id {}", id);
    }

    /**
     * Atualiza o papel do usuário.
     */
    public UserDTO updateUserRole(Long id, UserRole newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        logger.info("Papel do usuário atualizado para {} no id {}", newRole, id);
        return userMapper.toDTO(updatedUser);
    }
}
