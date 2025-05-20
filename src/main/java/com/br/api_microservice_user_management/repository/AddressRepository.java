package com.br.api_microservice_user_management.repository;

import com.br.api_microservice_user_management.entity.Address;
import com.br.api_microservice_user_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Busca todos os endereços de um usuário pelo ID do usuário.
     * @param userId ID do usuário
     * @return lista de endereços
     */
    List<Address> findByUserId(Long userId);

    /**
     * Busca um endereço pelo ID e pelo usuário.
     * @param id ID do endereço
     * @param user Usuário
     * @return endereço, se encontrado
     */
    Optional<Address> findByIdAndUser(Long id, User user);

    /**
     * Busca todos os endereços de um usuário de forma paginada.
     * @param user Usuário
     * @param pageable paginação
     * @return página de endereços
     */
    Page<Address> findAllByUser(User user, Pageable pageable);

    /**
     * Busca todos os endereços de um usuário pelo ID do usuário de forma paginada.
     * @param userId ID do usuário
     * @param pageable paginação
     * @return página de endereços
     */
    Page<Address> findAllByUser_Id(Long userId, Pageable pageable);
}

