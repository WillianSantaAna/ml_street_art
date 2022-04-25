package com.iade.streetart

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

class AppState(
  val scaffoldState: ScaffoldState,
  val navController: NavHostController,
  val coroutineScope: CoroutineScope,
  private val resources: Resources,
)

@Composable
fun rememberAppState(
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  navController: NavHostController = rememberNavController(),
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  resources: Resources = LocalContext.current.resources,
) = remember(scaffoldState, navController, coroutineScope, resources) {
  AppState(scaffoldState, navController, coroutineScope, resources)
}