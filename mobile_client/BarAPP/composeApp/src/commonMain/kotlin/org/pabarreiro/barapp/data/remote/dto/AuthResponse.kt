package org.pabarreiro.barapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PerfilDTO(
    val id: String,
    val nombre: String,
    val rol: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val perfil: PerfilDTO
)
