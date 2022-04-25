package com.iade.streetart.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.iade.streetart.R
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun MapView(navController: NavController, userViewModel: UserViewModel) {

  val scaffoldState = rememberScaffoldState()
  val scope = rememberCoroutineScope()
  val user = userViewModel.user

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
              scaffoldState.snackbarHostState.showSnackbar("Search Button Clicked!")
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
    drawerGesturesEnabled = true,
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
            userViewModel.logout()

            navController.navigate("home") {
              popUpTo("map") { inclusive = true }
          } }
        ) {
          Icon(imageVector = Icons.Filled.Logout, contentDescription = "logout")
          Text(text = "Logout")
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    },
    content = {
      Image(
        painter = painterResource(id = R.drawable.map),
        contentDescription = "map",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    },
  )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MapViewPreview () {
  MaterialTheme {
    MapView(rememberNavController(), viewModel())
  }
}