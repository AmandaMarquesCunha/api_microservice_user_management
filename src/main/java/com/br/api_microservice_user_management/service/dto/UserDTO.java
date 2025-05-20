package com.br.api_microservice_user_management.service.dto;

import com.br.api_microservice_user_management.utils.UserRole;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    private String name;

    @Email(message = "E-mail deve ser válido.")
    @NotBlank(message = "E-mail é obrigatório.")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
    private String password;

    private UserRole role;

    private Date createdAt;
    private Date updatedAt;


}
