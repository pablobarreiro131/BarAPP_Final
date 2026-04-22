package org.pabarreiro.barapp.domain.model

data class DetalleComanda(
    val id: Long? = null,
    val comandaId: String,
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val nombreProducto: String? = null
)
