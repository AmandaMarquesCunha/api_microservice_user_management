package com.br.api_microservice_user_management.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class LoggedUserLogger {


    private static final Logger log = LoggerFactory.getLogger(LoggedUserLogger.class);

    public static void logAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                log.info("Usuário autenticado: {}", userDetails.getUsername());
            } else {
                log.info("Principal: {}", principal.toString());
            }
        } else {
            log.warn("Nenhum usuário autenticado.");
        }
    }
}
