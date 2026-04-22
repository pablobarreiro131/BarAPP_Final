package org.pabarreiro.barapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mesas")
data class MesaEntity(
    @PrimaryKey val id: Long,
    val numeroMesa: Int,
    val capacidad: Int,
    val estado: String
)
