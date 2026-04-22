package org.pabarreiro.barapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val categoriaId: Long?,
    val imagenUrl: String?,
    val activo: Boolean
)
