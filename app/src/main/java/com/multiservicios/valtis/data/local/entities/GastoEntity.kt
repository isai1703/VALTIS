package com.multiservicios.valtis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monto: Double,
    val concepto: String,
    val fecha: Long,
    val categoria: String = "General"
)
