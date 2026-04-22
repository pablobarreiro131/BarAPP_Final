package org.pabarreiro.barapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val descripcion: String?
)
