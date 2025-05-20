package com.br.api_microservice_user_management.unit.service;

import com.br.api_microservice_user_management.entity.Address;
import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.exception.AddressAccessDeniedException;
import com.br.api_microservice_user_management.exception.AddressNotFoundException;
import com.br.api_microservice_user_management.exception.InvalidCepException;
import com.br.api_microservice_user_management.mapper.AddressMapper;
import com.br.api_microservice_user_management.repository.AddressRepository;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.service.address.AddressService;
import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.service.dto.ViaCepDTO;
import com.br.api_microservice_user_management.service.feign.ViaCepService;
import com.br.api_microservice_user_management.utils.UserRole;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private ViaCepService viaCepService;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private AddressService addressService;

    private AutoCloseable mocks;
    private User user;
    private Address address;
    private AddressDTO addressDTO;
    private ViaCepDTO viaCepDTO;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("user@email.com").role(UserRole.USER).build();
        address = Address.builder().id(1L).user(user).zipCode("12345678").build();
        addressDTO = AddressDTO.builder().id(1L).zipCode("12345678").build();
        viaCepDTO = ViaCepDTO.builder().cep("12345678").logradouro("Rua Teste").bairro("Bairro").localidade("Cidade").uf("UF").build();
        // Mock contexto de autenticação
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        SecurityContextHolder.clearContext();
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(viaCepService.searchAddress(anyString())).thenReturn(viaCepDTO);
        when(addressMapper.toEntity(any(AddressDTO.class))).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDTO(any(Address.class))).thenReturn(addressDTO);
        AddressDTO result = addressService.create(addressDTO, 1L);
        assertNotNull(result);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void create_invalidCep() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(viaCepService.searchAddress(anyString())).thenReturn(null);
        assertThrows(InvalidCepException.class, () -> addressService.create(addressDTO, 1L));
    }

    @Test
    void create_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AddressAccessDeniedException.class, () -> addressService.create(addressDTO, 1L));
    }

    @Test
    void getAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> page = new PageImpl<>(Collections.singletonList(address));
        when(addressRepository.findAll(pageable)).thenReturn(page);
        when(addressMapper.toDTO(any(Address.class))).thenReturn(addressDTO);
        Page<AddressDTO> result = addressService.getAll(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getById_success_user() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(addressMapper.toDTO(address)).thenReturn(addressDTO);
        AddressDTO result = addressService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void getById_success_admin() {
        user.setRole(UserRole.ADMIN);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDTO(address)).thenReturn(addressDTO);
        AddressDTO result = addressService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void getById_notFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());
        assertThrows(AddressAccessDeniedException.class, () -> addressService.getById(1L));
    }

    @Test
    void update_success_user() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(viaCepService.searchAddress(anyString())).thenReturn(viaCepDTO);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDTO(any(Address.class))).thenReturn(addressDTO);
        AddressDTO result = addressService.update(1L, addressDTO);
        assertNotNull(result);
    }

    @Test
    void update_success_admin() {
        user.setRole(UserRole.ADMIN);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(viaCepService.searchAddress(anyString())).thenReturn(viaCepDTO);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDTO(any(Address.class))).thenReturn(addressDTO);
        AddressDTO result = addressService.update(1L, addressDTO);
        assertNotNull(result);
    }

    @Test
    void update_invalidCep() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(viaCepService.searchAddress(anyString())).thenReturn(null);
        assertThrows(InvalidCepException.class, () -> addressService.update(1L, addressDTO));
    }

    @Test
    void delete_success_user() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address);
        assertDoesNotThrow(() -> addressService.delete(1L));
        verify(addressRepository).delete(address);
    }

    @Test
    void delete_success_admin() {
        user.setRole(UserRole.ADMIN);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address);
        assertDoesNotThrow(() -> addressService.delete(1L));
        verify(addressRepository).delete(address);
    }

    @Test
    void delete_notFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());
        assertThrows(AddressAccessDeniedException.class, () -> addressService.delete(1L));
    }
}

