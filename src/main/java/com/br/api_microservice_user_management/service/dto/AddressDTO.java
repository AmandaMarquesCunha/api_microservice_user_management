package com.br.api_microservice_user_management.service.dto;

import com.br.api_microservice_user_management.utils.AddressType;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

    private Long id;

    @NotBlank(message = "Logradouro é obrigatório.")
    private String street;

    @NotBlank(message = "Número é obrigatório.")
    private String number;

    private String complement;

    @NotBlank(message = "Bairro é obrigatório.")
    private String neighborhood;

    @NotBlank(message = "Cidade é obrigatória.")
    private String city;

    @NotBlank(message = "Estado é obrigatório.")
    private String state;

    @NotBlank(message = "CEP é obrigatório.")
    private String zipCode;

    @NotNull(message = "Tipo de endereço é obrigatório.")
    private AddressType type;

    private Date createdAt;
    private Date updatedAt;

    private UserDTO user;
}
