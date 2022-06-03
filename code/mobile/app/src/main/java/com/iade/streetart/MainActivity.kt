package com.iade.streetart

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.iade.streetart.models.UserLocalDataStore
import com.iade.streetart.ui.theme.StreetArtTheme
import com.iade.streetart.viewModels.UserViewModel

private val Context.dataStore by preferencesDataStore(
  name = "user"
)

class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      Log.i("Kilo", "Permission granted")
    } else
      Log.i("Kilo", "Permission denied")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val navController = rememberNavController()
      val userLocalDataStore = remember { UserLocalDataStore(dataStore) }
      val userViewModel = UserViewModel(userLocalDataStore)

      StreetArtTheme {
        NavHost(navController = navController, userViewModel)
      }
    }

    requestPermission(Manifest.permission.CAMERA)
    requestPermission(Manifest.permission.INTERNET)
//    requestPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
  }

  private fun requestPermission(permission: String) {
    when {
      ContextCompat.checkSelfPermission(
        this,
        permission
      ) == PackageManager.PERMISSION_GRANTED -> {
        Log.i("Kilo", "Permission previously granted")
      }

      ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        permission
      ) ->
        Log.i("Kilo", "Show camera permissions dialog")

      else ->
        requestPermissionLauncher.launch(permission)
    }
  }
}


