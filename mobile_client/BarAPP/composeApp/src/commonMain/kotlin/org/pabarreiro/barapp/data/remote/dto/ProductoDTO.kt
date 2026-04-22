package org.pabarreiro.barapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDTO(
    val id: Long? = null,
    val nombre: String,
    val precio: Double,
    val stock: Int? = null,
    val categoriaId: Long? = null,
    val imagenUrl: String? = null,
    val activo: Boolean? = true
)
