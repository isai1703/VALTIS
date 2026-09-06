package com.multiservicios.valtis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val ValtisDeep = Color(0xFF071D2D)
private val ValtisBlue = Color(0xFF0B304A)
private val ValtisLogoBlue = Color(0xFF123B55)
private val ValtisGold = Color(0xFFC9A95A)
private val ValtisWhite = Color(0xFFFFFFFF)
private val ValtisGray = Color(0xFFE8EDF1)

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
        animationSpec = tween(900),
        label = "logoAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.82f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(650, delayMillis = 950),
        label = "textAlpha"
    )

    val sloganAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(650, delayMillis = 1250),
        label = "sloganAlpha"
    )

    val poweredAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(650, delayMillis = 1550),
        label = "poweredAlpha"
    )

    val infinite = rememberInfiniteTransition(label = "ValtisGlow")

    val glowAlpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
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
                        ValtisBlue.copy(alpha = 0.88f),
                        ValtisDeep,
                        Color.Black
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        androidx.compose.ui.layout.Layout(
            content = {
                Image(
                    painter = painterResource(id = R.drawable.valtis_logo_transparent),
                    contentDescription = "VALTIS",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = logoScale,
                            scaleY = logoScale
                        )
                        .alpha(glowAlpha * logoAlpha)
                        .blur(18.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.valtis_logo_transparent),
                    contentDescription = null,
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

            val width = (constraints.maxWidth * 0.72f).toInt()
            val height = (width * 832f / 1259f).toInt()

            val placeableGlow = measurables[0].measure(
                androidx.compose.ui.unit.Constraints.fixed(width, height)
            )

            val placeableLogo = measurables[1].measure(
                androidx.compose.ui.unit.Constraints.fixed(width, height)
            )

            layout(
                width = constraints.maxWidth,
                height = height
            ) {
                val x = (constraints.maxWidth - width) / 2

                placeableGlow.place(
                    x = x,
                    y = 0
                )

                placeableLogo.place(
                    x = x,
                    y = 0
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "VALTIS",
            modifier = Modifier.alpha(textAlpha),
            color = ValtisWhite,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tu dinero. Tu control. Tu futuro.",
            modifier = Modifier
                .alpha(sloganAlpha)
                .padding(horizontal = 20.dp),
            color = ValtisWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Powered by Multiservicios",
            modifier = Modifier.alpha(poweredAlpha),
            color = ValtisGray.copy(alpha = 0.92f),
            fontSize = 12.sp,
            letterSpacing = 0.8.sp
        )
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
            color = ValtisGold,
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
