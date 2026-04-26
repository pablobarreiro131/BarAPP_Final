package org.pbarreiro.barapp.service;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.DashboardResumenDTO;
import org.pbarreiro.barapp.dto.ProductoVentaDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardResumenDTO getResumen() {
        String sqlVentas = "SELECT COALESCE(SUM(total), 0) FROM comandas " +
                           "WHERE fecha_cierre >= CURRENT_DATE AND estado_pago = true";
        BigDecimal ventasHoy = jdbcTemplate.queryForObject(sqlVentas, BigDecimal.class);

        String sqlMesas = "SELECT COUNT(*) FROM mesas WHERE estado = 'ocupada'";
        Long mesasOcupadas = jdbcTemplate.queryForObject(sqlMesas, Long.class);

        String sqlStock = "SELECT COUNT(*) FROM productos WHERE stock < 10 AND activo = true";
        Long productosBajoStock = jdbcTemplate.queryForObject(sqlStock, Long.class);

        List<ProductoVentaDTO> topProductos = getVentasPorProducto();

        return DashboardResumenDTO.builder()
                .ventasHoy(ventasHoy)
                .mesasOcupadas(mesasOcupadas)
                .productosBajoStock(productosBajoStock)
                .topProductos(topProductos)
                .build();
    }

    public List<ProductoVentaDTO> getVentasPorProducto() {
        String sql = "SELECT p.nombre, SUM(dc.cantidad) as total_vendido, SUM(dc.cantidad * dc.precio_unitario) as recaudacion " +
                     "FROM detalles_comanda dc " +
                     "JOIN productos p ON dc.producto_id = p.id " +
                     "JOIN comandas c ON dc.comanda_id = c.id " +
                     "WHERE c.estado_pago = true " +
                     "GROUP BY p.nombre " +
                     "ORDER BY total_vendido DESC " +
                     "LIMIT 5";
                     
        return jdbcTemplate.query(sql, (rs, rowNum) -> ProductoVentaDTO.builder()
                .nombre(rs.getString("nombre"))
                .totalVendido(rs.getLong("total_vendido"))
                .recaudacion(rs.getBigDecimal("recaudacion"))
                .build());
    }
}
