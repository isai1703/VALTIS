package com.multiservicios.valtis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.multiservicios.valtis.data.local.entities.CompromisoEntity
import com.multiservicios.valtis.data.local.entities.GastoEntity
import com.multiservicios.valtis.data.local.entities.IngresoEntity

@Database(
    entities = [
        IngresoEntity::class,
        CompromisoEntity::class,
        GastoEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ValtisDatabase : RoomDatabase() {
    abstract fun valtisDao(): ValtisDao
}
