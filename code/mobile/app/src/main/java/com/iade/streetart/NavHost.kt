package com.iade.streetart

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iade.streetart.viewModels.UserViewModel
import com.iade.streetart.views.*

@Composable
fun NavHost(navController: NavHostController) {
  val userViewModel: UserViewModel = viewModel()

  NavHost(
    navController = navController,
    startDestination = NavRoutes.HomeView.route
  ) {
    composable(NavRoutes.HomeView.route) {
      HomeView(navController)
    }

    composable(NavRoutes.LoginView.route) {
      LoginView(navController, userViewModel)
    }

    composable(NavRoutes.SignInView.route) {
      SignInView(navController, userViewModel)
    }

    composable(NavRoutes.MapView.route) {
      MapView(navController, userViewModel)
    }

    composable(NavRoutes.CameraView.route) {
      CameraView()
    }
  }
}