package com.iade.streetart.views

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.iade.streetart.models.StreetArt
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Composable
fun AddImageViewState(
  navController: NavController,
  userViewModel: UserViewModel,
  imageViewModel: ImageViewModel,
  streetArtViewModel: StreetArtViewModel
) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val imageUri = imageViewModel.imageUri
  val streetArts = streetArtViewModel.streetArts

  fun addImage(streetArtId: Int) {
    scope.launch {
      try {
        if (streetArtId == 0) {
          scaffoldState.snackbarHostState.showSnackbar("Please select a street art")
          return@launch
        }

        val file = imageViewModel.uriToFile()
        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

        val imageUrl = imageViewModel.addImage(streetArtId, userViewModel.user.usr_id, body)

        if (imageUrl.isNotEmpty()) {
          scaffoldState.snackbarHostState.showSnackbar("New image add successfully")

          streetArtViewModel.fetchStreetArts()
          imageViewModel.clearImageUri()

          navController.navigate("map") {
            popUpTo("map") { inclusive = true }
          }
        } else {
          scaffoldState.snackbarHostState.showSnackbar("Failed to upload new image")
        }
      } catch (ex: Exception) {
        scaffoldState.snackbarHostState.showSnackbar("Failed to upload new image")
      }
    }
  }
  
  AddImageView(
    scaffoldState = scaffoldState,
    imageUri = imageUri,
    streetArts = streetArts,
    addImage = { addImage(it) },
    navigateBackClick = { navController.popBackStack() }
  )
}

@Composable
fun AddImageView(
  scaffoldState: ScaffoldState,
  imageUri: Uri,
  streetArts: List<StreetArt>,
  addImage: (Int) -> Unit,
  navigateBackClick: () -> Unit,
  ) {

  var expand by remember { mutableStateOf(false) }
  var streetArtId by remember { mutableStateOf(0)}
  
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
        modifier = Modifier
          .fillMaxHeight()
          .padding(paddingValues)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Image(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 10.dp, bottom = 20.dp),
          painter = rememberAsyncImagePainter(imageUri),
          contentDescription = "Captured image"
        )

        Box(
          modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
        ) {
          Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = { expand = true }
          ) {
            Text(text = if (streetArtId == 0) "Select the street art" else "Selected")
          }

          DropdownMenu(
            modifier = Modifier
              .fillMaxHeight(0.4f)
              .fillMaxWidth(0.5f),
            expanded = expand,
            onDismissRequest = { expand = false }
          ) {
            streetArts.forEach { streetArt ->
              DropdownMenuItem(onClick = {
                streetArtId = streetArt.sta_id
                expand = false
              }) {
                Row {
                  AsyncImage(
                    modifier = Modifier
                      .height(40.dp)
                      .width(40.dp)
                      .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                      .data(streetArt.img_url)
                      .crossfade(true)
                      .allowHardware(false)
                      .build(),
                    contentDescription = "Street Art Image",
                    contentScale = ContentScale.Crop,
                  )

                  Spacer(modifier = Modifier.padding(10.dp))

                  Text(text = streetArt.sta_artist)
                }
              }
            }
          }
        }

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = {
            addImage(streetArtId)
          }
        ) {
          Text(text = "Add Street Art")
        }
      }
    }
  )
}