package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    // Son los prestamos activos de un usuario, Para ver que sean maximo 3
    long countByUserIdAndStatus(String userId, Status status);

    // Préstamos activos de un usuario
    List<LoanEntity> findByUserIdAndStatus(String userId, Status status);

    // Todos los préstamos de un usuario (activos + devueltos)
    List<LoanEntity> findByUserId(String userId);

    // Todos los préstamos de un libro
    List<LoanEntity> findByBookId(String bookId);
}
