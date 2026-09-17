package com.multiservicios.valtis.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.multiservicios.valtis.data.local.entities.CompromisoEntity
import com.multiservicios.valtis.data.local.entities.GastoEntity
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ValtisDao {

    // INGRESOS

    @Insert
    suspend fun insertarIngreso(ingreso: IngresoEntity)

    @Query("SELECT * FROM ingresos ORDER BY fecha DESC")
    fun observarIngresos(): Flow<List<IngresoEntity>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM ingresos")
    fun observarTotalIngresos(): Flow<Double>

    @Delete
    suspend fun eliminarIngreso(ingreso: IngresoEntity)


    // COMPROMISOS

    @Insert
    suspend fun insertarCompromiso(compromiso: CompromisoEntity)

    @Query("SELECT * FROM compromisos ORDER BY fechaVencimiento ASC")
    fun observarCompromisos(): Flow<List<CompromisoEntity>>

    @Delete
    suspend fun eliminarCompromiso(compromiso: CompromisoEntity)

    @Query("SELECT * FROM compromisos ORDER BY fechaVencimiento ASC")
    suspend fun obtenerCompromisos(): List<CompromisoEntity>

    @Query("UPDATE compromisos SET apartado = :apartado WHERE id = :id")
    suspend fun actualizarApartado(
        id: Long,
        apartado: Double
    )


    // GASTOS

    @Insert
    suspend fun insertarGasto(gasto: GastoEntity)

    @Query("SELECT * FROM gastos ORDER BY fecha DESC")
    fun observarGastos(): Flow<List<GastoEntity>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gastos")
    fun observarTotalGastos(): Flow<Double>

    @Delete
    suspend fun eliminarGasto(gasto: GastoEntity)
}
