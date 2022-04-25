package com.iade.streetart.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
  primary = Blue200,
  primaryVariant = Blue700,
  secondary = Blue700,
  onPrimary = Black,
  onSecondary = White,
//    secondaryVariant = Red500,
//    background = Red500,
//    surface = Red500,
//    error = Red500,

//    onBackground = White,
//    onSurface = White,
//    onError = Red500
)

private val LightColorPalette = lightColors(
  primary = Blue500,
  primaryVariant = Blue700,
  secondary = Blue700,
//    secondaryVariant = Red500,
    background = Blue50,
//    surface = Red500,
//    error = Red500,
    onPrimary = White,
    onSecondary = White,
//    onBackground = Red500,
//    onSurface = Red500,
//    onError = Red500

  /* Other default colors to override
  background = Color.White,
  surface = Color.White,
  onPrimary = Color.White,
  onSecondary = Color.Black,
  onBackground = Color.Black,
  onSurface = Color.Black,
  */
)

@Composable
fun StreetArtTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val systemUiController = rememberSystemUiController()
  val colors = if (darkTheme) DarkColorPalette else LightColorPalette

  systemUiController.setSystemBarsColor(color = if (darkTheme) OffBlack else Blue700)

  MaterialTheme(
    colors = colors,
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}