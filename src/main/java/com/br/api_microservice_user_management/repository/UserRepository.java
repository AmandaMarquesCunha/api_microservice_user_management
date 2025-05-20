package com.br.api_microservice_user_management.repository;

import com.br.api_microservice_user_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca um usuário pelo e-mail.
     * @param email e-mail do usuário
     * @return usuário, se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuários cujo nome contenha o termo informado (ignorando maiúsculas/minúsculas).
     * @param name termo do nome
     * @return lista de usuários
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Busca usuários por data de criação (exemplo de uso de Date).
     * @param createdAt data de criação
     * @return lista de usuários
     */
    List<User> findByCreatedAt(Date createdAt);
}
