package com.br.api_microservice_user_management.service.feign;


import com.br.api_microservice_user_management.service.dto.ViaCepDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "via-cep", url = "${app.users.addresses.url.viacep}")
public interface ViaCepService {

    @GetMapping( path = "/ws/{zip-code}/json/", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE )
    ViaCepDTO searchAddress(@PathVariable("zip-code") String zip );
}