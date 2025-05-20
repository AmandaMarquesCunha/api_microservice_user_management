package com.br.api_microservice_user_management.service.address;

import com.br.api_microservice_user_management.entity.Address;
import com.br.api_microservice_user_management.entity.User;
import com.br.api_microservice_user_management.mapper.AddressMapper;
import com.br.api_microservice_user_management.repository.AddressRepository;
import com.br.api_microservice_user_management.repository.UserRepository;
import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.utils.UserRole;
import com.br.api_microservice_user_management.service.feign.ViaCepService;
import com.br.api_microservice_user_management.service.dto.ViaCepDTO;
import com.br.api_microservice_user_management.exception.AddressNotFoundException;
import com.br.api_microservice_user_management.exception.AddressAccessDeniedException;
import com.br.api_microservice_user_management.exception.InvalidCepException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final ViaCepService viaCepService;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email).orElseThrow(() -> new AddressAccessDeniedException("Usuário não encontrado"));
        }
        throw new AddressAccessDeniedException("Usuário não autenticado");
    }

    /**
     * Cria um novo endereço para o usuário informado, validando o CEP via ViaCep.
     */
    public AddressDTO create(AddressDTO dto, Long userId) {
        if (dto == null) {
            throw new IllegalArgumentException("Address data must not be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AddressAccessDeniedException("User with ID " + userId + " not found"));

        // Consome o serviço ViaCep para validar e preencher os dados do endereço
        ViaCepDTO viaCepDTO = viaCepService.searchAddress(dto.getZipCode());
        if (viaCepDTO == null || viaCepDTO.getCep() == null || viaCepDTO.getCep().isEmpty() || viaCepDTO.getErro() != null) {
            throw new InvalidCepException("CEP inválido ou não encontrado");
        }

        Address address = addressMapper.toEntity(dto);
        address.setStreet(viaCepDTO.getLogradouro());
        address.setNeighborhood(viaCepDTO.getBairro());
        address.setCity(viaCepDTO.getLocalidade());
        address.setState(viaCepDTO.getUf());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        logger.info("Endereço criado para usuário {} com id {}", userId, savedAddress.getId());

        return addressMapper.toDTO(savedAddress);
    }

    /**
     * Lista todos os endereços paginados.
     */
    public Page<AddressDTO> getAll(Pageable pageable) {
        return addressRepository.findAll(pageable).map(addressMapper::toDTO);
    }

    /**
     * Lista todos os endereços de um usuário pelo userId.
     */
    public List<AddressDTO> getAllByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream().map(addressMapper::toDTO).toList();
    }

    /**
     * Busca endereço por id, validando permissão do usuário.
     */
    public AddressDTO getById(Long id) {
        User currentUser = getAuthenticatedUser();

        Address address = currentUser.getRole() == UserRole.ADMIN
                ? addressRepository.findById(id).orElseThrow(() -> new AddressNotFoundException("Address not found"))
                : addressRepository.findByIdAndUser(id, currentUser).orElseThrow(() -> new AddressAccessDeniedException("Address not found or access denied"));

        return addressMapper.toDTO(address);
    }

    /**
     * Atualiza endereço, validando permissão e CEP.
     */
    public AddressDTO update(Long id, AddressDTO dto) {
        User currentUser = getAuthenticatedUser();

        Address address = currentUser.getRole() == UserRole.ADMIN
                ? addressRepository.findById(id).orElseThrow(() -> new AddressNotFoundException("Address not found"))
                : addressRepository.findByIdAndUser(id, currentUser).orElseThrow(() -> new AddressAccessDeniedException("Address not found or access denied"));

        // Consome o serviço ViaCep para validar e preencher os dados do endereço
        ViaCepDTO viaCepDTO = viaCepService.searchAddress(dto.getZipCode());
        if (viaCepDTO == null || viaCepDTO.getCep() == null || viaCepDTO.getCep().isEmpty() || viaCepDTO.getErro() != null) {
            throw new InvalidCepException("CEP inválido ou não encontrado");
        }

        address.setStreet(viaCepDTO.getLogradouro());
        address.setNeighborhood(viaCepDTO.getBairro());
        address.setCity(viaCepDTO.getLocalidade());
        address.setState(viaCepDTO.getUf());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setZipCode(dto.getZipCode());
        address.setType(dto.getType());

        logger.info("Endereço atualizado com id {}", id);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    /**
     * Remove endereço, validando permissão do usuário.
     */
    public void delete(Long id) {
        User currentUser = getAuthenticatedUser();

        Address address = currentUser.getRole() == UserRole.ADMIN
                ? addressRepository.findById(id).orElseThrow(() -> new AddressNotFoundException("Address not found"))
                : addressRepository.findByIdAndUser(id, currentUser).orElseThrow(() -> new AddressAccessDeniedException("Address not found or access denied"));

        addressRepository.delete(address);
        logger.info("Endereço removido com id {}", id);
    }
}

