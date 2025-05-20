package com.br.api_microservice_user_management.security;

import com.br.api_microservice_user_management.utils.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserSecurity {

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private HttpServletRequest request;

    public boolean hasPermissionToEdit(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            return userDetails.getId().equals(userId) || userDetails.getRole() == UserRole.ADMIN;
        }
        return false;
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            return userDetails.getRole() == UserRole.ADMIN;
        }
        return false;
    }
}
