package com.multiservicios.valtis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingresos")
data class IngresoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val monto: Double,

    val concepto: String,

    val fecha: Long,

    val fuente: String = "Nómina"
)
