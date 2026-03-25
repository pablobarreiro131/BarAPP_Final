package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.ProductoDTO;
import org.pbarreiro.barapp.exception.ResourceNotFoundException;
import org.pbarreiro.barapp.model.Categoria;
import org.pbarreiro.barapp.model.Producto;
import org.pbarreiro.barapp.repository.CategoriaRepository;
import org.pbarreiro.barapp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaService categoriaService;

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> findByCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO findById(Long id) {
        return productoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    public ProductoDTO save(ProductoDTO dto) {
        Producto producto = new Producto();
        return mapToDTO(productoRepository.save(updateEntityFromDTO(producto, dto)));
    }

    public ProductoDTO update(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapToDTO(productoRepository.save(updateEntityFromDTO(producto, dto)));
    }

    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    private Producto updateEntityFromDTO(Producto producto, ProductoDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock() != null ? dto.getStock() : 0);
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        
        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + dto.getCategoriaId()));
            producto.setCategoria(categoria);
        }
        
        return producto;
    }

    public ProductoDTO mapToDTO(Producto producto) {
        if (producto == null) return null;
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null)
                .categoria(categoriaService.mapToDTO(producto.getCategoria()))
                .imagenUrl(producto.getImagenUrl())
                .activo(producto.getActivo())
                .build();
    }
}
