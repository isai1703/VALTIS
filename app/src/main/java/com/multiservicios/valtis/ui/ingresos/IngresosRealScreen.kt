package com.multiservicios.valtis.ui.ingresos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiservicios.valtis.data.local.IngresoRepository
import com.multiservicios.valtis.data.local.ValtisDatabaseProvider
import com.multiservicios.valtis.data.local.entities.IngresoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IngresosRealScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember {
        ValtisDatabaseProvider.getDatabase(context)
    }

    val repository = remember {
        IngresoRepository(database.valtisDao())
    }

    val ingresoViewModel: IngresosViewModel = viewModel(
        factory = IngresosViewModelFactory(repository)
    )

    val ingresos by ingresoViewModel.ingresos.collectAsState()

    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
    ) {

        Text(
            text = "Ingresos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Registra y controla tus depósitos reales.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar ingreso")
        }

        Spacer(Modifier.height(20.dp))

        if (ingresos.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Sin ingresos registrados",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Cuando recibas una nómina o depósito, regístralo aquí."
                    )
                }
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = ingresos,
                    key = { it.id }
                ) { ingreso ->

                    IngresoItem(
                        ingreso = ingreso,
                        onDelete = {
                            ingresoViewModel.eliminarIngreso(ingreso)
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {

        RegistrarIngresoDialog(
            onDismiss = {
                mostrarDialogo = false
            },
            onSave = { monto, concepto, fecha, fuente ->

                ingresoViewModel.registrarIngreso(
                    monto = monto,
                    concepto = concepto,
                    fecha = fecha,
                    fuente = fuente
                )

                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun IngresoItem(
    ingreso: IngresoEntity,
    onDelete: () -> Unit
) {

    val fecha = remember(ingreso.fecha) {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date(ingreso.fecha))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = ingreso.concepto,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "$${String.format(Locale.US, "%,.2f", ingreso.monto)}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${ingreso.fuente} • $fecha",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            TextButton(
                onClick = onDelete
            ) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
private fun RegistrarIngresoDialog(
    onDismiss: () -> Unit,
    onSave: (
        Double,
        String,
        Long,
        String
    ) -> Unit
) {

    var monto by remember {
        mutableStateOf("")
    }

    var concepto by remember {
        mutableStateOf("")
    }

    var fuente by remember {
        mutableStateOf("Nómina")
    }

    var error by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Registrar ingreso")
        },

        text = {

            Column {

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
                    value = concepto,
                    onValueChange = {
                        concepto = it
                        error = ""
                    },
                    label = {
                        Text("Concepto")
                    },
                    placeholder = {
                        Text("Ej. Nómina quincenal")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = fuente,
                    onValueChange = {
                        fuente = it
                    },
                    label = {
                        Text("Fuente")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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

                    when {
                        valor == null || valor <= 0 -> {
                            error = "Ingresa un monto válido."
                        }

                        concepto.isBlank() -> {
                            error = "Ingresa un concepto."
                        }

                        fuente.isBlank() -> {
                            error = "Ingresa la fuente."
                        }

                        else -> {
                            onSave(
                                valor,
                                concepto.trim(),
                                System.currentTimeMillis(),
                                fuente.trim()
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
