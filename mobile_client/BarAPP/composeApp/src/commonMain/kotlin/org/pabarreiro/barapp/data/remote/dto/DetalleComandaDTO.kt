package org.pabarreiro.barapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetalleComandaDTO(
    val id: Long? = null,
    val comandaId: String? = null,
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double
)
