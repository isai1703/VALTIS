package com.multiservicios.valtis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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

    LaunchedEffect(Unit) {
        delay(4700)
        showSplash = false
    }

    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(tween(350)),
        exit = fadeOut(tween(800))
    ) {
        ValtisSplash()
    }

    if (!showSplash) {
        ValtisMain()
    }
}

@Composable
fun ValtisSplash() {

    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        startAnimation = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1100,
            easing = FastOutSlowInEasing
        ),
        label = "splashAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.88f,
        animationSpec = tween(
            durationMillis = 1350,
            easing = FastOutSlowInEasing
        ),
        label = "splashScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ValtisBlue),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(340.dp, 225.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .alpha(alpha)
        ) {

            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.valtis_logo_splash),
                contentDescription = "VALTIS",
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = "VALTIS",
                color = ValtisWhite,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 98.dp)
            )

            Text(
                text = "Tu dinero. Tu control. Tu futuro.",
                color = ValtisWhite,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 138.dp)
            )

            Text(
                text = "Powered by Multiservicios",
                color = ValtisWhite,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 186.dp)
            )
        }
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
                        onClick = { selected = section },
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
                ValtisSection.INICIO -> DashboardScreen()
                ValtisSection.INGRESOS -> IngresosScreen()
                ValtisSection.GASTOS -> GastosScreen()
                ValtisSection.COMPROMISOS -> CompromisosScreen()
                ValtisSection.DEUDAS -> DeudasScreen()
            }
        }
    }
}

@Composable
private fun DashboardScreen() {

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

            Text(
                text = "Tu resumen financiero",
                color = ValtisText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            BalanceCard()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Ingresos",
                    value = "$0.00",
                    color = ValtisGreen
                )

                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Gastos",
                    value = "$0.00",
                    color = ValtisRed
                )
            }
        }

        item {
            SectionTitle("Próximos compromisos")
        }

        item {
            EmptyCard(
                title = "Sin compromisos registrados",
                message = "Aquí aparecerán tus próximos pagos."
            )
        }

        item {
            SectionTitle("Actividad reciente")
        }

        item {
            EmptyCard(
                title = "Sin movimientos",
                message = "Cuando registres ingresos o gastos aparecerán aquí."
            )
        }

        item {
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BalanceCard() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ValtisBlue
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Text(
                text = "Disponible",
                color = ValtisWhite.copy(alpha = 0.75f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "$0.00",
                color = ValtisWhite,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Sin movimientos registrados",
                color = ValtisWhite.copy(alpha = 0.8f),
                fontSize = 12.sp
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
