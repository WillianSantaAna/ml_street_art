package com.iade.streetart

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iade.streetart.viewModels.UserViewModel
import com.iade.streetart.views.*

@Composable
fun NavHost(navController: NavHostController, userViewModel: UserViewModel) {

  LaunchedEffect(true) {
    val res = userViewModel.isUserLoggedIn()

    if (res) {
      navController.navigate("map") {
        popUpTo("home") { inclusive = true }
      }
    }
  }

  NavHost(
    navController = navController,
    startDestination = NavRoutes.HomeView.route
  ) {
    composable(NavRoutes.HomeView.route) {
      HomeView(navController)
    }

    composable(NavRoutes.LoginView.route) {
      LoginViewState(navController, userViewModel)
    }

    composable(NavRoutes.SignInView.route) {
      SignInViewState(navController, userViewModel)
    }

    composable(NavRoutes.MapView.route) {
      MapView(navController, userViewModel)
    }

    composable(NavRoutes.CameraView.route) {
      CameraView()
    }
  }
}