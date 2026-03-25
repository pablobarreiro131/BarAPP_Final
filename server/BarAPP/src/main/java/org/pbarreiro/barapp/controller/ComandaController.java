package org.pbarreiro.barapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.ComandaDTO;
import org.pbarreiro.barapp.dto.DetalleComandaDTO;
import org.pbarreiro.barapp.service.ComandaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comandas")
@RequiredArgsConstructor
public class ComandaController {

    private final ComandaService comandaService;

    @GetMapping
    public ResponseEntity<List<ComandaDTO>> getAllComandas() {
        return ResponseEntity.ok(comandaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComandaDTO> getComandaById(@PathVariable UUID id) {
        return ResponseEntity.ok(comandaService.findById(id));
    }

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<ComandaDTO>> getComandasByMesa(@PathVariable Long mesaId) {
        return ResponseEntity.ok(comandaService.findByMesa(mesaId));
    }

    @PostMapping
    public ResponseEntity<ComandaDTO> createComanda(@Valid @RequestBody ComandaDTO dto) {
        return new ResponseEntity<>(comandaService.create(dto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<DetalleComandaDTO> addDetalle(@PathVariable UUID id, @Valid @RequestBody DetalleComandaDTO dto) {
        return new ResponseEntity<>(comandaService.addDetalle(id, dto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<ComandaDTO> payAndClose(@PathVariable UUID id) {
        return ResponseEntity.ok(comandaService.payAndClose(id));
    }
}
