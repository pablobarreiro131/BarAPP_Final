package org.pbarreiro.barapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.ComandaDTO;
import org.pbarreiro.barapp.dto.DetalleComandaDTO;
import org.pbarreiro.barapp.exception.ResourceNotFoundException;
import org.pbarreiro.barapp.model.Comanda;
import org.pbarreiro.barapp.model.DetalleComanda;
import org.pbarreiro.barapp.model.Mesa;
import org.pbarreiro.barapp.model.Perfil;
import org.pbarreiro.barapp.model.Producto;
import org.pbarreiro.barapp.repository.ComandaRepository;
import org.pbarreiro.barapp.repository.DetalleComandaRepository;
import org.pbarreiro.barapp.repository.MesaRepository;
import org.pbarreiro.barapp.repository.PerfilRepository;
import org.pbarreiro.barapp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final DetalleComandaRepository detalleComandaRepository;
    private final MesaRepository mesaRepository;
    private final PerfilRepository perfilRepository;
    private final ProductoRepository productoRepository;
    
    private final MesaService mesaService;
    private final PerfilService perfilService;
    private final ProductoService productoService;

    public List<ComandaDTO> findAll() {
        return comandaRepository.findAll().stream()
                .map(this::mapToDTOBuilder)
                .collect(Collectors.toList());
    }

    public ComandaDTO findById(UUID id) {
        return comandaRepository.findById(id)
                .map(this::mapToDTOBuilder)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda no encontrada con ID: " + id));
    }

    public List<ComandaDTO> findByMesa(Long mesaId) {
        return comandaRepository.findByMesaId(mesaId).stream()
                .map(this::mapToDTOBuilder)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComandaDTO create(ComandaDTO dto) {
        Mesa mesa = mesaRepository.findById(dto.getMesaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada: " + dto.getMesaId()));
        
        Perfil camarero = perfilRepository.findById(dto.getCamareroId())
                .orElseThrow(() -> new ResourceNotFoundException("Camarero no encontrado: " + dto.getCamareroId()));

        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setCamarero(camarero);
        comanda.setEstadoPago(false);
        comanda.setTotal(BigDecimal.ZERO);
        
        Comanda savedComanda = comandaRepository.save(comanda);
        
        mesa.setEstado("ocupada");
        mesaRepository.save(mesa);
        
        return mapToDTOBuilder(savedComanda);
    }

    @Transactional
    public DetalleComandaDTO addDetalle(UUID comandaId, DetalleComandaDTO detalleDTO) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda no encontrada: " + comandaId));
                
        if (comanda.getFechaCierre() != null) {
            throw new IllegalStateException("La comanda ya está cerrada");
        }

        Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + detalleDTO.getProductoId()));

        DetalleComanda detalle = new DetalleComanda();
        detalle.setComanda(comanda);
        detalle.setProducto(producto);
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecioUnitario(producto.getPrecio()); 

        DetalleComanda savedDetalle = detalleComandaRepository.save(detalle);
        
        actualizarTotalComanda(comanda);
        
        return mapDetalleToDTO(savedDetalle);
    }

    @Transactional
    public ComandaDTO payAndClose(UUID id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda no encontrada con ID: " + id));
        
        comanda.setEstadoPago(true);
        comanda.setFechaCierre(OffsetDateTime.now());
        Comanda savedComanda = comandaRepository.save(comanda);
        
        Mesa mesa = comanda.getMesa();
        if (comandaRepository.findByMesaIdAndFechaCierreIsNull(mesa.getId()).isEmpty()) {
            mesa.setEstado("libre");
            mesaRepository.save(mesa);
        }
        
        return mapToDTOBuilder(savedComanda);
    }

    private void actualizarTotalComanda(Comanda comanda) {
        List<DetalleComanda> detalles = detalleComandaRepository.findByComandaId(comanda.getId());
        BigDecimal total = detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(new BigDecimal(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        comanda.setTotal(total);
        comandaRepository.save(comanda);
    }

    private ComandaDTO mapToDTOBuilder(Comanda comanda) {
        ComandaDTO dto = ComandaDTO.builder()
                .id(comanda.getId())
                .mesaId(comanda.getMesa().getId())
                .mesa(mesaService.mapToDTO(comanda.getMesa()))
                .camareroId(comanda.getCamarero().getId())
                .camarero(perfilService.findById(comanda.getCamarero().getId()))
                .fechaApertura(comanda.getFechaApertura())
                .fechaCierre(comanda.getFechaCierre())
                .estadoPago(comanda.getEstadoPago())
                .total(comanda.getTotal())
                .build();
                
        List<DetalleComandaDTO> detalles = detalleComandaRepository.findByComandaId(comanda.getId())
                .stream()
                .map(this::mapDetalleToDTO)
                .collect(Collectors.toList());
                
        dto.setDetalles(detalles);
        return dto;
    }
    
    private DetalleComandaDTO mapDetalleToDTO(DetalleComanda detalle) {
        return DetalleComandaDTO.builder()
                .id(detalle.getId())
                .comandaId(detalle.getComanda().getId())
                .productoId(detalle.getProducto().getId())
                .producto(productoService.mapToDTO(detalle.getProducto()))
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .build();
    }
}
