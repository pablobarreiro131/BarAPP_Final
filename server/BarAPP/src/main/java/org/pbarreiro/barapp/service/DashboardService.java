package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.ProductoVentaDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public List<ProductoVentaDTO> getVentasPorProducto() {
        String sql = "SELECT p.nombre, SUM(dc.cantidad) as total_vendido, SUM(dc.cantidad * dc.precio_unitario) as recaudacion " +
                     "FROM detalles_comanda dc " +
                     "JOIN productos p ON dc.producto_id = p.id " +
                     "GROUP BY p.nombre";
                     
        return jdbcTemplate.query(sql, (rs, rowNum) -> ProductoVentaDTO.builder()
                .nombre(rs.getString("nombre"))
                .totalVendido(rs.getLong("total_vendido"))
                .recaudacion(rs.getBigDecimal("recaudacion"))
                .build());
    }
}
