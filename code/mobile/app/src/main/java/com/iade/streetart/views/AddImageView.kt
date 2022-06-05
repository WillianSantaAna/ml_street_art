package com.iade.streetart.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel

@Composable
fun AddImageViewState(
  navController: NavController,
  imageViewModel: ImageViewModel,
  streetArtViewModel: StreetArtViewModel
) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val focusManager = LocalFocusManager.current
  var email by rememberSaveable { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }
  
  AddImageView(
    scaffoldState = scaffoldState,
    focusManager = focusManager,
    navigateBackClick = { navController.popBackStack() }
  )
}

@Composable
fun AddImageView(
  scaffoldState: ScaffoldState,
  focusManager: FocusManager,
  navigateBackClick: () -> Unit,
  ) {
  
  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = {
            navigateBackClick()
          }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
          }
        },
        title = { Text(text = "Add Image") },
        elevation = 4.dp,
      )
    },
    content = { paddingValues ->
      Column(
        modifier = Modifier.padding(paddingValues)
      ) {
        Text(text = "addImages temp")
      }
    }
  )
}