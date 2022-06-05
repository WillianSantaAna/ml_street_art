package com.iade.streetart.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.iade.streetart.models.StreetArt
import com.iade.streetart.models.User
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun MapViewState(
  navController: NavController,
  userViewModel: UserViewModel,
  streetArtViewModel: StreetArtViewModel
) {
  val scaffoldState = rememberScaffoldState()
  val scope = rememberCoroutineScope()
  val user = userViewModel.user
  val streetArts = streetArtViewModel.streetArts

  fun openSideBar() {
    scope.launch {
      scaffoldState.drawerState.open()
    }
  }

  fun logout() {
    navController.navigate("home") {
      userViewModel.logout()
      popUpTo("map") { inclusive = true }
    }
  }

  fun streetArtNav(id: Int): Boolean {
    scope.launch {
      navController.navigate("streetArt/${id}")
    }

    return true
  }

  fun pageNav(location: String) {
    scope.launch {
      navController.navigate(location)
    }
  }

  MapView(
    scaffoldState = scaffoldState,
    user = user,
    streetArts = streetArts,
    openSideBar = { openSideBar() },
    logout = { logout() },
    streetArtNav = { streetArtNav(it) },
    pageNav = { pageNav(it) },
  )
}

@Composable
fun MapView(
  scaffoldState: ScaffoldState,
  user: User,
  streetArts: List<StreetArt>,
  openSideBar: () -> Unit,
  logout: () -> Unit,
  streetArtNav: (id: Int) -> Boolean,
  pageNav: (location: String) -> Unit,
  ) {

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = openSideBar) {
            Icon(imageVector = Icons.Filled.Menu, contentDescription = "menu")
          }
        },
        title = { Text(text = "Street Art Lisbon") },
        elevation = 4.dp,
        actions = {
          IconButton(onClick = { pageNav("search/ ") }) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
          }
        }
      )
    },
    floatingActionButtonPosition = FabPosition.Center,
    floatingActionButton = {
      FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(4.dp),
        onClick = { pageNav("camera") },
      ) {
        Icon(
          imageVector = Icons.Filled.CameraAlt,
          contentDescription = "Camera",
          tint = Color.White
        )
      }
    },
    drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
    drawerContent = {
      Column(
        modifier = Modifier
          .padding(horizontal = 10.dp, vertical = 20.dp)
          .align(Alignment.Start)
      ) {
        Icon(
          modifier = Modifier.size(64.dp),
          imageVector = Icons.Filled.AccountCircle,
          contentDescription = "User icon"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(fontSize = 25.sp, text = user.usr_name.capitalize(Locale.current))
        Text(text = user.usr_type.capitalize(Locale.current))

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
          modifier = Modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
          onClick = { pageNav("search/ ") }
        ) {
          Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "Search")
          Spacer(modifier = Modifier.weight(1f))
        }

        TextButton(
          modifier = Modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
          onClick = { pageNav("camera") }
        ) {
          Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Camera")
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "Camera")
          Spacer(modifier = Modifier.weight(1f))
        }

        TextButton(
          modifier = Modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
          onClick = { logout() }
        ) {
          Icon(imageVector = Icons.Filled.Logout, contentDescription = "logout")
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "Logout")
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    },
    content = { paddingValues ->
      val lisbon = LatLng(38.71667, -9.13333)
      val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lisbon, 11f)
      }

      GoogleMap(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        cameraPositionState = cameraPositionState
      ) {
        streetArts.map { streetArt ->
          val (lat, lng) = streetArt.sta_coords

          Marker(
            state = MarkerState(position = LatLng(lat, lng)),
            onClick = { streetArtNav(streetArt.sta_id) }
          )
        }
      }
    },
  )
}
