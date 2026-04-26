package org.pbarreiro.barapp.controller;

import lombok.RequiredArgsConstructor;
import org.pbarreiro.barapp.dto.DashboardResumenDTO;
import org.pbarreiro.barapp.dto.ProductoVentaDTO;
import org.pbarreiro.barapp.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen() {
        return ResponseEntity.ok(dashboardService.getResumen());
    }

    @GetMapping("/ventas-productos")
    public ResponseEntity<List<ProductoVentaDTO>> getVentasPorProducto() {
        return ResponseEntity.ok(dashboardService.getVentasPorProducto());
    }
}
