package com.iade.streetart.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.iade.streetart.models.PredictResult
import com.iade.streetart.viewModels.ImageViewModel
import com.iade.streetart.viewModels.StreetArtViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Composable
fun ImagePreviewViewState(
  navController: NavController,
  imageViewModel: ImageViewModel,
  streetArtViewModel: StreetArtViewModel
) {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val imageUri = rememberSaveable { mutableStateOf(imageViewModel.imageUri) }
  var openDialog by rememberSaveable { mutableStateOf(false) }
  var predictResult by remember { mutableStateOf(PredictResult("", 0.0)) }

  fun onPredictClick() {
    try {
      Log.i("uri", imageUri.value.encodedPath!!)

      val file = imageViewModel.uriToFile()
      val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
      val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

      scope.launch {
        val result = streetArtViewModel.predict(body)

        if (result.isNotEmpty()) {
          predictResult = result.find { it.prediction > 0.33 }!!
          openDialog = true
        }
      }
    } catch (e: Exception) {
      Log.e("error", e.message ?: "")
    }
  }

  fun searchAuthor(author: String) {
    openDialog = false
    navController.navigate("search/$author")
  }

  ImagePreviewView(
    imageUri = imageUri.value,
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
          .padding(paddingValues),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Image(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
          painter = rememberAsyncImagePainter(imageUri),
          contentScale = ContentScale.FillWidth,
          contentDescription = "Captured image"
        )

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = { onPredictClick() }) {
          Text(text = "Classify image")
        }

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = { navigateTo("addStreetArt") }) {
          Text(text = "Add Street Art")
        }

//        Button(
//          modifier = Modifier.fillMaxWidth(0.5f),
//          onClick = { navigateTo("addImage") }) {
//          Text(text = "Add Image")
//        }
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
  AlertDialog(
      onDismissRequest = closeDialog,
      title = { Text(text = "Author") },
      text = {
        val author = predictResult.author
        val prediction = String.format("%.2f", predictResult.prediction * 100)

        Log.i("Prediction", predictResult.toString())
        TextButton(onClick = { searchAuthor(author) }) {
          Text(text = "$author: $prediction%")
        }
      },
      buttons = {
        TextButton(onClick = closeDialog) {
          Text(text = "Close!")
        }
      }
    )
}