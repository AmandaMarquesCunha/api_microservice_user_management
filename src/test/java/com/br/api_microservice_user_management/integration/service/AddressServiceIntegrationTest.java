package com.br.api_microservice_user_management.integration.service;

import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.repository.AddressRepository;
import com.br.api_microservice_user_management.service.address.AddressService;
import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.service.feign.ViaCepService;
import com.br.api_microservice_user_management.service.dto.ViaCepDTO;
import com.br.api_microservice_user_management.utils.AddressType;
import com.br.api_microservice_user_management.utils.UserRole;
import com.br.api_microservice_user_management.exception.AddressNotFoundException;
import com.br.api_microservice_user_management.exception.AddressAccessDeniedException;
import com.br.api_microservice_user_management.exception.InvalidCepException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
class AddressServiceIntegrationTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @MockBean
    private ViaCepService viaCepService;

    private User user;
    private User admin;
    private AddressDTO addressDTO;
    private ViaCepDTO viaCepDTO;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();
        user = new User();
        user.setName("Usuário Teste");
        user.setEmail("user@exemplo.com");
        user.setPassword("senha123");
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        admin = new User();
        admin.setName("Admin Teste");
        admin.setEmail("admin@exemplo.com");
        admin.setPassword("senha123");
        admin.setRole(UserRole.ADMIN);
        admin = userRepository.save(admin);

        addressDTO = new AddressDTO();
        addressDTO.setZipCode("01001000");
        addressDTO.setNumber("123");
        addressDTO.setComplement("Apto 1");
        addressDTO.setType(AddressType.RESIDENTIAL);

        viaCepDTO = new ViaCepDTO();
        viaCepDTO.setCep("01001-000");
        viaCepDTO.setLogradouro("Praça da Sé");
        viaCepDTO.setBairro("Sé");
        viaCepDTO.setLocalidade("São Paulo");
        viaCepDTO.setUf("SP");
        Mockito.when(viaCepService.searchAddress(anyString())).thenReturn(viaCepDTO);
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testCreate() {
        AddressDTO created = addressService.create(addressDTO, user.getId());
        assertNotNull(created.getId(), "O endereço criado deve ter um ID");
        assertEquals("Praça da Sé", created.getStreet(), "O logradouro deve ser preenchido pelo ViaCep");
        assertEquals("São Paulo", created.getCity(), "A cidade deve ser preenchida pelo ViaCep");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testGetAll() {
        addressService.create(addressDTO, user.getId());
        Page<AddressDTO> page = addressService.getAll(PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements(), "Deve retornar 1 endereço cadastrado");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testGetById_User() {
        AddressDTO created = addressService.create(addressDTO, user.getId());
        AddressDTO found = addressService.getById(created.getId());
        assertEquals(created.getId(), found.getId(), "O endereço retornado deve ter o mesmo ID");
    }

    @Test
    @WithMockUser(username = "admin@exemplo.com", roles = {"ADMIN"})
    void testGetById_Admin() {
        AddressDTO created = addressService.create(addressDTO, user.getId());
        AddressDTO found = addressService.getById(created.getId());
        assertEquals(created.getId(), found.getId(), "O admin deve conseguir buscar qualquer endereço");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testUpdate() {
        AddressDTO created = addressService.create(addressDTO, user.getId());
        AddressDTO updateDTO = new AddressDTO();
        updateDTO.setZipCode("01001000");
        updateDTO.setNumber("999");
        updateDTO.setComplement("Casa");
        updateDTO.setType(AddressType.COMMERCIAL);
        AddressDTO updated = addressService.update(created.getId(), updateDTO);
        assertEquals("999", updated.getNumber(), "O número deve ser atualizado");
        assertEquals("Casa", updated.getComplement(), "O complemento deve ser atualizado");
        assertEquals(AddressType.COMMERCIAL.name(), updated.getType().name(), "O tipo deve ser atualizado");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testDelete() {
        AddressDTO created = addressService.create(addressDTO, user.getId());
        addressService.delete(created.getId());
        assertFalse(addressRepository.findById(created.getId()).isPresent(), "O endereço deve ser removido do banco");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testGetById_NotFound() {
        assertThrows(AddressAccessDeniedException.class, () -> addressService.getById(999L), "Deve lançar exceção se o endereço não existir para o usuário");
    }

    @Test
    @WithMockUser(username = "admin@exemplo.com", roles = {"ADMIN"})
    void testGetById_NotFound_Admin() {
        assertThrows(AddressNotFoundException.class, () -> addressService.getById(999L), "Deve lançar exceção se o endereço não existir para o admin");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testUpdate_InvalidCep() {
        AddressDTO created = addressService.create(addressDTO, user.getId());
        Mockito.when(viaCepService.searchAddress(anyString())).thenReturn(null);
        AddressDTO updateDTO = new AddressDTO();
        updateDTO.setZipCode("00000000");
        updateDTO.setNumber("999");
        updateDTO.setComplement("Casa");
        updateDTO.setType(AddressType.COMMERCIAL);
        assertThrows(InvalidCepException.class, () -> addressService.update(created.getId(), updateDTO), "Deve lançar exceção se o CEP for inválido");
    }

    @Test
    @WithMockUser(username = "user@exemplo.com", roles = {"USER"})
    void testDelete_NotFound() {
        assertThrows(AddressAccessDeniedException.class, () -> addressService.delete(999L), "Deve lançar exceção ao tentar deletar endereço inexistente");
    }
}

