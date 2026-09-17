package com.multiservicios.valtis

import com.multiservicios.valtis.ui.ingresos.IngresosRealScreen
import com.multiservicios.valtis.data.local.ValtisDatabaseProvider
import com.multiservicios.valtis.ui.compromisos.CompromisosRealScreen
import com.multiservicios.valtis.ui.gastos.GastosRealScreen

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.ceil

private val ValtisBlue = Color(0xFF163754)
private val ValtisWhite = Color.White
private val ValtisBackground = Color(0xFFF5F7FA)
private val ValtisText = Color(0xFF18212B)
private val ValtisMuted = Color(0xFF68727D)
private val ValtisGreen = Color(0xFF198754)
private val ValtisRed = Color(0xFFD64545)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ValtisApp()
        }
    }
}

@Composable
fun ValtisApp() {

    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        ValtisSplashVideo(
            onFinished = {
                showSplash = false
            }
        )
    } else {
        ValtisMain()
    }
}

@Composable
private fun ValtisSplashVideo(
    onFinished: () -> Unit
) {

    val context = LocalContext.current

    val videoView = remember {
        VideoView(context).apply {

            setVideoURI(
                Uri.parse(
                    "android.resource://${context.packageName}/${R.raw.valtis_splash}"
                )
            )

            setOnCompletionListener {
                onFinished()
            }

            setOnErrorListener { _, _, _ ->
                onFinished()
                true
            }

            start()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoView.stopPlayback()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B304A)),
        contentAlignment = Alignment.Center
    ) {

        androidx.compose.ui.viewinterop.AndroidView(
            factory = {
                FrameLayout(context).apply {

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    addView(
                        videoView,
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    LaunchedEffect(Unit) {
        delay(12000)
        onFinished()
    }
}

private enum class ValtisSection(
    val title: String,
    val symbol: String
) {
    INICIO("Inicio", "⌂"),
    INGRESOS("Ingresos", "+"),
    GASTOS("Gastos", "−"),
    COMPROMISOS("Compromisos", "✓"),
    DEUDAS("Deudas", "$")
}

@Composable
private fun ValtisMain() {

    var selected by remember {
        mutableStateOf(ValtisSection.INICIO)
    }

    Scaffold(
        containerColor = ValtisBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                ValtisSection.entries.forEach { section ->

                    NavigationBarItem(
                        selected = selected == section,
                        onClick = {
                            selected = section
                        },
                        icon = {
                            Text(
                                text = section.symbol,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        label = {
                            Text(
                                text = section.title,
                                fontSize = 10.sp
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (selected) {

                ValtisSection.INICIO ->
                    DashboardScreen()

                ValtisSection.INGRESOS ->
                    IngresosRealScreen()

                ValtisSection.GASTOS ->
                    GastosRealScreen()

                ValtisSection.COMPROMISOS ->
                    CompromisosRealScreen()

                ValtisSection.DEUDAS ->
                    DeudasScreen()
            }
        }
    }
}

@Composable
private fun DashboardScreen() {

    val context = LocalContext.current

    val database = remember {
        ValtisDatabaseProvider.getDatabase(context)
    }

    val dao = database.valtisDao()

    val ingresos by dao.observarIngresos()
        .collectAsState(initial = emptyList())

    val compromisos by dao.observarCompromisos()
        .collectAsState(initial = emptyList())

    val gastos by dao.observarGastos()
        .collectAsState(initial = emptyList())

    val ultimoIngreso = ingresos.firstOrNull()

    val gastosDesdeUltimoIngreso = remember(
        ultimoIngreso,
        gastos
    ) {
        ultimoIngreso?.let { ingreso ->
            gastos
                .filter { it.fecha >= ingreso.fecha }
                .sumOf { it.monto }
        } ?: 0.0
    }

    val financialResult = remember(
        ultimoIngreso,
        compromisos
    ) {

        if (ultimoIngreso == null) {

            null

        } else {

            FinancialEngine.calculate(
                deposit = ValtisDeposit(
                    amount = ultimoIngreso.monto
                ),
                commitments = compromisos.map { compromiso ->

                    ValtisCommitment(
                        id = compromiso.id,
                        name = compromiso.nombre,
                        amount = compromiso.monto,
                        depositsUntilDue = depositsUntilDueWeekly(
                            fromDate = ultimoIngreso.fecha,
                            dueDate = compromiso.fechaVencimiento
                        ),
                        alreadySetAside = compromiso.apartado
                    )
                }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Spacer(Modifier.height(18.dp))

            Text(
                text = "Hola 👋",
                color = ValtisMuted,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Tu resumen financiero",
                color = ValtisText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (financialResult == null) {

            item {
                EmptyCard(
                    title = "Aún no tienes ingresos",
                    message = "Registra tu primer depósito real para que VALTIS pueda calcular tu dinero disponible."
                )
            }

            item {
                EmptyCard(
                    title = "Tus compromisos",
                    message = if (compromisos.isEmpty()) {
                        "Todavía no tienes compromisos registrados."
                    } else {
                        "Tienes ${compromisos.size} compromiso(s) registrado(s)."
                    }
                )
            }

        } else {

            val disponibleDespuesDeGastos =
                (financialResult.available - gastosDesdeUltimoIngreso)
                    .coerceAtLeast(0.0)

            item {
                BalanceCard(disponibleDespuesDeGastos)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Ingreso",
                        value = money(financialResult.deposit),
                        color = ValtisGreen
                    )

                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Apartado",
                        value = money(financialResult.totalSetAside),
                        color = ValtisRed
                    )
                }
            }

            item {
                SummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Gastos",
                    value = money(gastosDesdeUltimoIngreso),
                    color = ValtisRed
                )
            }

            item {
                SectionTitle("Próximos compromisos")
            }

            if (financialResult.commitments.isEmpty()) {

                item {
                    EmptyCard(
                        title = "Sin compromisos registrados",
                        message = "Agrega tus próximos pagos para que VALTIS pueda calcular cuánto apartar."
                    )
                }

            } else {

                items(financialResult.commitments.size) { index ->

                    val commitment =
                        financialResult.commitments[index]

                    CommitmentCard(commitment)
                }
            }

            item {
                SectionTitle("Actividad reciente")
            }

            item {
                EmptyCard(
                    title = ultimoIngreso!!.concepto,
                    message = "Último ingreso registrado: ${money(ultimoIngreso!!.monto)}"
                )
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun depositsUntilDueWeekly(
    fromDate: Long,
    dueDate: Long
): Int {

    val millisecondsPerDay = 24L * 60L * 60L * 1000L

    val daysUntilDue =
        ((dueDate - fromDate).toDouble() / millisecondsPerDay)
            .coerceAtLeast(0.0)

    return ceil(daysUntilDue / 7.0)
        .toInt()
        .coerceAtLeast(1)
}

@Composable
private fun BalanceCard(
    available: Double
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ValtisBlue
    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Text(
                text = "Disponible real",
                color = ValtisWhite.copy(alpha = 0.75f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = money(available),
                color = ValtisWhite,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Después de considerar tus próximos compromisos",
                color = ValtisWhite.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CommitmentCard(
    commitment: ValtisCommitmentResult
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = commitment.name,
                color = ValtisText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Apartar ahora: ${money(commitment.recommendedSetAside)}",
                color = ValtisGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Pendiente después de este depósito: ${money(commitment.remainingAmount)}",
                color = ValtisMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = title,
                color = ValtisMuted,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = value,
                color = color,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {

    Text(
        text = title,
        color = ValtisText,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun EmptyCard(
    title: String,
    message: String
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                color = ValtisText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = message,
                color = ValtisMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun IngresosScreen() {

    ModuleScreen(
        title = "Ingresos",
        subtitle = "Registra y controla tus depósitos reales.",
        action = "Registrar ingreso"
    )
}

@Composable
private fun GastosScreen() {

    ModuleScreen(
        title = "Gastos",
        subtitle = "Controla en qué estás utilizando tu dinero.",
        action = "Registrar gasto"
    )
}

@Composable
private fun CompromisosScreen() {

    ModuleScreen(
        title = "Compromisos",
        subtitle = "Administra tus pagos recurrentes y fechas límite.",
        action = "Agregar compromiso"
    )
}

@Composable
private fun DeudasScreen() {

    ModuleScreen(
        title = "Deudas",
        subtitle = "Consulta saldos, pagos y progreso.",
        action = "Agregar deuda"
    )
}

@Composable
private fun ModuleScreen(
    title: String,
    subtitle: String,
    action: String
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        item {

            Spacer(Modifier.height(18.dp))

            Text(
                text = title,
                color = ValtisText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = ValtisMuted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = ValtisBlue
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = action,
                        color = ValtisWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            EmptyCard(
                title = "Aún no hay registros",
                message = "Aquí aparecerá tu información cuando comiences a utilizar VALTIS."
            )
        }
    }
}

private fun money(value: Double): String {
    return String.format(
        Locale.US,
        "$%,.2f",
        value
    )
}
