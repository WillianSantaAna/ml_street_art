package com.iade.streetart

sealed class NavRoutes(val route: String) {
  object HomeView: NavRoutes("home")
  object LoginView: NavRoutes("login")
  object SignInView: NavRoutes("signIn")
  object MapView: NavRoutes("map")
  object CameraView: NavRoutes("camera")
}
