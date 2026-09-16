package com.multiservicios.valtis.ui.compromisos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.multiservicios.valtis.data.local.ValtisDao

class CompromisosViewModelFactory(
    private val dao: ValtisDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(CompromisosViewModel::class.java)) {
            return CompromisosViewModel(dao) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
