package com.multiservicios.valtis.ui.ingresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiservicios.valtis.data.local.IngresoRepository
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import com.multiservicios.valtis.finance.ObligationType
import com.multiservicios.valtis.finance.PayrollDeposit
import com.multiservicios.valtis.finance.PayrollFrequency
import com.multiservicios.valtis.finance.SetAsideEngine
import com.multiservicios.valtis.finance.SetAsideObligation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class IngresosViewModel(
    private val repository: IngresoRepository
) : ViewModel() {

    val ingresos: StateFlow<List<IngresoEntity>> =
        repository.observarIngresos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val totalIngresos: StateFlow<Double> =
        repository.observarTotalIngresos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    fun registrarIngreso(
        monto: Double,
        concepto: String,
        fecha: Long,
        fuente: String
    ) {
        viewModelScope.launch {

            repository.insertarIngreso(
                IngresoEntity(
                    monto = monto,
                    concepto = concepto,
                    fecha = fecha,
                    fuente = fuente
                )
            )

            procesarApartados(
                monto = monto,
                fecha = fecha
            )
        }
    }

    private suspend fun procesarApartados(
        monto: Double,
        fecha: Long
    ) {

        /*
         * ============================================================
         * UNA SOLA NÓMINA
         * ============================================================
         *
         * Compromisos y deudas entran juntos al mismo motor.
         *
         * Esto evita que:
         *
         *   Compromisos -> gasten toda la nómina
         *   Deudas      -> vuelvan a calcular usando la misma nómina
         *
         * Cada peso disponible se distribuye una sola vez.
         */

        val compromisos =
            repository.obtenerCompromisos()

        val deudas =
            repository.obtenerDeudasActivas()

        if (
            compromisos.isEmpty() &&
            deudas.isEmpty()
        ) {
            return
        }

        val obligaciones =
            mutableListOf<SetAsideObligation>()

        /*
         * ------------------------------------------------------------
         * COMPROMISOS
         * ------------------------------------------------------------
         */

        compromisos.forEach { compromiso ->

            obligaciones += SetAsideObligation(
                id = compromiso.id,
                name = compromiso.nombre,
                amount = compromiso.monto,
                dueDate = millisToLocalDate(
                    compromiso.fechaVencimiento
                ),
                alreadySetAside =
                    compromiso.apartado,
                previousShortfall = 0.0,
                active = true,
                type = ObligationType.COMPROMISO
            )
        }

        /*
         * ------------------------------------------------------------
         * DEUDAS RECURRENTES
         * ------------------------------------------------------------
         */

        deudas.forEach { deuda ->

            obligaciones += SetAsideObligation(
                id = deuda.id,
                name = deuda.nombre,
                amount = deuda.montoPago,
                dueDate = millisToLocalDate(
                    deuda.fechaProximoPago
                ),
                alreadySetAside =
                    deuda.apartado,
                previousShortfall =
                    deuda.faltanteAnterior,
                active =
                    deuda.activa,
                type = ObligationType.DEUDA
            )
        }

        /*
         * ------------------------------------------------------------
         * CÁLCULO ÚNICO
         * ------------------------------------------------------------
         */

        val resultado =
            SetAsideEngine.calculate(
                deposit = PayrollDeposit(
                    id = 0L,
                    amount = monto,
                    date = millisToLocalDate(fecha)
                ),
                obligations = obligaciones,
                frequency = PayrollFrequency.WEEKLY
            )

        /*
         * ------------------------------------------------------------
         * GUARDAR RESULTADOS
         * ------------------------------------------------------------
         */

        resultado.obligations.forEach { calculado ->

            when (calculado.type) {

                ObligationType.COMPROMISO -> {

                    val compromiso =
                        compromisos.firstOrNull {
                            it.id == calculado.obligationId
                        }

                    if (compromiso != null) {

                        val nuevoApartado =
                            compromiso.apartado +
                                calculado.recommendedSetAside

                        repository.actualizarApartado(
                            id = compromiso.id,
                            apartado =
                                nuevoApartado
                        )
                    }
                }

                ObligationType.DEUDA -> {

                    val deuda =
                        deudas.firstOrNull {
                            it.id == calculado.obligationId
                        }

                    if (deuda != null) {

                        val nuevoApartado =
                            deuda.apartado +
                                calculado.recommendedSetAside

                        repository.actualizarEstadoDeuda(
                            id = deuda.id,
                            pagosRestantes =
                                deuda.pagosRestantes,
                            fechaProximoPago =
                                deuda.fechaProximoPago,
                            apartado =
                                nuevoApartado,
                            faltanteAnterior =
                                calculado.projectedShortfall,
                            activa =
                                deuda.activa
                        )
                    }
                }
            }
        }
    }

    fun eliminarIngreso(
        ingreso: IngresoEntity
    ) {
        viewModelScope.launch {
            repository.eliminarIngreso(ingreso)
        }
    }

    private fun millisToLocalDate(
        millis: Long
    ): LocalDate {

        return Instant
            .ofEpochMilli(millis)
            .atZone(
                ZoneId.systemDefault()
            )
            .toLocalDate()
    }
}
