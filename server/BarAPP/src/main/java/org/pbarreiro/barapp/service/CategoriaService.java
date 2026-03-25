package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.CategoriaDTO;
import org.pbarreiro.barapp.exception.ResourceNotFoundException;
import org.pbarreiro.barapp.model.Categoria;
import org.pbarreiro.barapp.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO findById(Long id) {
        return categoriaRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public CategoriaDTO save(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return mapToDTO(categoriaRepository.save(categoria));
    }

    public CategoriaDTO update(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return mapToDTO(categoriaRepository.save(categoria));
    }

    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    public CategoriaDTO mapToDTO(Categoria categoria) {
        if (categoria == null) return null;
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}
