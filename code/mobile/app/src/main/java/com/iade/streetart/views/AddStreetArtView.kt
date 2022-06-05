package com.iade.streetart.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.iade.streetart.models.StreetArtPost
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.time.LocalDateTime

@Composable
fun AddStreetArtViewState(
  navController: NavController,
  userViewModel: UserViewModel,
  imageViewModel: ImageViewModel,
  streetArtViewModel: StreetArtViewModel
) {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val focusManager = LocalFocusManager.current
  val image = imageViewModel.imageUri
  var artist by rememberSaveable { mutableStateOf("") }
  var address by rememberSaveable { mutableStateOf("") }
  var coords by rememberSaveable { mutableStateOf("") }

  fun addStreetArt() {

    scope.launch {
      try {
        val streetArt = StreetArtPost(
          usr_id = userViewModel.user.usr_id,
          artist = artist,
          project = "none",
          year = LocalDateTime.now().year,
          credits = "none",
          address = address,
          coords = coords,
          status = "existente"
        )

        Log.i("streetArt", streetArt.toString())
        val streetArtId = streetArtViewModel.addStreetArt(streetArt)
        Log.i("streetArtId", streetArtId.toString())
        val file = imageViewModel.uriToFile()
        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

        val imageUrl = imageViewModel.addImage(streetArtId, userViewModel.user.usr_id, body)

        if (imageUrl.isNotEmpty()) {
          streetArtViewModel.fetchStreetArts()
          imageViewModel.clearImageUri()
          navController.navigate("map") {
            popUpTo("map") { inclusive = true }
          }
        } else {
          artist = ""
          address = ""
          coords = ""
          scaffoldState.snackbarHostState.showSnackbar("Failed to upload new street art")
        }
      } catch (e: Exception) {
        artist = ""
        address = ""
        coords = ""
        scaffoldState.snackbarHostState.showSnackbar("Failed to upload new street art")
      }
    }
  }

  AddStreetArtView(
    scaffoldState = scaffoldState,
    focusManager = focusManager,
    navigateBackClick = { navController.popBackStack() },
    addStreetArt = { addStreetArt() },
    image = image,
    artist = artist,
    onArtistChange = { artist = it },
    address = address,
    onAddressChange = { address = it },
    coords = coords,
    onCoordsChange = { coords = it },
  )
}

@Composable
fun AddStreetArtView(
  scaffoldState: ScaffoldState,
  focusManager: FocusManager,
  navigateBackClick: () -> Unit,
  addStreetArt: () -> Unit,
  image: Uri,
  artist: String,
  onArtistChange: (String) -> Unit,
  address: String,
  onAddressChange: (String) -> Unit,
  coords: String,
  onCoordsChange: (String) -> Unit,
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
        title = { Text(text = "Add Street Art") },
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
          painter = rememberAsyncImagePainter(image),
          contentDescription = "Captured image"
        )

        OutlinedTextField(
          value = artist,
          leadingIcon = { Icon(imageVector = Icons.Filled.People, contentDescription = "Artist") },
          onValueChange = onArtistChange,
          label = { Text(text = "Artist") },
          placeholder = { Text(text = "Enter the artist name") },
          keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
          )
        )

        OutlinedTextField(
          value = address,
          leadingIcon = { Icon(imageVector = Icons.Filled.Map, contentDescription = "Address") },
          onValueChange = onAddressChange,
          label = { Text(text = "Address") },
          placeholder = { Text(text = "Enter the street art address") },
          keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
          )
        )

        OutlinedTextField(
          value = coords,
          leadingIcon = { Icon(imageVector = Icons.Filled.Place, contentDescription = "Coords") },
          onValueChange = onCoordsChange,
          label = { Text(text = "Coords") },
          placeholder = { Text(text = "Ex. \"39.5,-9.52\"") },
          keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
          )
        )

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = {
            addStreetArt()
          }
        ) {
          Text(text = "Add Street Art")
        }
      }
    }
  )
}