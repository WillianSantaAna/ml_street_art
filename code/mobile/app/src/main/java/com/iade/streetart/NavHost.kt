package com.iade.streetart

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import com.iade.streetart.views.*

@Composable
fun NavHost(
  navController: NavHostController,
  userViewModel: UserViewModel,
  imageViewModel: ImageViewModel,
  streetArtViewModel: StreetArtViewModel,
) {

  NavHost(
    navController = navController,
    startDestination = NavRoutes.HomeView.route
  ) {
    composable(NavRoutes.HomeView.route) {
      HomeView(navController, userViewModel)
    }

    composable(NavRoutes.LoginView.route) {
      LoginViewState(navController, userViewModel)
    }

    composable(NavRoutes.SignUpView.route) {
      SignUpViewState(navController, userViewModel)
    }

    composable(NavRoutes.MapView.route) {
      MapViewState(navController, userViewModel, streetArtViewModel)
    }

    composable(NavRoutes.CameraView.route) {
      CameraView(navController, imageViewModel)
    }

    composable(NavRoutes.ImagePreviewView.route) {
      ImagePreviewViewState(navController, userViewModel, imageViewModel, streetArtViewModel)
    }

    composable(NavRoutes.AddImageView.route) {
      AddImageViewState(navController, userViewModel, imageViewModel, streetArtViewModel)
    }

    composable(NavRoutes.AddStreetArtView.route) {
      AddStreetArtViewState(navController, userViewModel, imageViewModel, streetArtViewModel)
    }

    composable(NavRoutes.SearchView.route) { backStackEntry ->
      SearchViewState(
        navController,
        streetArtViewModel,
        authorName = backStackEntry.arguments?.getString("authorName") ?: ""
      )
    }

    composable(NavRoutes.SingleStreetArtView.route) { backStackEntry ->
      SingleStreetArtViewState(
        navController = navController,
        streetArtViewModel = streetArtViewModel,
        streetArtId = backStackEntry.arguments?.getString("streetArtId") ?: "0"
      )
    }
  }
}