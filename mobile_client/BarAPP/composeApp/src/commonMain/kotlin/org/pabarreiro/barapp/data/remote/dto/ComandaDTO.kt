package org.pabarreiro.barapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ComandaDTO(
    val id: String? = null,
    val mesaId: Long,
    val camareroId: String,
    val fechaApertura: String? = null,
    val fechaCierre: String? = null,
    val estadoPago: Boolean? = false,
    val total: Double? = 0.0,
    val detalles: List<DetalleComandaDTO>? = emptyList()
)
