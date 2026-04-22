package org.pabarreiro.barapp.domain.model

import kotlinx.datetime.Instant

data class Comanda(
    val id: String,
    val mesaId: Long,
    val camareroId: String,
    val fechaApertura: Instant,
    val fechaCierre: Instant?,
    val estadoPago: Boolean,
    val total: Double,
    val detalles: List<DetalleComanda> = emptyList()
)
