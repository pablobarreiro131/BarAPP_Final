package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.PerfilDTO;
import org.pbarreiro.barapp.exception.ResourceNotFoundException;
import org.pbarreiro.barapp.model.Perfil;
import org.pbarreiro.barapp.repository.PerfilRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final SupabaseAuthService supabaseAuthService;

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
        Perfil perfil = Perfil.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .rol(dto.getRol())
                .fechaCreacion(dto.getFechaCreacion())
                .build();
        return mapToDTO(perfilRepository.save(perfil));
    }

    public PerfilDTO findCurrentPerfil() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String supabaseId = jwt.getSubject();
            return findById(UUID.fromString(supabaseId));
        }
        throw new ResourceNotFoundException("No se pudo identificar al usuario desde el token");
    }

    public PerfilDTO createWithAuth(String email, String password, String nombre, String rol) {
        String supabaseId = supabaseAuthService.createAdminUser(email, password, rol);

        Perfil perfil = new Perfil();
        perfil.setId(UUID.fromString(supabaseId));
        perfil.setNombre(nombre);
        perfil.setEmail(email);
        perfil.setRol(rol);

        return mapToDTO(perfilRepository.save(perfil));
    }

    public PerfilDTO update(UUID id, PerfilDTO dto) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado: " + id));

        perfil.setNombre(dto.getNombre());
        perfil.setRol(dto.getRol());

        return mapToDTO(perfilRepository.save(perfil));
    }

    public void delete(UUID id) {
        if (!perfilRepository.existsById(id)) {
            throw new ResourceNotFoundException("Perfil no encontrado: " + id);
        }

        try {
            supabaseAuthService.deleteUser(id.toString());
        } catch (Exception e) {

            System.err.println("Error borrando de Supabase: " + e.getMessage());
        }
        perfilRepository.deleteById(id);
    }

    private PerfilDTO mapToDTO(Perfil perfil) {
        return PerfilDTO.builder()
                .id(perfil.getId())
                .nombre(perfil.getNombre())
                .email(perfil.getEmail())
                .rol(perfil.getRol())
                .fechaCreacion(perfil.getFechaCreacion())
                .build();
    }
}
