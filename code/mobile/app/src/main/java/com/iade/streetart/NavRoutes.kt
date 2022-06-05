package com.iade.streetart

sealed class NavRoutes(val route: String) {
  object MapView: NavRoutes("map")
  object HomeView: NavRoutes("home")
  object LoginView: NavRoutes("login")
  object SignUpView: NavRoutes("signUp")
  object CameraView: NavRoutes("camera")
  object AddImageView: NavRoutes("addImage")
  object ImagePreviewView: NavRoutes("preview")
  object AddStreetArtView: NavRoutes("addStreetArt")
  object SearchView: NavRoutes("search/{authorName}")
  object SingleStreetArtView: NavRoutes("streetArt/{streetArtId}")
}
