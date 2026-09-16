package com.multiservicios.valtis.ui.compromisos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiservicios.valtis.data.local.IngresoRepository
import com.multiservicios.valtis.data.local.ValtisDao
import com.multiservicios.valtis.data.local.entities.CompromisoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CompromisosViewModel(
    private val dao: ValtisDao
) : ViewModel() {

    val compromisos: StateFlow<List<CompromisoEntity>> =
        dao.observarCompromisos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun registrarCompromiso(
        nombre: String,
        monto: Double,
        fechaVencimiento: Long
    ) {
        viewModelScope.launch {
            dao.insertarCompromiso(
                CompromisoEntity(
                    nombre = nombre,
                    monto = monto,
                    fechaVencimiento = fechaVencimiento
                )
            )
        }
    }

    fun eliminarCompromiso(compromiso: CompromisoEntity) {
        viewModelScope.launch {
            dao.eliminarCompromiso(compromiso)
        }
    }
}
