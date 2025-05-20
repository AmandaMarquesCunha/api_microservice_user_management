package com.br.api_microservice_user_management.mapper;

import com.br.api_microservice_user_management.entity.Address;
import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.mapper.UserMapper;
import com.br.api_microservice_user_management.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Converte AddressDTO em Address (entidade).
     */
    public Address toEntity(AddressDTO dto) {
        return Address.builder()
                .id(dto.getId())
                .street(dto.getStreet())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .neighborhood(dto.getNeighborhood())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .type(dto.getType())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    /**
     * Converte Address (entidade) em AddressDTO.
     */
    public AddressDTO toDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .type(address.getType())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .user(address.getUser() != null ? userMapper.toDTO(address.getUser()) : null)
                .build();
    }
}
