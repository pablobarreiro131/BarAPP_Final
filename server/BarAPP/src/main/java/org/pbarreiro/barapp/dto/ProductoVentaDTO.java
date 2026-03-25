package org.pbarreiro.barapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoVentaDTO {
    private String nombre;
    private Long totalVendido;
    private BigDecimal recaudacion;
}
