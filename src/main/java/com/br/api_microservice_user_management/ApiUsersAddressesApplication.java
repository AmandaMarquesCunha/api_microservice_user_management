package com.br.api_microservice_user_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.br.api_microservice_user_management")
@EnableFeignClients(basePackages = "com.br.api_microservice_user_management.service.feign")
public class ApiUsersAddressesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiUsersAddressesApplication.class, args);
	}

}
