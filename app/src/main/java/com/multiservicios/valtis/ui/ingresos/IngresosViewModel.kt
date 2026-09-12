package com.multiservicios.valtis.ui.ingresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiservicios.valtis.data.local.IngresoRepository
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
        }
    }

    fun eliminarIngreso(ingreso: IngresoEntity) {
        viewModelScope.launch {
            repository.eliminarIngreso(ingreso)
        }
    }
}
