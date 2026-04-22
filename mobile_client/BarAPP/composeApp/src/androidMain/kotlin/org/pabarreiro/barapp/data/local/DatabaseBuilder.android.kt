package org.pabarreiro.barapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseBuilder(private val ctx: Context) {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<BarDatabase> {
        val appContext = ctx.applicationContext
        val dbFile = appContext.getDatabasePath("barapp.db")
        return Room.databaseBuilder<BarDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
