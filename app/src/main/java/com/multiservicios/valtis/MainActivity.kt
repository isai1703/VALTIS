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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val ValtisBlue = Color(0xFF0B304A)
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

    var showSplash by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(4700)
        showSplash = false
    }

    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(
            animationSpec = tween(350)
        ),
        exit = fadeOut(
            animationSpec = tween(800)
        )
    ) {
        ValtisSplash()
    }

    if (!showSplash) {
        ValtisHomePlaceholder()
    }
}

@Composable
fun ValtisSplash() {

    var startAnimation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "logoAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.88f,
        animationSpec = tween(
            durationMillis = 1250,
            easing = FastOutSlowInEasing
        ),
        label = "logoScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ValtisBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

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
}

@Composable
private fun ValtisHomePlaceholder() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ValtisBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "VALTIS",
            color = ValtisWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Dashboard",
            color = ValtisWhite,
            fontSize = 18.sp
        )
    }
}
