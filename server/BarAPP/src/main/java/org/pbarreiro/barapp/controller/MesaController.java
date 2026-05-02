package org.pbarreiro.barapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.MesaDTO;
import org.pbarreiro.barapp.service.MesaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public ResponseEntity<List<MesaDTO>> getAllMesas() {
        return ResponseEntity.ok(mesaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> getMesaById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MesaDTO> createMesa(@Valid @RequestBody MesaDTO dto) {
        return new ResponseEntity<>(mesaService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> updateMesa(@PathVariable Long id, @Valid @RequestBody MesaDTO dto) {
        return ResponseEntity.ok(mesaService.update(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MesaDTO> updateEstadoMesa(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mesaService.updateEstado(id, estado));
    }

    @PostMapping("/renumerar")
    public ResponseEntity<Void> renumberMesas() {
        mesaService.renumberTables();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
