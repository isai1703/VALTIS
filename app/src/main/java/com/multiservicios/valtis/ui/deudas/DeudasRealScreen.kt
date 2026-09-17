package com.multiservicios.valtis.ui.deudas

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
import com.multiservicios.valtis.data.local.entities.DeudaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeudasRealScreen() {

    val context = LocalContext.current

    val database = remember {
        ValtisDatabaseProvider.getDatabase(context)
    }

    val viewModel: DeudasViewModel = viewModel(
        factory = DeudasViewModelFactory(
            database.valtisDao()
        )
    )

    val deudas by viewModel.deudas.collectAsState()

    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Deudas",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Administra pagos recurrentes sin registrar cada mensualidad.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    mostrarDialogo = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar deuda")
            }

            Spacer(Modifier.height(8.dp))
        }

        if (deudas.isEmpty()) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Sin deudas registradas",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Agrega una deuda como moto, préstamo o financiamiento."
                        )
                    }
                }
            }

        } else {

            items(
                items = deudas,
                key = { it.id }
            ) { deuda ->

                DeudaItem(
                    deuda = deuda,
                    onRegistrarPago = {
                        viewModel.registrarPago(deuda)
                    },
                    onEliminar = {
                        viewModel.eliminarDeuda(deuda)
                    }
                )
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
        }
    }

    if (mostrarDialogo) {

        RegistrarDeudaDialog(
            onDismiss = {
                mostrarDialogo = false
            },
            onSave = { nombre,
                       montoPago,
                       pagosRestantes,
                       periodicidad,
                       diaPago,
                       fechaProximoPago ->

                viewModel.registrarDeuda(
                    nombre = nombre,
                    montoPago = montoPago,
                    pagosRestantes = pagosRestantes,
                    periodicidad = periodicidad,
                    diaPago = diaPago,
                    fechaProximoPago = fechaProximoPago
                )

                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun DeudaItem(
    deuda: DeudaEntity,
    onRegistrarPago: () -> Unit,
    onEliminar: () -> Unit
) {

    val fecha = remember(deuda.fechaProximoPago) {
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(
            Date(deuda.fechaProximoPago)
        )
    }

    val totalPendiente =
        deuda.montoPago * deuda.pagosRestantes

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = deuda.nombre,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "${
                    String.format(
                        Locale.US,
                        "$%,.2f",
                        deuda.montoPago
                    )
                } por pago"
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Pagos restantes: ${deuda.pagosRestantes}"
            )

            Spacer(Modifier.height(4.dp))

            if (deuda.activa) {

                Text(
                    text = "Próximo pago: $fecha"
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Pendiente: ${
                        String.format(
                            Locale.US,
                            "$%,.2f",
                            totalPendiente
                        )
                    }"
                )

                if (deuda.apartado > 0.0) {

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Apartado: ${
                            String.format(
                                Locale.US,
                                "$%,.2f",
                                deuda.apartado
                            )
                        }"
                    )
                }

                if (deuda.faltanteAnterior > 0.0) {

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Faltante anterior: ${
                            String.format(
                                Locale.US,
                                "$%,.2f",
                                deuda.faltanteAnterior
                            )
                        }",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onRegistrarPago,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar pago")
                }

            } else {

                Text(
                    text = "Liquidada",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = onEliminar
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
private fun RegistrarDeudaDialog(
    onDismiss: () -> Unit,
    onSave: (
        String,
        Double,
        Int,
        String,
        Int,
        Long
    ) -> Unit
) {

    var nombre by remember {
        mutableStateOf("")
    }

    var monto by remember {
        mutableStateOf("")
    }

    var pagos by remember {
        mutableStateOf("")
    }

    var periodicidad by remember {
        mutableStateOf("MENSUAL")
    }

    var diaPago by remember {
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
            Text("Agregar deuda")
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
                        Text("Monto de cada pago")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = pagos,
                    onValueChange = {
                        pagos = it.filter(Char::isDigit)
                        error = ""
                    },
                    label = {
                        Text("Pagos restantes")
                    },
                    placeholder = {
                        Text("Ej. 15")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Periodicidad",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        "MENSUAL",
                        "QUINCENAL",
                        "SEMANAL"
                    ).forEach { opcion ->

                        if (periodicidad == opcion) {

                            Button(
                                onClick = {
                                    periodicidad = opcion
                                }
                            ) {
                                Text(
                                    when (opcion) {
                                        "MENSUAL" -> "Mensual"
                                        "QUINCENAL" -> "Quincenal"
                                        else -> "Semanal"
                                    }
                                )
                            }

                        } else {

                            OutlinedButton(
                                onClick = {
                                    periodicidad = opcion
                                }
                            ) {
                                Text(
                                    when (opcion) {
                                        "MENSUAL" -> "Mensual"
                                        "QUINCENAL" -> "Quincenal"
                                        else -> "Semanal"
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = diaPago,
                    onValueChange = {
                        diaPago = it
                            .filter(Char::isDigit)
                            .take(2)
                        error = ""
                    },
                    label = {
                        Text("Día de pago")
                    },
                    placeholder = {
                        Text("Ej. 30")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = {
                        fecha = it
                            .filter(Char::isDigit)
                            .take(8)
                        error = ""
                    },
                    label = {
                        Text("Primer pago")
                    },
                    placeholder = {
                        Text("ddMMyyyy")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Ejemplo: 30092026",
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

                    val montoReal =
                        monto
                            .replace(",", "")
                            .toDoubleOrNull()

                    val pagosReal =
                        pagos.toIntOrNull()

                    val diaReal =
                        diaPago.toIntOrNull()

                    val fechaReal =
                        try {

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

                        montoReal == null || montoReal <= 0 -> {
                            error = "Ingresa un monto válido."
                        }

                        pagosReal == null || pagosReal <= 0 -> {
                            error = "Ingresa una cantidad de pagos válida."
                        }

                        diaReal == null ||
                            diaReal !in 1..31 -> {
                            error = "El día debe estar entre 1 y 31."
                        }

                        fechaReal == null -> {
                            error = "Ingresa una fecha válida como ddMMyyyy."
                        }

                        else -> {

                            onSave(
                                nombre.trim(),
                                montoReal,
                                pagosReal,
                                periodicidad,
                                diaReal,
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
