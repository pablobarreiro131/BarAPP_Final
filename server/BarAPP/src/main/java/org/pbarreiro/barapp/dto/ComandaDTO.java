package org.pbarreiro.barapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComandaDTO {
    private UUID id;

    @NotNull(message = "La mesa es obligatoria")
    private Long mesaId;
    
    private MesaDTO mesa;

    @NotNull(message = "El camarero es obligatorio")
    private UUID camareroId;
    
    private PerfilDTO camarero;

    private OffsetDateTime fechaApertura;
    private OffsetDateTime fechaCierre;
    private Boolean estadoPago;
    private BigDecimal total;
    
    private List<DetalleComandaDTO> detalles;
}
