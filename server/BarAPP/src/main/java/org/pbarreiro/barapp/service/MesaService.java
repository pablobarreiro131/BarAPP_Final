package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.MesaDTO;
import org.pbarreiro.barapp.exception.ResourceNotFoundException;
import org.pbarreiro.barapp.model.Mesa;
import org.pbarreiro.barapp.repository.MesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    public List<MesaDTO> findAll() {
        return mesaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MesaDTO findById(Long id) {
        return mesaRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
    }

    public MesaDTO findByNumeroMesa(Integer numeroMesa) {
        return mesaRepository.findByNumeroMesa(numeroMesa)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con número: " + numeroMesa));
    }

    public MesaDTO save(MesaDTO dto) {
        if (mesaRepository.findByNumeroMesa(dto.getNumeroMesa()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una mesa con el número: " + dto.getNumeroMesa());
        }
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(dto.getNumeroMesa());
        mesa.setCapacidad(dto.getCapacidad() != null ? dto.getCapacidad() : 4);
        mesa.setEstado(dto.getEstado() != null ? dto.getEstado() : "libre");
        return mapToDTO(mesaRepository.save(mesa));
    }

    public MesaDTO update(Long id, MesaDTO dto) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
        
        mesa.setNumeroMesa(dto.getNumeroMesa());
        if (dto.getCapacidad() != null) mesa.setCapacidad(dto.getCapacidad());
        if (dto.getEstado() != null) mesa.setEstado(dto.getEstado());
        
        return mapToDTO(mesaRepository.save(mesa));
    }
    
    public MesaDTO updateEstado(Long id, String estado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
        mesa.setEstado(estado);
        return mapToDTO(mesaRepository.save(mesa));
    }

    public void delete(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa no encontrada con ID: " + id);
        }
        mesaRepository.deleteById(id);
        renumberTables();
    }

    @org.springframework.transaction.annotation.Transactional
    public void renumberTables() {
        List<Mesa> mesas = mesaRepository.findAllByOrderByNumeroMesaAsc();
        for (int i = 0; i < mesas.size(); i++) {
            mesas.get(i).setNumeroMesa(i + 1);
        }
        mesaRepository.saveAll(mesas);
    }

    public MesaDTO mapToDTO(Mesa mesa) {
        if (mesa == null) return null;
        return MesaDTO.builder()
                .id(mesa.getId())
                .numeroMesa(mesa.getNumeroMesa())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado())
                .build();
    }
}
