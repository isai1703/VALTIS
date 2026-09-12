package com.multiservicios.valtis.ui.ingresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.multiservicios.valtis.data.local.IngresoRepository

class IngresosViewModelFactory(
    private val repository: IngresoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngresosViewModel::class.java)) {
            return IngresosViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
