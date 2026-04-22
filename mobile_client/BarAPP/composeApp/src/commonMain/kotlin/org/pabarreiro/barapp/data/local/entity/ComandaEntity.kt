package org.pabarreiro.barapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comandas")
data class ComandaEntity(
    @PrimaryKey val id: String,
    val mesaId: Long,
    val camareroId: String,
    val fechaAperturaMillis: Long,
    val fechaCierreMillis: Long?,
    val estadoPago: Boolean,
    val total: Double,
    val isSynced: Boolean = true
)
