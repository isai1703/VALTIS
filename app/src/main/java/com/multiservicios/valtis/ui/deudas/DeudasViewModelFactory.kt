package com.multiservicios.valtis.ui.deudas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.multiservicios.valtis.data.local.ValtisDao

class DeudasViewModelFactory(
    private val dao: ValtisDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                DeudasViewModel::class.java
            )
        ) {
            return DeudasViewModel(dao) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
