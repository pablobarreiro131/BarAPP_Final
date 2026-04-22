package org.pabarreiro.barapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MesaDTO(
    val id: Long? = null,
    val numeroMesa: Int,
    val capacidad: Int,
    val estado: String
)
