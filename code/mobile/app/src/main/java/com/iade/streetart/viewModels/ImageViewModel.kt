package com.iade.streetart.viewModels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream

class ImageViewModel : ViewModel() {

  private var _imageUri by mutableStateOf(Uri.parse("file://dev/null"))

  val imageUri: Uri
    get() = _imageUri

  fun setImageUri(value: Uri) {
    _imageUri = value
  }

  fun clearImageUri() {
    _imageUri = Uri.parse("file://dev/null")
  }

  fun uriToFile(): File {
    val file = _imageUri.toFile()

    val bitmap = decodeBitmapFromFile(imageUri.path!!)

    bitmapToFile(bitmap, file)

    return file
  }

  private fun decodeBitmapFromFile(
    imgPath: String,
    reqWidth: Int = 1024,
    reqHeight: Int = 1024,
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

  private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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

  private fun bitmapToFile(bitmap: Bitmap, file: File) {
    val stream = FileOutputStream(file)

    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
  }
}

