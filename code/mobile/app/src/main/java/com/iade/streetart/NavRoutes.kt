package com.iade.streetart

sealed class NavRoutes(val route: String) {
  object MapView: NavRoutes("map")
  object HomeView: NavRoutes("home")
  object LoginView: NavRoutes("login")
  object SignInView: NavRoutes("signIn")
  object CameraView: NavRoutes("camera")
  object SearchView: NavRoutes("search")
  object ImagePreviewView: NavRoutes("preview")
  object SingleStreetArtView: NavRoutes("streetArt/{streetArtId}")
}
