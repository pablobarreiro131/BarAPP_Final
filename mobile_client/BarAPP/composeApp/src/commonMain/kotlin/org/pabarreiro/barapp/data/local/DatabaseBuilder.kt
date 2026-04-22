package org.pabarreiro.barapp.data.local

import androidx.room.RoomDatabase

expect class DatabaseBuilder {
    fun getDatabaseBuilder(): RoomDatabase.Builder<BarDatabase>
}
