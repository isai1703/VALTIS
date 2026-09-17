package com.multiservicios.valtis.ui.deudas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiservicios.valtis.data.local.ValtisDao
import com.multiservicios.valtis.data.local.entities.DeudaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeudasViewModel(
    private val dao: ValtisDao
) : ViewModel() {

    val deudas: StateFlow<List<DeudaEntity>> =
        dao.observarDeudas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun registrarDeuda(
        nombre: String,
        montoPago: Double,
        pagosRestantes: Int,
        periodicidad: String,
        diaPago: Int,
        fechaProximoPago: Long
    ) {
        viewModelScope.launch {
            dao.insertarDeuda(
                DeudaEntity(
                    nombre = nombre,
                    montoPago = montoPago,
                    pagosRestantes = pagosRestantes,
                    periodicidad = periodicidad,
                    diaPago = diaPago,
                    fechaProximoPago = fechaProximoPago
                )
            )
        }
    }

    fun eliminarDeuda(deuda: DeudaEntity) {
        viewModelScope.launch {
            dao.eliminarDeuda(deuda)
        }
    }

    fun registrarPago(deuda: DeudaEntity) {
        viewModelScope.launch {

            val nuevosPagosRestantes =
                (deuda.pagosRestantes - 1)
                    .coerceAtLeast(0)

            if (nuevosPagosRestantes == 0) {

                dao.actualizarEstadoDeuda(
                    id = deuda.id,
                    pagosRestantes = 0,
                    fechaProximoPago = deuda.fechaProximoPago,
                    apartado = 0.0,
                    faltanteAnterior = 0.0,
                    activa = false
                )

                return@launch
            }

            val proximaFecha =
                calcularProximaFecha(
                    fechaActual = deuda.fechaProximoPago,
                    periodicidad = deuda.periodicidad,
                    diaPago = deuda.diaPago
                )

            dao.actualizarEstadoDeuda(
                id = deuda.id,
                pagosRestantes = nuevosPagosRestantes,
                fechaProximoPago = proximaFecha,
                apartado = 0.0,
                faltanteAnterior = 0.0,
                activa = true
            )
        }
    }

    private fun calcularProximaFecha(
        fechaActual: Long,
        periodicidad: String,
        diaPago: Int
    ): Long {

        val fecha =
            java.time.Instant
                .ofEpochMilli(fechaActual)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

        val siguiente =
            when (periodicidad.uppercase()) {

                "SEMANAL" ->
                    fecha.plusWeeks(1)

                "QUINCENAL" ->
                    fecha.plusWeeks(2)

                else ->
                    siguienteFechaMensual(
                        fecha = fecha,
                        diaPago = diaPago
                    )
            }

        return siguiente
            .atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun siguienteFechaMensual(
        fecha: java.time.LocalDate,
        diaPago: Int
    ): java.time.LocalDate {

        val siguienteMes =
            fecha.plusMonths(1)

        val diaValido =
            minOf(
                diaPago,
                siguienteMes.lengthOfMonth()
            )

        return siguienteMes.withDayOfMonth(diaValido)
    }
}
