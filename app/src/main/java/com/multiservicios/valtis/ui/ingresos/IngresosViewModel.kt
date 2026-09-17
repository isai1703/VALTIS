package com.multiservicios.valtis.ui.ingresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiservicios.valtis.FinancialEngine
import com.multiservicios.valtis.ValtisCommitment
import com.multiservicios.valtis.ValtisDeposit
import com.multiservicios.valtis.data.local.IngresoRepository
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

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

            val ingreso = IngresoEntity(
                monto = monto,
                concepto = concepto,
                fecha = fecha,
                fuente = fuente
            )

            repository.insertarIngreso(ingreso)

            val compromisos =
                repository.obtenerCompromisos()

            if (compromisos.isEmpty()) {
                return@launch
            }

            val resultado =
                FinancialEngine.calculate(
                    deposit = ValtisDeposit(
                        amount = monto
                    ),
                    commitments = compromisos.map { compromiso ->

                        ValtisCommitment(
                            id = compromiso.id,
                            name = compromiso.nombre,
                            amount = compromiso.monto,
                            depositsUntilDue =
                                depositsUntilDueWeekly(
                                    fromDate = fecha,
                                    dueDate = compromiso.fechaVencimiento
                                ),
                            alreadySetAside = compromiso.apartado
                        )
                    }
                )

            resultado.commitments.forEach { calculado ->

                val compromiso =
                    compromisos.firstOrNull {
                        it.id == calculado.id
                    }

                if (compromiso != null) {

                    val nuevoApartado =
                        compromiso.apartado +
                            calculado.recommendedSetAside

                    repository.actualizarApartado(
                        id = compromiso.id,
                        apartado = nuevoApartado
                    )
                }
            }
        }
    }

    fun eliminarIngreso(ingreso: IngresoEntity) {
        viewModelScope.launch {
            repository.eliminarIngreso(ingreso)
        }
    }

    private fun depositsUntilDueWeekly(
        fromDate: Long,
        dueDate: Long
    ): Int {

        val days =
            TimeUnit.MILLISECONDS.toDays(
                (dueDate - fromDate)
                    .coerceAtLeast(0L)
            )

        return ceil(days / 7.0)
            .toInt()
            .coerceAtLeast(1)
    }
}
