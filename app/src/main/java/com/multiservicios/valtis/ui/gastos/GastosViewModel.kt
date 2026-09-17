package com.multiservicios.valtis.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiservicios.valtis.data.local.ValtisDao
import com.multiservicios.valtis.data.local.entities.GastoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GastosViewModel(
    private val dao: ValtisDao
) : ViewModel() {

    val gastos: StateFlow<List<GastoEntity>> =
        dao.observarGastos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val totalGastos: StateFlow<Double> =
        dao.observarTotalGastos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    fun registrarGasto(
        monto: Double,
        concepto: String,
        fecha: Long,
        categoria: String
    ) {
        viewModelScope.launch {
            dao.insertarGasto(
                GastoEntity(
                    monto = monto,
                    concepto = concepto,
                    fecha = fecha,
                    categoria = categoria
                )
            )
        }
    }

    fun eliminarGasto(gasto: GastoEntity) {
        viewModelScope.launch {
            dao.eliminarGasto(gasto)
        }
    }
}
