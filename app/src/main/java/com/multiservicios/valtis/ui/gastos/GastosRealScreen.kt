package com.multiservicios.valtis.ui.gastos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import java.util.Locale

@Composable
fun GastosRealScreen() {

    val context = LocalContext.current

    val database = remember {
        ValtisDatabaseProvider.getDatabase(context)
    }

    val viewModel: GastosViewModel = viewModel(
        factory = GastosViewModelFactory(database.valtisDao())
    )

    val gastos by viewModel.gastos.collectAsState()
    val totalGastos by viewModel.totalGastos.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Gastos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = String.format(
                Locale.US,
                "Total registrado: $%.2f",
                totalGastos
            ),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar gasto")
        }

        Spacer(Modifier.height(16.dp))

        if (gastos.isEmpty()) {

            Text(
                text = "Aún no tienes gastos registrados."
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = gastos,
                    key = { it.id }
                ) { gasto ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = gasto.concepto,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = gasto.categoria,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "$%.2f",
                                        gasto.monto
                                    ),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.eliminarGasto(gasto)
                                }
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {

        RegistrarGastoDialog(
            onDismiss = {
                mostrarDialogo = false
            },
            onGuardar = { monto, concepto, categoria ->

                viewModel.registrarGasto(
                    monto = monto,
                    concepto = concepto,
                    fecha = System.currentTimeMillis(),
                    categoria = categoria
                )

                mostrarDialogo = false
            }
        )
    }
}


@Composable
private fun RegistrarGastoDialog(
    onDismiss: () -> Unit,
    onGuardar: (Double, String, String) -> Unit
) {

    var monto by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Registrar gasto")
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        monto = it
                    },
                    label = {
                        Text("Monto")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = concepto,
                    onValueChange = {
                        concepto = it
                    },
                    label = {
                        Text("Concepto")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = {
                        categoria = it
                    },
                    label = {
                        Text("Categoría")
                    },
                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val montoDouble =
                        monto.replace(",", ".").toDoubleOrNull()

                    if (
                        montoDouble != null &&
                        montoDouble > 0 &&
                        concepto.isNotBlank()
                    ) {

                        onGuardar(
                            montoDouble,
                            concepto.trim(),
                            if (categoria.isBlank()) {
                                "General"
                            } else {
                                categoria.trim()
                            }
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
