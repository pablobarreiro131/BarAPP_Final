package org.pabarreiro.barapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val categoriaId: Long?,
    val imagenUrl: String?,
    val activo: Boolean
)
