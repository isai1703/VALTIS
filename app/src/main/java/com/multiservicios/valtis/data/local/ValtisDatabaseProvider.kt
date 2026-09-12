package com.multiservicios.valtis.data.local

import android.content.Context
import androidx.room.Room

object ValtisDatabaseProvider {

    @Volatile
    private var INSTANCE: ValtisDatabase? = null

    fun getDatabase(context: Context): ValtisDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ValtisDatabase::class.java,
                "valtis_database"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}
