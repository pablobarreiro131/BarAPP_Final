package org.pabarreiro.barapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoriaDTO(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String?
)
