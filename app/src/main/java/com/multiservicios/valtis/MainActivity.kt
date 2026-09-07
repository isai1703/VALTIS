package com.multiservicios.valtis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val ValtisDeep = Color(0xFF04111B)
private val ValtisBlue = Color(0xFF0A2B40)
private val ValtisWhite = Color.White

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
        ValtisHomePlaceholder()
    }
}

@Composable
fun ValtisSplash() {

    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1100,
            easing = FastOutSlowInEasing
        ),
        label = "logoAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.88f,
        animationSpec = tween(
            durationMillis = 1300,
            easing = FastOutSlowInEasing
        ),
        label = "logoScale"
    )

    val infinite = rememberInfiniteTransition(
        label = "ValtisPremiumGlow"
    )

    val glowAlpha by infinite.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1900,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ValtisBlue.copy(alpha = 0.95f),
                        ValtisDeep.copy(alpha = 0.98f),
                        Color.Black
                    ),
                    radius = 1050f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        androidx.compose.ui.layout.Layout(
            content = {

                // Halo suave detrás del logo.
                // Blanco para reforzar el contraste del logotipo original.
                Image(
                    painter = painterResource(
                        id = R.drawable.valtis_logo_transparent
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = logoScale * 1.015f,
                            scaleY = logoScale * 1.015f
                        )
                        .alpha(glowAlpha * logoAlpha)
                        .blur(38.dp)
                )

                // Logo oficial sin modificar sus colores.
                Image(
                    painter = painterResource(
                        id = R.drawable.valtis_logo_transparent
                    ),
                    contentDescription = "VALTIS",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = logoScale,
                            scaleY = logoScale
                        )
                        .alpha(logoAlpha)
                )

            }
        ) { measurables, constraints ->

            // Un poco más de presencia visual.
            val width = (constraints.maxWidth * 0.91f).toInt()

            val height = (width * 832f / 1259f).toInt()

            val logoConstraints = Constraints.fixed(
                width,
                height
            )

            val glow = measurables[0].measure(
                logoConstraints
            )

            val logo = measurables[1].measure(
                logoConstraints
            )

            layout(
                width = constraints.maxWidth,
                height = height
            ) {

                val x = (constraints.maxWidth - width) / 2

                glow.place(
                    x = x,
                    y = 0
                )

                logo.place(
                    x = x,
                    y = 0
                )
            }
        }
    }
}

@Composable
private fun ValtisHomePlaceholder() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ValtisDeep),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "VALTIS",
            color = ValtisWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Dashboard",
            color = ValtisWhite,
            fontSize = 18.sp
        )
    }
}
