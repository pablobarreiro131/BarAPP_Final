package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.PerfilDTO;
import org.pbarreiro.barapp.exception.ResourceNotFoundException;
import org.pbarreiro.barapp.model.Perfil;
import org.pbarreiro.barapp.repository.PerfilRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public List<PerfilDTO> findAll() {
        return perfilRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PerfilDTO findById(UUID id) {
        return perfilRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));
    }

    public PerfilDTO save(PerfilDTO dto) {
        Perfil perfil = new Perfil(dto.getId(), dto.getNombre(), dto.getRol(), dto.getFechaCreacion());
        return mapToDTO(perfilRepository.save(perfil));
    }

    private PerfilDTO mapToDTO(Perfil perfil) {
        return PerfilDTO.builder()
                .id(perfil.getId())
                .nombre(perfil.getNombre())
                .rol(perfil.getRol())
                .fechaCreacion(perfil.getFechaCreacion())
                .build();
    }
}
