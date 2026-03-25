package org.pbarreiro.barapp.repository;

import org.pbarreiro.barapp.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, UUID> {
    List<Comanda> findByMesaId(Long mesaId);
    List<Comanda> findByMesaIdAndFechaCierreIsNull(Long mesaId);
}
