package com.br.api_microservice_user_management.controller;

import com.br.api_microservice_user_management.service.address.AddressService;
import com.br.api_microservice_user_management.service.dto.AddressDTO;
import com.br.api_microservice_user_management.service.dto.ViaCepDTO;
import com.br.api_microservice_user_management.service.feign.ViaCepService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final ViaCepService viaCepService;

    @PostMapping("/v1/create/{userId}")
    public ResponseEntity<AddressDTO> create(@PathVariable Long userId, @Valid @RequestBody AddressDTO dto) {
        AddressDTO address = addressService.create(dto, userId);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/v1/list")
    public ResponseEntity<Page<AddressDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "street") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        Page<AddressDTO> addresses = addressService.getAll(pageable);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/v1/list/{id}")
    public ResponseEntity<AddressDTO> getById(@PathVariable Long id) {
        AddressDTO address = addressService.getById(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/v1/list-addresses/{userId}/")
    public ResponseEntity<List<AddressDTO>> getAllByUserId(@PathVariable Long userId) {
        List<AddressDTO> addresses = addressService.getAllByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/v1/update/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable Long id, @Valid @RequestBody AddressDTO dto) {
        AddressDTO address = addressService.update(id, dto);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/v1/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("v1/cep/{cep}")
    public AddressDTO getAddressByCep(@PathVariable("cep") String cep) {
        ViaCepDTO viaCepDTO = viaCepService.searchAddress(cep);

        return AddressDTO.builder()
                .zipCode(viaCepDTO.getCep())
                .street(viaCepDTO.getLogradouro())
                .complement(viaCepDTO.getComplemento())
                .neighborhood(viaCepDTO.getBairro())
                .city(viaCepDTO.getLocalidade())
                .state(viaCepDTO.getUf())
                .build();
    }
}
