package org.pbarreiro.barapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleComandaDTO {
    private Long id;

    @NotNull(message = "La comanda es obligatoria")
    private UUID comandaId;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    
    private ProductoDTO producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private BigDecimal precioUnitario;
}
