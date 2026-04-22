package org.pabarreiro.barapp.domain.model

data class Mesa(
    val id: Long,
    val numeroMesa: Int,
    val capacidad: Int,
    val estado: String // 'libre', 'ocupada', 'reservada'
)
