package org.pabarreiro.barapp.data.local

import androidx.room.*
import org.pabarreiro.barapp.data.local.dao.BarDao
import org.pabarreiro.barapp.data.local.entity.*

@Database(
    entities = [
        MesaEntity::class,
        ProductoEntity::class,
        CategoriaEntity::class,
        ComandaEntity::class,
        DetalleComandaEntity::class
    ],
    version = 1
)
abstract class BarDatabase : RoomDatabase() {
    abstract fun barDao(): BarDao
}
