package org.pbarreiro.barapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResumenDTO {
    private BigDecimal ventasHoy;
    private Long mesasOcupadas;
    private Long productosBajoStock;
    private List<ProductoVentaDTO> topProductos;
}
