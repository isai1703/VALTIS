package com.multiservicios.valtis

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.multiservicios.valtis.data.local.ValtisDatabaseProvider
import com.multiservicios.valtis.data.local.entities.DeudaEntity
import com.multiservicios.valtis.finance.ObligationType
import com.multiservicios.valtis.finance.PayrollDeposit
import com.multiservicios.valtis.finance.PayrollFrequency
import com.multiservicios.valtis.finance.SetAsideEngine
import com.multiservicios.valtis.finance.SetAsideObligation
import com.multiservicios.valtis.finance.SetAsideResult
import com.multiservicios.valtis.ui.compromisos.CompromisosRealScreen
import com.multiservicios.valtis.ui.deudas.DeudasRealScreen
import com.multiservicios.valtis.ui.gastos.GastosRealScreen
import com.multiservicios.valtis.ui.ingresos.IngresosRealScreen
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

private val ValtisBlue = Color(0xFF163754)
private val ValtisWhite = Color.White
private val ValtisBackground = Color(0xFFF5F7FA)
private val ValtisText = Color(0xFF18212B)
private val ValtisMuted = Color(0xFF68727D)
private val ValtisGreen = Color(0xFF198754)
private val ValtisRed = Color(0xFFD64545)
private val ValtisGold = Color(0xFFC9A227)
private val ValtisOrange = Color(0xFFE38B2C)

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

    var showSplash by remember {
        mutableStateOf(true)
    }

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

                    layoutParams =
                        ViewGroup.LayoutParams(
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
                    DeudasRealScreen()
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

    val deudas by dao.observarDeudas()
        .collectAsState(initial = emptyList())

    val gastos by dao.observarGastos()
        .collectAsState(initial = emptyList())

    val ultimoIngreso =
        ingresos.firstOrNull()

    val gastosDesdeUltimoIngreso =
        remember(
            ultimoIngreso,
            gastos
        ) {

            ultimoIngreso?.let { ingreso ->

                gastos
                    .filter {
                        it.fecha >= ingreso.fecha
                    }
                    .sumOf {
                        it.monto
                    }

            } ?: 0.0
        }

    val financialResult =
        remember(
            ultimoIngreso,
            compromisos,
            deudas
        ) {

            if (ultimoIngreso == null) {

                null

            } else {

                val obligaciones =
                    mutableListOf<SetAsideObligation>()

                compromisos.forEach { compromiso ->

                    obligaciones +=
                        SetAsideObligation(
                            id = compromiso.id,
                            name = compromiso.nombre,
                            amount = compromiso.monto,
                            dueDate =
                                millisToLocalDate(
                                    compromiso.fechaVencimiento
                                ),
                            alreadySetAside =
                                compromiso.apartado,
                            previousShortfall = 0.0,
                            active = true,
                            type =
                                ObligationType.COMPROMISO
                        )
                }

                deudas
                    .filter { it.activa }
                    .forEach { deuda ->

                        obligaciones +=
                            SetAsideObligation(
                                id = deuda.id,
                                name = deuda.nombre,
                                amount = deuda.montoPago,
                                dueDate =
                                    millisToLocalDate(
                                        deuda.fechaProximoPago
                                    ),
                                alreadySetAside =
                                    deuda.apartado,
                                previousShortfall =
                                    deuda.faltanteAnterior,
                                active =
                                    deuda.activa,
                                type =
                                    ObligationType.DEUDA
                            )
                    }

                SetAsideEngine.calculate(

                    deposit =
                        PayrollDeposit(
                            id =
                                ultimoIngreso.id,
                            amount =
                                ultimoIngreso.monto,
                            date =
                                millisToLocalDate(
                                    ultimoIngreso.fecha
                                )
                        ),

                    obligations =
                        obligaciones,

                    frequency =
                        PayrollFrequency.WEEKLY
                )
            }
        }

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)

    ) {

        item {

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text = "Hola 👋",
                color = ValtisMuted,
                fontSize = 15.sp
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(
                text = "Tu resumen financiero",
                color = ValtisText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(
                text =
                    "Esto es lo que realmente tienes disponible.",
                color = ValtisMuted,
                fontSize = 13.sp
            )
        }

        if (financialResult == null) {

            item {

                EmptyCard(
                    title = "Aún no tienes ingresos",
                    message =
                        "Registra tu primer depósito real para que VALTIS pueda calcular tu dinero disponible."
                )
            }

            item {

                EmptyCard(
                    title = "Tus obligaciones",

                    message =
                        if (
                            compromisos.isEmpty() &&
                            deudas.isEmpty()
                        ) {

                            "Todavía no tienes compromisos ni deudas registrados."

                        } else {

                            "Tienes ${
                                compromisos.size +
                                    deudas.count { it.activa }
                            } obligación(es) registrada(s)."
                        }
                )
            }

        } else {

            val disponibleDespuesDeGastos =
                (
                    financialResult.availableToSpend -
                        gastosDesdeUltimoIngreso
                    )
                    .coerceAtLeast(0.0)

            item {

                BalanceCard(
                    available =
                        disponibleDespuesDeGastos,

                    hasInsufficientFunds =
                        financialResult.hasInsufficientFunds
                )
            }

            item {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {

                    SummaryCard(
                        modifier =
                            Modifier.weight(1f),

                        title =
                            "Ingreso",

                        value =
                            money(
                                financialResult.depositAmount
                            ),

                        color =
                            ValtisGreen
                    )

                    SummaryCard(
                        modifier =
                            Modifier.weight(1f),

                        title =
                            "Apartado",

                        value =
                            money(
                                financialResult
                                    .totalRecommendedSetAside
                            ),

                        color =
                            ValtisGold
                    )
                }
            }

            item {

                SummaryCard(
                    modifier =
                        Modifier.fillMaxWidth(),

                    title =
                        "Gastos desde la última nómina",

                    value =
                        money(
                            gastosDesdeUltimoIngreso
                        ),

                    color =
                        ValtisRed
                )
            }

            item {

                SectionTitle(
                    "Próximas obligaciones"
                )
            }

            if (
                financialResult.obligations.isEmpty()
            ) {

                item {

                    EmptyCard(
                        title =
                            "Sin obligaciones registradas",

                        message =
                            "Agrega compromisos o deudas para que VALTIS calcule cuánto apartar."
                    )
                }

            } else {

                items(
                    financialResult.obligations.size
                ) { index ->

                    val obligation =
                        financialResult
                            .obligations[index]

                    val deuda =
                        if (
                            obligation.type ==
                                ObligationType.DEUDA
                        ) {

                            deudas.firstOrNull {
                                it.id ==
                                    obligation.obligationId
                            }

                        } else {

                            null
                        }

                    ObligationCard(
                        obligation =
                            obligation,

                        deuda =
                            deuda
                    )
                }
            }

            if (
                financialResult.hasInsufficientFunds
            ) {

                item {

                    InsufficientFundsCard(
                        amount =
                            financialResult
                                .totalProjectedShortfall
                    )
                }
            }

            item {

                SectionTitle(
                    "Actividad reciente"
                )
            }

            item {

                ActivityCard(
                    title =
                        ultimoIngreso?.concepto
                            ?: "Último ingreso",

                    amount =
                        ultimoIngreso?.monto
                            ?: 0.0,

                    date =
                        ultimoIngreso?.fecha
                )
            }
        }

        item {

            Spacer(
                Modifier.height(20.dp)
            )
        }
    }
}

private fun millisToLocalDate(
    millis: Long
): LocalDate {

    return Instant
        .ofEpochMilli(millis)
        .atZone(
            ZoneId.systemDefault()
        )
        .toLocalDate()
}

@Composable
private fun BalanceCard(
    available: Double,
    hasInsufficientFunds: Boolean
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        color =
            ValtisBlue
    ) {

        Column(

            modifier =
                Modifier.padding(22.dp)

        ) {

            Text(
                text = "Disponible real",
                color =
                    ValtisWhite.copy(
                        alpha = 0.75f
                    ),
                fontSize = 14.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text =
                    money(available),

                color =
                    ValtisWhite,

                fontSize = 34.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text =
                    if (hasInsufficientFunds) {
                        "Hay obligaciones que requieren atención."
                    } else {
                        "Dinero libre después de apartados y gastos."
                    },

                color =
                    ValtisWhite.copy(
                        alpha = 0.85f
                    ),

                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ObligationCard(
    obligation: SetAsideResult,
    deuda: DeudaEntity?
) {

    val today =
        LocalDate.now()

    val vencida =
        obligation.dueDate.isBefore(today)

    val diasRestantes =
        java.time.temporal.ChronoUnit.DAYS.between(
            today,
            obligation.dueDate
        )

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(20.dp),

        color =
            Color.White
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp)

        ) {

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            obligation.name,

                        color =
                            ValtisText,

                        fontSize =
                            17.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (
                                obligation.type ==
                                    ObligationType.DEUDA
                            ) {
                                "Deuda recurrente"
                            } else {
                                "Compromiso"
                            },

                        color =
                            ValtisMuted,

                        fontSize =
                            12.sp
                    )
                }

                StatusBadge(
                    vencida =
                        vencida,

                    diasRestantes =
                        diasRestantes
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                MiniInfo(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        "Total",

                    value =
                        money(
                            obligation.totalAmount
                        )
                )

                MiniInfo(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        "Apartado",

                    value =
                        money(
                            obligation.alreadySetAside
                        )
                )

                MiniInfo(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        "Pendiente",

                    value =
                        money(
                            obligation.remainingAmount
                        )
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp),

                color =
                    Color(0xFFF5F7FA)
            ) {

                Column(

                    modifier =
                        Modifier.padding(14.dp)

                ) {

                    Text(
                        text =
                            "Próximo vencimiento",

                        color =
                            ValtisMuted,

                        fontSize =
                            12.sp
                    )

                    Spacer(
                        Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            formatDate(
                                obligation.dueDate
                            ),

                        color =
                            ValtisText,

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    if (
                        obligation.recommendedSetAside >
                            0.009
                    ) {

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Apartar ahora: ${
                                    money(
                                        obligation
                                            .recommendedSetAside
                                    )
                                }",

                            color =
                                ValtisGreen,

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    if (
                        obligation.projectedShortfall >
                            0.009
                    ) {

                        Spacer(
                            Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "Faltante proyectado: ${
                                    money(
                                        obligation
                                            .projectedShortfall
                                    )
                                }",

                            color =
                                ValtisRed,

                            fontSize =
                                13.sp
                        )
                    }
                }
            }

            if (
                deuda != null
            ) {

                Spacer(
                    Modifier.height(12.dp)
                )

                DebtDetails(
                    deuda =
                        deuda
                )
            }
        }
    }
}

@Composable
private fun DebtDetails(
    deuda: DeudaEntity
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(14.dp),

        color =
            Color(0xFFFDF9EF)
    ) {

        Column(

            modifier =
                Modifier.padding(14.dp)

        ) {

            Text(
                text =
                    "Plan de deuda",

                color =
                    ValtisText,

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "Pagos restantes",

                    color =
                        ValtisMuted,

                    fontSize =
                        12.sp
                )

                Text(
                    text =
                        deuda.pagosRestantes
                            .toString(),

                    color =
                        ValtisText,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                Modifier.height(5.dp)
            )

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "Pago programado",

                    color =
                        ValtisMuted,

                    fontSize =
                        12.sp
                )

                Text(
                    text =
                        money(
                            deuda.montoPago
                        ),

                    color =
                        ValtisText,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                Modifier.height(5.dp)
            )

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "Próximo pago",

                    color =
                        ValtisMuted,

                    fontSize =
                        12.sp
                )

                Text(
                    text =
                        formatDate(
                            millisToLocalDate(
                                deuda.fechaProximoPago
                            )
                        ),

                    color =
                        ValtisText,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            if (
                deuda.faltanteAnterior >
                    0.009
            ) {

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Faltante anterior: ${
                            money(
                                deuda.faltanteAnterior
                            )
                        }",

                    color =
                        ValtisRed,

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    vencida: Boolean,
    diasRestantes: Long
) {

    val text: String
    val color: Color

    when {

        vencida -> {
            text = "Vencida"
            color = ValtisRed
        }

        diasRestantes == 0L -> {
            text = "Hoy"
            color = ValtisRed
        }

        diasRestantes == 1L -> {
            text = "Mañana"
            color = ValtisOrange
        }

        diasRestantes <= 7L -> {
            text = "En $diasRestantes días"
            color = ValtisOrange
        }

        else -> {
            text = "Programada"
            color = ValtisGreen
        }
    }

    Surface(

        shape =
            RoundedCornerShape(50.dp),

        color =
            color.copy(alpha = 0.10f)
    ) {

        Text(

            text =
                text,

            color =
                color,

            fontSize =
                11.sp,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
        )
    }
}

@Composable
private fun MiniInfo(
    modifier: Modifier,
    title: String,
    value: String
) {

    Column(
        modifier =
            modifier
    ) {

        Text(
            text =
                title,

            color =
                ValtisMuted,

            fontSize =
                10.sp
        )

        Spacer(
            Modifier.height(3.dp)
        )

        Text(
            text =
                value,

            color =
                ValtisText,

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}

private fun formatDate(
    date: LocalDate
): String {

    return String.format(
        Locale.getDefault(),

        "%02d/%02d/%04d",

        date.dayOfMonth,
        date.monthValue,
        date.year
    )
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color
) {

    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(20.dp),

        color =
            Color.White
    ) {

        Column(

            modifier =
                Modifier.padding(17.dp)

        ) {

            Text(
                text =
                    title,

                color =
                    ValtisMuted,

                fontSize =
                    13.sp
            )

            Spacer(
                Modifier.height(7.dp)
            )

            Text(
                text =
                    value,

                color =
                    color,

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    amount: Double,
    date: Long?
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color.White
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp)

        ) {

            Text(
                text =
                    title,

                color =
                    ValtisText,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text =
                    "Ingreso registrado: ${
                        money(amount)
                    }",

                color =
                    ValtisGreen,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold
            )

            if (date != null) {

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Fecha: ${
                            formatDate(
                                millisToLocalDate(date)
                            )
                        }",

                    color =
                        ValtisMuted,

                    fontSize =
                        12.sp
                )
            }
        }
    }
}

@Composable
private fun InsufficientFundsCard(
    amount: Double
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color(0xFFFFF4F4)
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp)

        ) {

            Text(
                text =
                    "Fondos insuficientes",

                color =
                    ValtisRed,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                text =
                    "Esta nómina no alcanza para cubrir todo lo proyectado.",

                color =
                    ValtisText,

                fontSize =
                    13.sp
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                text =
                    "Faltante proyectado: ${money(amount)}",

                color =
                    ValtisRed,

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                text =
                    "VALTIS continuará recalculando el apartado con las siguientes nóminas.",

                color =
                    ValtisMuted,

                fontSize =
                    12.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String
) {

    Text(

        text =
            title,

        color =
            ValtisText,

        fontSize =
            18.sp,

        fontWeight =
            FontWeight.Bold,

        modifier =
            Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun EmptyCard(
    title: String,
    message: String
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            Color.White
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp)

        ) {

            Text(
                text =
                    title,

                color =
                    ValtisText,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                text =
                    message,

                color =
                    ValtisMuted,

                fontSize =
                    13.sp
            )
        }
    }
}

@Composable
private fun IngresosScreen() {

    ModuleScreen(
        title =
            "Ingresos",

        subtitle =
            "Registra y controla tus depósitos reales.",

        action =
            "Registrar ingreso"
    )
}

@Composable
private fun GastosScreen() {

    ModuleScreen(
        title =
            "Gastos",

        subtitle =
            "Controla en qué estás utilizando tu dinero.",

        action =
            "Registrar gasto"
    )
}

@Composable
private fun CompromisosScreen() {

    ModuleScreen(
        title =
            "Compromisos",

        subtitle =
            "Administra tus pagos recurrentes y fechas límite.",

        action =
            "Agregar compromiso"
    )
}

@Composable
private fun DeudasScreen() {

    ModuleScreen(
        title =
            "Deudas",

        subtitle =
            "Consulta saldos, pagos y progreso.",

        action =
            "Agregar deuda"
    )
}

@Composable
private fun ModuleScreen(
    title: String,
    subtitle: String,
    action: String
) {

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp)

    ) {

        item {

            Spacer(
                Modifier.height(18.dp)
            )

            Text(
                text =
                    title,

                color =
                    ValtisText,

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text =
                    subtitle,

                color =
                    ValtisMuted,

                fontSize =
                    14.sp
            )

            Spacer(
                Modifier.height(24.dp)
            )

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                color =
                    ValtisBlue
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                    contentAlignment =
                        Alignment.Center

                ) {

                    Text(
                        text =
                            action,

                        color =
                            ValtisWhite,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            EmptyCard(

                title =
                    "Aún no hay registros",

                message =
                    "Aquí aparecerá tu información cuando comiences a utilizar VALTIS."
            )
        }
    }
}

private fun money(
    value: Double
): String {

    return String.format(
        Locale.US,
        "$%,.2f",
        value
    )
}
