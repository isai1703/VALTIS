package com.multiservicios.valtis.ui.compromisos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiservicios.valtis.data.local.ValtisDatabaseProvider
import com.multiservicios.valtis.data.local.entities.CompromisoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompromisosRealScreen() {

    val context = LocalContext.current

    val database = remember {
        ValtisDatabaseProvider.getDatabase(context)
    }

    val compromisoViewModel: CompromisosViewModel = viewModel(
        factory = CompromisosViewModelFactory(
            database.valtisDao()
        )
    )

    val compromisos by compromisoViewModel.compromisos.collectAsState()

    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Text(
            text = "Compromisos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Administra tus pagos y fechas límite.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar compromiso")
        }

        Spacer(Modifier.height(20.dp))

        if (compromisos.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Sin compromisos registrados",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Agrega pagos como moto, colegiatura, renta o servicios."
                    )
                }
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = compromisos,
                    key = { it.id }
                ) { compromiso ->

                    CompromisoItem(
                        compromiso = compromiso,
                        onDelete = {
                            compromisoViewModel.eliminarCompromiso(
                                compromiso
                            )
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {

        RegistrarCompromisoDialog(
            onDismiss = {
                mostrarDialogo = false
            },
            onSave = { nombre, monto, fecha ->

                compromisoViewModel.registrarCompromiso(
                    nombre = nombre,
                    monto = monto,
                    fechaVencimiento = fecha
                )

                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun CompromisoItem(
    compromiso: CompromisoEntity,
    onDelete: () -> Unit
) {

    val fecha = remember(compromiso.fechaVencimiento) {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date(compromiso.fechaVencimiento))
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = compromiso.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "$${String.format(Locale.US, "%,.2f", compromiso.monto)}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Vence: $fecha",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            if (compromiso.apartado > 0) {

                Text(
                    text = "Apartado: $${String.format(Locale.US, "%,.2f", compromiso.apartado)}"
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onDelete
            ) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
private fun RegistrarCompromisoDialog(
    onDismiss: () -> Unit,
    onSave: (
        String,
        Double,
        Long
    ) -> Unit
) {

    var nombre by remember {
        mutableStateOf("")
    }

    var monto by remember {
        mutableStateOf("")
    }

    var fecha by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Agregar compromiso")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        error = ""
                    },
                    label = {
                        Text("Nombre")
                    },
                    placeholder = {
                        Text("Ej. Moto")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        monto = it
                        error = ""
                    },
                    label = {
                        Text("Monto")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = {
                        fecha = it
                            .filter { char -> char.isDigit() }
                            .take(8)
                        error = ""
                    },
                    label = {
                        Text("Vencimiento")
                    },
                    placeholder = {
                        Text("ddMMyyyy")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Ejemplo: 30102026",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (error.isNotEmpty()) {

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    val valor = monto
                        .replace(",", "")
                        .toDoubleOrNull()

                    val fechaReal = try {

                        if (fecha.length != 8) {
                            null
                        } else {

                            SimpleDateFormat(
                                "ddMMyyyy",
                                Locale.getDefault()
                            ).apply {
                                isLenient = false
                            }.parse(fecha)?.time
                        }

                    } catch (_: Exception) {
                        null
                    }

                    when {
                        nombre.isBlank() -> {
                            error = "Ingresa el nombre."
                        }

                        valor == null || valor <= 0 -> {
                            error = "Ingresa un monto válido."
                        }

                        fechaReal == null -> {
                            error = "Ingresa una fecha válida como ddMMyyyy."
                        }

                        else -> {

                            onSave(
                                nombre.trim(),
                                valor,
                                fechaReal
                            )
                        }
                    }
                }
            ) {
                Text("Guardar")
            }
        },

        dismissButton = {

            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
