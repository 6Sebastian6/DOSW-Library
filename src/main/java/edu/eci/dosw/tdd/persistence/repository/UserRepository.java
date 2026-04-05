package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    // Buscar por username, Esto se necesita para el login con JWT
    Optional<UserEntity> findByUsername(String username);

    // Buscar por nombre, Esto para un endpoin de busqueda
    List<UserEntity> findByNameContainingIgnoreCase(String name);

    // Verificar si ya existe un username, Por si hay duplicados
    boolean existsByUsername(String username);
}