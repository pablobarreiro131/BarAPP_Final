package org.pbarreiro.barapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "comandas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camarero_id")
    private Perfil camarero;

    @CreationTimestamp
    @Column(name = "fecha_apertura", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now())")
    private OffsetDateTime fechaApertura;

    @Column(name = "fecha_cierre", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime fechaCierre;

    @Column(name = "estado_pago", columnDefinition = "boolean default false")
    private Boolean estadoPago;

    @Column(precision = 10, scale = 2, columnDefinition = "numeric default 0.00")
    private BigDecimal total;
}
