package com.multiservicios.valtis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val ValtisDeep = Color(0xFF071D2D)
private val ValtisBlue = Color(0xFF0B304A)
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
        delay(4300)
        showSplash = false
    }

    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(700))
    ) {
        ValtisSplash()
    }

    if (!showSplash) {
        ValtisHomePlaceholder()
    }
}

@Composable
fun ValtisSplash() {

    val logoProgress = remember { Animatable(0f) }
    val arrowProgress = remember { Animatable(0f) }
    val nameProgress = remember { Animatable(0f) }
    val sloganProgress = remember { Animatable(0f) }
    val poweredProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {

        logoProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1100,
                easing = FastOutSlowInEasing
            )
        )

        arrowProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 650,
                easing = FastOutSlowInEasing
            )
        )

        delay(120)

        nameProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(650)
        )

        delay(180)

        sloganProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(650)
        )

        delay(220)

        poweredProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(650)
        )
    }

    val infinite = rememberInfiniteTransition(label = "neon")

    val glowPulse by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ValtisBlue.copy(alpha = 0.72f),
                        ValtisDeep,
                        Color.Black
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        ValtisAnimatedLogo(
            logoProgress = logoProgress.value,
            arrowProgress = arrowProgress.value,
            glow = glowPulse
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "VALTIS",
            modifier = Modifier.alpha(nameProgress.value),
            color = ValtisGold,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Tu dinero. Tu control. Tu futuro.",
            modifier = Modifier
                .alpha(sloganProgress.value)
                .padding(horizontal = 24.dp),
            color = ValtisWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Powered by Multiservicios",
            modifier = Modifier.alpha(poweredProgress.value),
            color = ValtisGray.copy(alpha = 0.9f),
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ValtisAnimatedLogo(
    logoProgress: Float,
    arrowProgress: Float,
    glow: Float
) {

    Canvas(
        modifier = Modifier
            .height(145.dp)
            .padding(horizontal = 50.dp)
    ) {

        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val width = size.width * 0.58f
        val height = size.height * 0.62f

        val leftX = centerX - width / 2f
        val rightX = centerX + width / 2f
        val bottomY = centerY + height / 2f
        val topY = centerY - height / 2f

        val vPath = Path().apply {
            moveTo(leftX, topY)
            lineTo(centerX, bottomY)
            lineTo(rightX, topY)
        }

        val arrowPath = Path().apply {
            moveTo(centerX, bottomY * 0.93f)
            lineTo(centerX, topY * 0.55f)
            moveTo(centerX, topY * 0.55f)
            lineTo(centerX - width * 0.13f, topY * 0.72f)
            moveTo(centerX, topY * 0.55f)
            lineTo(centerX + width * 0.13f, topY * 0.72f)
        }

        val glowWidth = 15.dp.toPx()

        drawPath(
            path = vPath,
            color = ValtisGold.copy(alpha = 0.16f * glow),
            style = Stroke(
                width = glowWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawPath(
            path = vPath,
            color = ValtisGold.copy(alpha = logoProgress),
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawPath(
            path = arrowPath,
            color = ValtisWhite.copy(alpha = 0.18f * arrowProgress),
            style = Stroke(
                width = 13.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawPath(
            path = arrowPath,
            color = ValtisGold.copy(alpha = arrowProgress),
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        if (arrowProgress > 0.9f) {

            drawCircle(
                color = ValtisGold.copy(alpha = 0.20f * glow),
                radius = 17.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(
                    centerX,
                    topY * 0.55f
                )
            )

            drawCircle(
                color = ValtisGold.copy(alpha = 0.9f),
                radius = 3.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(
                    centerX,
                    topY * 0.55f
                )
            )
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
