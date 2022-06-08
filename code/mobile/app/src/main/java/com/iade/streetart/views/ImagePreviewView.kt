package com.iade.streetart.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.iade.streetart.models.PredictResult
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Composable
fun ImagePreviewViewState(
  navController: NavController,
  userViewModel: UserViewModel,
  imageViewModel: ImageViewModel,
  streetArtViewModel: StreetArtViewModel
) {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val imageUri = imageViewModel.imageUri
  var openDialog by rememberSaveable { mutableStateOf(false) }
  var predictResult by remember { mutableStateOf(PredictResult("", 0.0)) }

  fun onPredictClick() {
    scope.launch {

      try {
        val file = imageViewModel.uriToFile()
        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

        val result = streetArtViewModel.predict(body)

        if (result.isNotEmpty()) {
          result.forEach {
            if (it.prediction > predictResult.prediction) {
              predictResult = it
            }
          }
          openDialog = true
        }
      } catch (e: Exception) {
        Log.e("error", e.message ?: "")
        scaffoldState.snackbarHostState.showSnackbar("Failed to classify the image.")
      }
    }

  }

  fun searchAuthor(author: String) {
    openDialog = false
    navController.navigate("search/$author")
  }

  ImagePreviewView(
    imageUri = imageUri,
    userType = userViewModel.user.usr_type,
    scaffoldState = scaffoldState,
    navigateBackClick = {
      navController.popBackStack()
      imageViewModel.clearImageUri()
    },
    navigateTo = {
      navController.navigate(it)
    },
    onPredictClick = { onPredictClick() },
  )

  if (openDialog) {
    ResultDialog(
      predictResult = predictResult,
      closeDialog = { openDialog = false },
      searchAuthor = { searchAuthor(it) }
    )
  }
}

@Composable
fun ImagePreviewView(
  imageUri: Uri,
  userType: String,
  scaffoldState: ScaffoldState,
  navigateBackClick: () -> Unit,
  navigateTo: (String) -> Unit,
  onPredictClick: () -> Unit,
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
        title = { Text(text = "Image preview") },
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
            .height(500.dp)
            .padding(top = 10.dp, bottom = 20.dp),
          painter = rememberAsyncImagePainter(imageUri),
          contentDescription = "Captured image"
        )

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = { onPredictClick() }) {
          Text(text = "Discover the author")
        }

        if (userType != "user") {
          Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = { navigateTo("addStreetArt") }
          ) {
            Text(text = "Add Street Art")
          }

          Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = { navigateTo("addImage") }
          ) {
            Text(text = "Add Image")
          }
        }
      }
    }
  )
}

@Composable
fun ResultDialog(
  predictResult: PredictResult,
  closeDialog: () -> Unit,
  searchAuthor: (String) -> Unit,
) {
  val author = predictResult.author
  val prediction = String.format("%.2f", predictResult.prediction * 100)

  AlertDialog(
    onDismissRequest = closeDialog,
    title = { Text(text = "Author") },
    text = {
      Text(text = "$author: $prediction%")
    },
    buttons = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 15.dp)
          .padding(bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Button(onClick = { searchAuthor(author) }) {
          Text(text = "See More...")
        }

        TextButton(onClick = closeDialog) {
          Text(text = "Close!")
        }
      }
    }
  )
}