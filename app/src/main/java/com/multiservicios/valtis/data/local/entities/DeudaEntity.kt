package com.multiservicios.valtis.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deudas")
data class DeudaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,

    val montoPago: Double,

    val pagosRestantes: Int,

    val periodicidad: String = "MENSUAL",

    val diaPago: Int,

    val fechaProximoPago: Long,

    val apartado: Double = 0.0,

    val faltanteAnterior: Double = 0.0,

    val activa: Boolean = true
)
