package org.pbarreiro.barapp.controller;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.PerfilDTO;
import org.pbarreiro.barapp.service.PerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/perfiles")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping
    public ResponseEntity<List<PerfilDTO>> getAllPerfiles() {
        return ResponseEntity.ok(perfilService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilDTO> getPerfilById(@PathVariable UUID id) {
        return ResponseEntity.ok(perfilService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PerfilDTO> createPerfil(@RequestBody PerfilDTO dto) {
        return ResponseEntity.ok(perfilService.save(dto));
    }
}
