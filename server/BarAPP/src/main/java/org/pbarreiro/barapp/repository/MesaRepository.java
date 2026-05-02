package org.pbarreiro.barapp.repository;

import org.pbarreiro.barapp.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByNumeroMesa(Integer numeroMesa);
    java.util.List<Mesa> findAllByOrderByNumeroMesaAsc();
}
