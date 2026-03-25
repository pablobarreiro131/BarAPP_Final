package org.pbarreiro.barapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesaDTO {
    private Long id;

    @NotNull(message = "El número de mesa es obligatorio")
    private Integer numeroMesa;
    
    private Integer capacidad;
    private String estado;
}
