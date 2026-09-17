package com.multiservicios.valtis.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.multiservicios.valtis.data.local.ValtisDao

class GastosViewModelFactory(
    private val dao: ValtisDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(GastosViewModel::class.java)) {
            return GastosViewModel(dao) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
