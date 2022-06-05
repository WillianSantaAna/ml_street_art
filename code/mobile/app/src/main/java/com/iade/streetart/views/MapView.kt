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
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun MapView(
  navController: NavController,
  userViewModel: UserViewModel,
  streetArtViewModel: StreetArtViewModel
) {

  val scaffoldState = rememberScaffoldState()
  val scope = rememberCoroutineScope()
  val user = userViewModel.user
  val streetArts = streetArtViewModel.streetArts

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = {
            scope.launch {
              scaffoldState.drawerState.open()
            }
          }) {
            Icon(imageVector = Icons.Filled.Menu, contentDescription = "menu")
          }
        },
        title = { Text(text = "Street Art Lisbon") },
        elevation = 4.dp,
        actions = {
          IconButton(onClick = {
            scope.launch {
              navController.navigate("search")
            }
          }) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
          }
        }
      )
    },
    floatingActionButtonPosition = FabPosition.Center,
    floatingActionButton = {
      FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(4.dp),
        onClick = {
          scope.launch {
            navController.navigate("camera")
          }
        },
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
          onClick = { /*TODO*/ }
        ) {
          Icon(imageVector = Icons.Filled.Image, contentDescription = "gallery")
          Text(text = "Gallery")
          Spacer(modifier = Modifier.weight(1f))
        }

        TextButton(
          modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
          onClick = { /*TODO*/ }
        ) {
          Icon(imageVector = Icons.Filled.Settings, contentDescription = "settings")
          Text(text = "Settings")
          Spacer(modifier = Modifier.weight(1f))
        }

        TextButton(
          modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
          onClick = {

            navController.navigate("home") {
              userViewModel.logout()
              popUpTo("map") { inclusive = true }
            }
          }
        ) {
          Icon(imageVector = Icons.Filled.Logout, contentDescription = "logout")
          Text(text = "Logout")
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    },
    content = { paddingValues ->
      val lisbon = LatLng(38.72821, -9.14064)
      val lisbon2 = LatLng(38.73, -9.135)
      val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lisbon, 15f)
      }

      GoogleMap(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        cameraPositionState = cameraPositionState
      ) {

//        MarkerInfoWindow(
//          state = MarkerState(position = lisbon2),
//          title = "Lisbon2",
//          snippet = "Marker in Lisbon2",
////          icon = BitmapDescriptorFactory.fromBitmap(image)
//        ) {
//          AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//              .data("https://res.cloudinary.com/fgsilva/image/upload/v1646029378/07-file-upload/tmp-1-1646029377762_koopnl.jpg")
//              .crossfade(true)
//              .allowHardware(false)
//              .build(),
//            contentDescription = "test",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//              .size(75.dp)
//              .clip(RoundedCornerShape(percent = 100))
//              .padding(paddingValues)
//          )
//        }
        streetArts.map { streetArt ->
          val (lat, lng) = streetArt.sta_coords

          Marker(
            state = MarkerState(position = LatLng(lat, lng)),
            onClick = {
              scope.launch {
                navController.navigate("streetArt/${streetArt.sta_id}")
              }

              true
            }
          )
        }

      }
    },
  )
}
