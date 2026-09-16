package com.multiservicios.valtis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compromisos")
data class CompromisoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,

    val monto: Double,

    val fechaVencimiento: Long,

    val apartado: Double = 0.0
)
