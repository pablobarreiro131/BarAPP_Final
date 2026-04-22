package org.pabarreiro.barapp.domain.model

data class Perfil(
    val id: String, // UUID from Supabase
    val nombre: String,
    val rol: String
)
