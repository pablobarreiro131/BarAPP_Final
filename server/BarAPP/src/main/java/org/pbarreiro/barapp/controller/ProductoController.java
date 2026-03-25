package org.pbarreiro.barapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.ProductoDTO;
import org.pbarreiro.barapp.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos(
            @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.findByCategoria(categoriaId));
        }
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@Valid @RequestBody ProductoDTO dto) {
        return new ResponseEntity<>(productoService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
