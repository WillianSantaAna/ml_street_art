package com.iade.streetart.views

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.iade.streetart.viewModels.ImageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraView(
  navController: NavController,
  imageViewModel: ImageViewModel,
) {

  Box {
    CameraCapture(
      onImageFile = { file ->
        imageViewModel.setImageUri(file.toUri())
        navController.navigate("preview")
        Log.i("imageUri", imageViewModel.imageUri.toString())
      }
    )

  }
}

@Composable
fun CameraPreview(
  modifier: Modifier = Modifier,
  scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
  onUseCase: (UseCase) -> Unit = { }
) {

  AndroidView(
    modifier = modifier,
    factory = { context ->
      val previewView = PreviewView(context).apply {
        this.scaleType = scaleType
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
      }

      onUseCase(Preview.Builder().build()
        .also {
          it.setSurfaceProvider(previewView.surfaceProvider)
        }
      )

      previewView
    }
  )
}

@Composable
fun CameraCapture(
  modifier: Modifier = Modifier,
  onImageFile: (File) -> Unit = { },
  cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {

  Box(modifier = modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
    val imageCaptureUseCase by remember {
      mutableStateOf(
        ImageCapture.Builder()
          .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
          .build()
      )
    }

    CameraPreview(
      modifier = Modifier.fillMaxSize(),
      onUseCase = {
        previewUseCase = it
      }
    )

    IconButton(modifier = Modifier
      .padding(10.dp)
      .size(64.dp)
      .padding(1.dp)
      .border(1.dp, Color.White, CircleShape)
      .align(Alignment.BottomCenter),
      onClick = {
        coroutineScope.launch {
          onImageFile(imageCaptureUseCase.takePicture(context.executor))
        }
      }
    ) {
      Icon(
        modifier = Modifier.size(64.dp),
        imageVector = Icons.Sharp.Lens,
        contentDescription = "Take Picture",
        tint = Color.White
      )
    }

    LaunchedEffect(previewUseCase) {
      val cameraProvider = context.getCameraProvider()
      try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
          lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
        )
      } catch (ex: Exception) {
        Log.e("CameraCapture", "Failed to bind camera use case", ex)
      }
    }

  }
}

suspend fun ImageCapture.takePicture(executor: Executor): File {
  val photoFile = withContext(Dispatchers.IO) {
    kotlin.runCatching {
      File.createTempFile("image", "jpg")
    }.getOrElse { ex ->
      Log.e("TakePicture", "Failed to create temporary file", ex)
      File("/dev/null")
    }
  }

  return suspendCoroutine { continuation ->
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {

      override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        continuation.resume(photoFile)
      }

      override fun onError(ex: ImageCaptureException) {
        Log.e("TakePicture", "Image capture failed", ex)
        continuation.resumeWithException(ex)
      }
    })
  }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine {
  continuation -> ProcessCameraProvider.getInstance(this).also {
    cameraProvider -> cameraProvider.addListener({
      continuation.resume(cameraProvider.get())
    }, executor)
  }
}

val Context.executor: Executor
  get() = ContextCompat.getMainExecutor(this)
