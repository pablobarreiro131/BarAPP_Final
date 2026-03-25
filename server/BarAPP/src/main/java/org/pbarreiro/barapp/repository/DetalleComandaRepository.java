package org.pbarreiro.barapp.repository;

import org.pbarreiro.barapp.model.DetalleComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DetalleComandaRepository extends JpaRepository<DetalleComanda, Long> {
    List<DetalleComanda> findByComandaId(UUID comandaId);
}
