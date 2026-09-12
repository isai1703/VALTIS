package com.multiservicios.valtis.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ValtisDao {

    @Insert
    suspend fun insertarIngreso(ingreso: IngresoEntity)

    @Query("SELECT * FROM ingresos ORDER BY fecha DESC")
    fun observarIngresos(): Flow<List<IngresoEntity>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM ingresos")
    fun observarTotalIngresos(): Flow<Double>

    @Delete
    suspend fun eliminarIngreso(ingreso: IngresoEntity)
}
