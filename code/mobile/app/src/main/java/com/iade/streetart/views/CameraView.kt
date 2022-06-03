package com.iade.streetart.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import androidx.core.net.toFile
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.iade.streetart.models.PredictResult
import com.iade.streetart.models.RetrofitHelper
import com.iade.streetart.models.StreetArtApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")

@Composable
fun CameraView(modifier: Modifier = Modifier) {
  var showGallerySelect by remember { mutableStateOf(false) }
  var openDialog by remember { mutableStateOf(false) }
  val emptyImageUri = Uri.parse("file://dev/null")
  var imageUri by remember { mutableStateOf(emptyImageUri) }
  var predictResult by remember { mutableStateOf(PredictResult("", 0.0)) }
  val scope = rememberCoroutineScope()

  when {
    imageUri != emptyImageUri -> {
      Box(modifier = modifier) {
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
            onClick = {
              try {
                Log.i("uri", imageUri.encodedPath!!)
                val streetArtApi = RetrofitHelper.getInstance().create(StreetArtApi::class.java)
                val file = imageUri.toFile()

//                val file = File.createTempFile("image", "jpg")
                val bitmap = decodeBitmapFromFile(imageUri.path!!, 1024, 1024)

                Log.i("file path", file.path)


                bitmapToFile(bitmap, file)

                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
                scope.launch {
                  val result = streetArtApi.predict(body)

                  if (result.isSuccessful) {
                    predictResult = result.body()?.find { it.prediction > 0.33 }!!
                    openDialog = true
                  }
                }
              } catch (e: Exception) {
                Log.e("error", e.message ?: "")
              }

            }) {
            Text(text = "Classify image")
          }

          Button(
            onClick = {
              imageUri = emptyImageUri
            }) {
            Text(text = "Remove image")
          }
        }
      }
    }
    showGallerySelect -> {
      GallerySelect(
//      modifier = modifier,
        onImageUri = { uri ->
          showGallerySelect = false
          imageUri = uri
        }
      )
    }
    else -> {
      Box(modifier = modifier) {
        CameraCapture(
          modifier = modifier,
          onImageFile = { file ->
            imageUri = file.toUri()
            Log.i("imageUri", imageUri.toString())
          }
        )

        Button(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(4.dp),
          onClick = {
            showGallerySelect = true
          }
        ) {
          Text("Select from Gallery")
        }
      }
    }
  }

  if(openDialog) {
    AlertDialog(
      onDismissRequest = { openDialog = false },
      title = { Text(text = "Author") },
      text = {
        val author = predictResult.author
        val prediction = String.format("%.2f", predictResult.prediction * 100)

        Log.i("Prediction", predictResult.toString())
        Text(text = "$author: $prediction%")
      },
      buttons = {
        TextButton(onClick = { openDialog = false }) {
          Text(text = "Close!")
        }
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

@Composable
fun GallerySelect(
//  modifier: Modifier = Modifier,
  onImageUri: (Uri) -> Unit = { }
) {
//  val context = LocalContext.current
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
    onResult = { uri: Uri? ->
      onImageUri(uri ?: EMPTY_IMAGE_URI)
    }
  )

  @Composable
  fun LaunchGallery() {
    SideEffect {
      launcher.launch("image/*")
    }
  }

  LaunchGallery()
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

fun decodeBitmapFromFile(
  imgPath: String,
  reqWidth: Int,
  reqHeight: Int
): Bitmap {
  // First decode with inJustDecodeBounds=true to check dimensions
  return BitmapFactory.Options().run {
    inJustDecodeBounds = true
    BitmapFactory.decodeFile(imgPath, this)

    // Calculate inSampleSize
    inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

    // Decode bitmap with inSampleSize set
    inJustDecodeBounds = false

    BitmapFactory.decodeFile(imgPath, this)
  }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
  // Raw height and width of image
  val (height: Int, width: Int) = options.run { outHeight to outWidth }
  var inSampleSize = 1

  if (height > reqHeight || width > reqWidth) {

    val halfHeight: Int = height / 2
    val halfWidth: Int = width / 2

    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
    // height and width larger than the requested height and width.
    while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
      inSampleSize *= 2
    }
  }

  return inSampleSize
}

fun bitmapToFile(bitmap: Bitmap, file: File) {
  val stream = FileOutputStream(file)

  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
  stream.flush()
  stream.close()
}