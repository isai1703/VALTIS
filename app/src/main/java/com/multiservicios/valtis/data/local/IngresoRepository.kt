package com.multiservicios.valtis.data.local

import com.multiservicios.valtis.data.local.entities.IngresoEntity
import kotlinx.coroutines.flow.Flow

class IngresoRepository(
    private val dao: ValtisDao
) {

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
}
