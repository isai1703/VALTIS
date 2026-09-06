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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

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

    val archProgress = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }
    val arrowProgress = remember { Animatable(0f) }
    val accentProgress = remember { Animatable(0f) }
    val nameProgress = remember { Animatable(0f) }
    val sloganProgress = remember { Animatable(0f) }
    val poweredProgress = remember { Animatable(0f) }
    val exitProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {

        archProgress.animateTo(
            1f,
            tween(900, easing = FastOutSlowInEasing)
        )

        delay(80)

        checkProgress.animateTo(
            1f,
            tween(650, easing = FastOutSlowInEasing)
        )

        delay(80)

        arrowProgress.animateTo(
            1f,
            tween(600, easing = FastOutSlowInEasing)
        )

        delay(100)

        accentProgress.animateTo(
            1f,
            tween(300)
        )

        delay(100)

        nameProgress.animateTo(
            1f,
            tween(550, easing = FastOutSlowInEasing)
        )

        delay(180)

        sloganProgress.animateTo(
            1f,
            tween(650, easing = FastOutSlowInEasing)
        )

        delay(180)

        poweredProgress.animateTo(
            1f,
            tween(650, easing = FastOutSlowInEasing)
        )

        delay(650)

        exitProgress.animateTo(
            1f,
            tween(650, easing = FastOutSlowInEasing)
        )
    }

    val infinite = rememberInfiniteTransition(label = "ValtisNeon")

    val pulse by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 850,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val energy by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1700,
                easing = LinearEasing
            )
        ),
        label = "energy"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ValtisBlue.copy(alpha = 0.82f),
                        ValtisDeep,
                        Color.Black
                    )
                )
            )
            .alpha(1f - exitProgress.value * 0.15f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        ValtisOfficialMark(
            archProgress = archProgress.value,
            checkProgress = checkProgress.value,
            arrowProgress = arrowProgress.value,
            accentProgress = accentProgress.value,
            pulse = pulse,
            energy = energy
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "VALTIS",
            modifier = Modifier.alpha(nameProgress.value),
            color = ValtisWhite,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tu dinero. Tu control. Tu futuro.",
            modifier = Modifier
                .alpha(sloganProgress.value)
                .padding(horizontal = 20.dp),
            color = ValtisWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        PoweredByMultiservicios(
            progress = poweredProgress.value,
            pulse = pulse
        )
    }
}

@Composable
private fun ValtisOfficialMark(
    archProgress: Float,
    checkProgress: Float,
    arrowProgress: Float,
    accentProgress: Float,
    pulse: Float,
    energy: Float
) {

    Canvas(
        modifier = Modifier
            .height(165.dp)
            .padding(horizontal = 42.dp)
    ) {

        val cx = size.width / 2f
        val cy = size.height * 0.54f

        val logoWidth = min(size.width * 0.56f, 180.dp.toPx())
        val radius = logoWidth * 0.31f

        val left = cx - radius
        val right = cx + radius
        val bottom = cy + radius * 0.76f
        val top = cy - radius * 0.92f

        val goldWidth = 13.dp.toPx()
        val blueWidth = 13.dp.toPx()

        /*
         * ARCO DORADO
         *
         * Reproduce la forma semicircular del logotipo oficial:
         * arco superior + pequeñas terminaciones verticales.
         */

        val arcStart = 180f
        val arcSweep = 180f * archProgress

        drawArc(
            color = ValtisGold.copy(alpha = 0.14f * pulse),
            startAngle = arcStart,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(
                radius * 2f,
                radius * 1.55f
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = goldWidth * 2.2f,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = ValtisGold.copy(alpha = archProgress),
            startAngle = arcStart,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(
                radius * 2f,
                radius * 1.55f
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = goldWidth,
                cap = StrokeCap.Round
            )
        )

        if (archProgress > 0.01f) {

            val leftVerticalProgress =
                max(0f, min(1f, (archProgress - 0.70f) / 0.30f))

            val rightVerticalProgress =
                max(0f, min(1f, (archProgress - 0.78f) / 0.22f))

            drawLine(
                color = ValtisGold.copy(alpha = leftVerticalProgress),
                start = Offset(
                    left,
                    cy + radius * 0.04f
                ),
                end = Offset(
                    left,
                    bottom
                ),
                strokeWidth = goldWidth,
                cap = StrokeCap.Butt
            )

            drawLine(
                color = ValtisGold.copy(alpha = rightVerticalProgress),
                start = Offset(
                    right,
                    cy + radius * 0.04f
                ),
                end = Offset(
                    right,
                    bottom
                ),
                strokeWidth = goldWidth,
                cap = StrokeCap.Butt
            )
        }

        /*
         * V / CHECK AZUL
         */

        val checkStart = Offset(
            cx - radius * 0.34f,
            cy - radius * 0.48f
        )

        val checkBottom = Offset(
            cx - radius * 0.02f,
            bottom - radius * 0.02f
        )

        val checkEnd = Offset(
            cx + radius * 0.38f,
            cy - radius * 0.46f
        )

        drawAnimatedSegment(
            start = checkStart,
            end = checkBottom,
            progress = checkProgress,
            color = ValtisLogoBlue,
            glow = ValtisWhite.copy(alpha = 0.25f * pulse),
            width = blueWidth
        )

        drawAnimatedSegment(
            start = checkBottom,
            end = checkEnd,
            progress = checkProgress,
            color = ValtisLogoBlue,
            glow = ValtisWhite.copy(alpha = 0.25f * pulse),
            width = blueWidth
        )

        /*
         * FLECHA ASCENDENTE
         */

        val arrowTip = Offset(
            cx + radius * 0.95f,
            top - radius * 0.48f
        )

        val arrowBase = checkEnd

        drawAnimatedSegment(
            start = arrowBase,
            end = arrowTip,
            progress = arrowProgress,
            color = ValtisLogoBlue,
            glow = ValtisWhite.copy(alpha = 0.35f * pulse),
            width = blueWidth
        )

        /*
         * CABEZA DE LA FLECHA
         */

        if (arrowProgress > 0.75f) {

            val headProgress =
                min(1f, (arrowProgress - 0.75f) / 0.25f)

            val headSize = radius * 0.27f

            val leftHead = Offset(
                arrowTip.x - headSize,
                arrowTip.y + headSize * 0.58f
            )

            val rightHead = Offset(
                arrowTip.x - headSize * 0.08f,
                arrowTip.y + headSize * 0.92f
            )

            drawLine(
                color = ValtisLogoBlue.copy(alpha = headProgress),
                start = arrowTip,
                end = leftHead,
                strokeWidth = blueWidth * headProgress,
                cap = StrokeCap.Square
            )

            drawLine(
                color = ValtisLogoBlue.copy(alpha = headProgress),
                start = arrowTip,
                end = rightHead,
                strokeWidth = blueWidth * headProgress,
                cap = StrokeCap.Square
            )

            /*
             * Destello premium en la punta.
             */

            drawCircle(
                color = ValtisGold.copy(
                    alpha = 0.18f * pulse * headProgress
                ),
                radius = radius * 0.25f,
                center = arrowTip
            )

            drawCircle(
                color = ValtisWhite.copy(
                    alpha = 0.9f * headProgress
                ),
                radius = radius * 0.045f,
                center = arrowTip
            )
        }

        /*
         * PEQUEÑO SEGMENTO SUPERIOR IZQUIERDO
         * presente en el logotipo oficial.
         */

        if (accentProgress > 0f) {

            val accentStart = Offset(
                cx - radius * 0.78f,
                top + radius * 0.10f
            )

            val accentEnd = Offset(
                cx - radius * 0.57f,
                top + radius * 0.18f
            )

            drawLine(
                color = ValtisLogoBlue.copy(alpha = accentProgress),
                start = accentStart,
                end = accentEnd,
                strokeWidth = blueWidth,
                cap = StrokeCap.Square
            )
        }

        /*
         * Línea de energía que recorre la zona inferior
         * del emblema.
         */

        val sweepX =
            left + (right - left) * energy

        drawCircle(
            color = ValtisGold.copy(alpha = 0.30f * pulse),
            radius = 5.dp.toPx(),
            center = Offset(
                sweepX,
                bottom + radius * 0.23f
            )
        )

        drawLine(
            color = ValtisGold.copy(alpha = 0.18f * pulse),
            start = Offset(
                left - radius * 0.35f,
                bottom + radius * 0.23f
            ),
            end = Offset(
                right + radius * 0.35f,
                bottom + radius * 0.23f
            ),
            strokeWidth = 1.2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAnimatedSegment(
    start: Offset,
    end: Offset,
    progress: Float,
    color: Color,
    glow: Color,
    width: Float
) {

    val p = min(1f, max(0f, progress))

    val current = Offset(
        x = start.x + (end.x - start.x) * p,
        y = start.y + (end.y - start.y) * p
    )

    if (p <= 0f) return

    drawLine(
        color = glow,
        start = start,
        end = current,
        strokeWidth = width * 2.5f,
        cap = StrokeCap.Round
    )

    drawLine(
        color = color.copy(alpha = p),
        start = start,
        end = current,
        strokeWidth = width,
        cap = StrokeCap.Round
    )
}

@Composable
private fun PoweredByMultiservicios(
    progress: Float,
    pulse: Float
) {

    Canvas(
        modifier = Modifier
            .height(30.dp)
            .padding(horizontal = 35.dp)
            .alpha(progress)
    ) {

        val centerY = size.height / 2f
        val centerX = size.width / 2f

        val lineLength = size.width * 0.19f
        val gap = size.width * 0.06f

        drawLine(
            color = ValtisGold.copy(alpha = 0.65f * pulse),
            start = Offset(
                centerX - gap - lineLength,
                centerY
            ),
            end = Offset(
                centerX - gap,
                centerY
            ),
            strokeWidth = 1.2.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = ValtisGold.copy(alpha = 0.65f * pulse),
            start = Offset(
                centerX + gap,
                centerY
            ),
            end = Offset(
                centerX + gap + lineLength,
                centerY
            ),
            strokeWidth = 1.2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    Text(
        text = "Powered by Multiservicios",
        modifier = Modifier
            .alpha(progress)
            .padding(top = 0.dp),
        color = ValtisGray.copy(alpha = 0.92f),
        fontSize = 12.sp,
        letterSpacing = 0.8.sp
    )
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
