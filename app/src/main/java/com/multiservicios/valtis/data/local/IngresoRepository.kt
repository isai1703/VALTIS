package com.multiservicios.valtis.data.local

import com.multiservicios.valtis.data.local.entities.CompromisoEntity
import com.multiservicios.valtis.data.local.entities.DeudaEntity
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import kotlinx.coroutines.flow.Flow

class IngresoRepository(
    private val dao: ValtisDao
) {

    // INGRESOS

    fun observarIngresos(): Flow<List<IngresoEntity>> =
        dao.observarIngresos()

    fun observarTotalIngresos(): Flow<Double> =
        dao.observarTotalIngresos()

    suspend fun insertarIngreso(ingreso: IngresoEntity) {
        dao.insertarIngreso(ingreso)
    }

    suspend fun eliminarIngreso(ingreso: IngresoEntity) {
        dao.eliminarIngreso(ingreso)
    }


    // COMPROMISOS

    suspend fun obtenerCompromisos(): List<CompromisoEntity> =
        dao.obtenerCompromisos()

    suspend fun actualizarApartado(
        id: Long,
        apartado: Double
    ) {
        dao.actualizarApartado(
            id = id,
            apartado = apartado
        )
    }


    // DEUDAS

    suspend fun obtenerDeudasActivas(): List<DeudaEntity> =
        dao.obtenerDeudasActivas()

    suspend fun actualizarEstadoDeuda(
        id: Long,
        pagosRestantes: Int,
        fechaProximoPago: Long,
        apartado: Double,
        faltanteAnterior: Double,
        activa: Boolean
    ) {
        dao.actualizarEstadoDeuda(
            id = id,
            pagosRestantes = pagosRestantes,
            fechaProximoPago = fechaProximoPago,
            apartado = apartado,
            faltanteAnterior = faltanteAnterior,
            activa = activa
        )
    }
}
