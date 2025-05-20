package com.br.api_microservice_user_management.service.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}