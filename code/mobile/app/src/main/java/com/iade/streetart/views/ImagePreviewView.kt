package com.iade.streetart.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

  ImagePreviewView(
    imageUri = imageUri.value,
    clearImageUri = {
      navController.popBackStack()
      imageViewModel.clearImageUri()
    },
    onPredictClick = { onPredictClick() }
  )

  if (openDialog) {
    ResultDialog(
      predictResult = predictResult,
      closeDialog = { openDialog = false }
    )
  }
}

@Composable
fun ImagePreviewView(
  imageUri: Uri,
  clearImageUri: () -> Unit,
  onPredictClick: () -> Unit,
) {
  Box(modifier = Modifier) {
    Image(
      modifier = Modifier.fillMaxSize(),
      painter = rememberAsyncImagePainter(imageUri),
      contentDescription = "Captured image"
    )

    Row (
      modifier = Modifier.align(Alignment.BottomCenter),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      Button(
        onClick = { onPredictClick() }) {
        Text(text = "Classify image")
      }

      Button(
        onClick = { clearImageUri() }) {
        Text(text = "Remove image")
      }
    }
  }
}

@Composable
fun ResultDialog(
  predictResult: PredictResult,
  closeDialog: () -> Unit
) {
  AlertDialog(
      onDismissRequest = closeDialog,
      title = { Text(text = "Author") },
      text = {
        val author = predictResult.author
        val prediction = String.format("%.2f", predictResult.prediction * 100)

        Log.i("Prediction", predictResult.toString())
        Text(text = "$author: $prediction%")
      },
      buttons = {
        TextButton(onClick = closeDialog) {
          Text(text = "Close!")
        }
      }
    )
}