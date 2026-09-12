package com.multiservicios.valtis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.multiservicios.valtis.data.local.entities.IngresoEntity

@Database(
    entities = [
        IngresoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ValtisDatabase : RoomDatabase() {

    abstract fun valtisDao(): ValtisDao
}
