package com.br.api_microservice_user_management.mapper;

import com.br.api_microservice_user_management.service.dto.UserDTO;
import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.utils.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Converte UserDTO em User (entidade).
     */
    public User toEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole() != null ? userDTO.getRole() : UserRole.USER)
                .build();
    }

    /**
     * Converte User (entidade) em UserDTO. Não expõe a senha.
     */
    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(null) // Nunca expor a senha
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
