package org.pabarreiro.barapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalles_comanda",
    foreignKeys = [
        ForeignKey(
            entity = ComandaEntity::class,
            parentColumns = ["id"],
            childColumns = ["comandaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DetalleComandaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val comandaId: String,
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val isSynced: Boolean = true
)
