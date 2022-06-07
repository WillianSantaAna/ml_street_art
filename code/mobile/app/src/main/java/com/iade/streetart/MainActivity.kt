package com.iade.streetart

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iade.streetart.models.UserLocalDataStore
import com.iade.streetart.ui.theme.StreetArtTheme
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel

private val Context.dataStore by preferencesDataStore(
  name = "user"
)

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val navController = rememberNavController()
      val userLocalDataStore = remember { UserLocalDataStore(dataStore) }
      val userViewModel = UserViewModel(userLocalDataStore)
      val imageViewModel = ImageViewModel()
      val streetArtViewModel = StreetArtViewModel()

      val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
          Manifest.permission.CAMERA,
          Manifest.permission.INTERNET,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
        )
      )
      
      StreetArtTheme {
        if (multiplePermissionsState.allPermissionsGranted) {
          NavHost(
            navController = navController,
            userViewModel =  userViewModel,
            imageViewModel = imageViewModel,
            streetArtViewModel = streetArtViewModel,
          )
        } else {
          SideEffect {
            multiplePermissionsState.launchMultiplePermissionRequest()
          }
        }
      }
    }
  }
}
