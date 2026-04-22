package org.pabarreiro.barapp.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<BarDatabase> {
    val dbFilePath = NSHomeDirectory() + "/barapp.db"
    return Room.databaseBuilder<BarDatabase>(
        name = dbFilePath,
        factory = { BarDatabase::class.instantiateImpl() }
    )
}
