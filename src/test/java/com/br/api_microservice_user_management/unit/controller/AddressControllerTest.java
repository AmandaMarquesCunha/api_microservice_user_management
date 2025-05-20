package com.br.api_microservice_user_management.unit.controller;

import com.br.api_microservice_user_management.controller.AddressController;
import com.br.api_microservice_user_management.service.address.AddressService;
import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.service.dto.ViaCepDTO;
import com.br.api_microservice_user_management.service.feign.ViaCepService;
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

class AddressControllerTest {
    @Mock
    private AddressService addressService;
    @Mock
    private ViaCepService viaCepService;
    @InjectMocks
    private AddressController addressController;

    private AddressDTO addressDTO;
    private Page<AddressDTO> addressPage;
    private ViaCepDTO viaCepDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressDTO = AddressDTO.builder()
                .id(1L)
                .zipCode("12345678")
                .street("Rua Teste")
                .city("Cidade")
                .state("UF")
                .build();
        addressPage = new PageImpl<>(Collections.singletonList(addressDTO));
        viaCepDTO = ViaCepDTO.builder()
                .cep("12345678")
                .logradouro("Rua Teste")
                .complemento("Comp")
                .bairro("Bairro")
                .localidade("Cidade")
                .uf("UF")
                .build();
    }

    @Test
    void create_success() {
        when(addressService.create(any(AddressDTO.class), anyLong())).thenReturn(addressDTO);
        ResponseEntity<AddressDTO> response = addressController.create(1L, addressDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(addressDTO, response.getBody());
    }

    @Test
    void getAll_success() {
        when(addressService.getAll(any(Pageable.class))).thenReturn(addressPage);
        ResponseEntity<Page<AddressDTO>> response = addressController.getAll(0, 10, "street", "asc");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getById_success() {
        when(addressService.getById(1L)).thenReturn(addressDTO);
        ResponseEntity<AddressDTO> response = addressController.getById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(addressDTO, response.getBody());
    }

    @Test
    void update_success() {
        AddressDTO updatedDTO = AddressDTO.builder()
                .id(1L)
                .zipCode("87654321")
                .street("Rua Nova")
                .city("Nova Cidade")
                .state("NU")
                .build();
        when(addressService.update(eq(1L), any(AddressDTO.class))).thenReturn(updatedDTO);
        ResponseEntity<AddressDTO> response = addressController.update(1L, updatedDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedDTO, response.getBody());
    }

    @Test
    void delete_success() {
        doNothing().when(addressService).delete(1L);
        ResponseEntity<Void> response = addressController.delete(1L);
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(addressService).delete(1L);
    }

    @Test
    void getAddressByCep_success() {
        when(viaCepService.searchAddress("12345678")).thenReturn(viaCepDTO);
        AddressDTO result = addressController.getAddressByCep("12345678");
        assertNotNull(result);
        assertEquals("12345678", result.getZipCode());
        assertEquals("Rua Teste", result.getStreet());
        assertEquals("Cidade", result.getCity());
        assertEquals("UF", result.getState());
    }
}

