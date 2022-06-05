package com.iade.streetart

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iade.streetart.models.UserLocalDataStore
import com.iade.streetart.ui.theme.StreetArtTheme
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
      val navController = rememberNavController()
      val userLocalDataStore = remember { UserLocalDataStore(dataStore) }
      val userViewModel = UserViewModel(userLocalDataStore)
      val streetArtViewModel = StreetArtViewModel()

      LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Default).launch {
          streetArtViewModel.fetchStreetArts()
        }
      }
      
      StreetArtTheme {
        NavHost(
          navController = navController,
          userViewModel =  userViewModel,
          streetArtViewModel = streetArtViewModel,
        )
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      requestPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
    }

    requestPermission(Manifest.permission.CAMERA)
    requestPermission(Manifest.permission.INTERNET)
    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
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


