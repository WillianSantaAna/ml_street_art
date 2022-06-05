package com.iade.streetart.viewModels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.iade.streetart.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class StreetArtViewModel : ViewModel() {

  private var _streetArts by mutableStateOf<List<StreetArt>>(listOf())

  val streetArts: List<StreetArt>
    get() = _streetArts

  private val streetArtApi = RetrofitHelper.getInstance().create(StreetArtApi::class.java)

  suspend fun fetchStreetArts(): String? {
    val res = withContext(Dispatchers.Default) {
      val result = streetArtApi.getStreetArts()

      if (result.isSuccessful) {
        _streetArts = result.body()!!
      }

      result.message()
    }

    Log.i("streetArt", _streetArts.toString())
    return res
  }

  suspend fun fetchImages(id: Int): List<Image> {
    val res = withContext(Dispatchers.Default) {
      val result = streetArtApi.getStreetArtImages(id)

      if (result.isSuccessful) {
        return@withContext result.body()!!
      }

      return@withContext listOf<Image>()
    }

    Log.i("images", res.toString())
    return res
  }

  suspend fun predict(body: MultipartBody.Part): List<PredictResult> {
    val res = withContext(Dispatchers.Default) {
      val result = streetArtApi.predict(body)

      if (result.isSuccessful) {
        return@withContext result.body()!!
      }

      return@withContext listOf<PredictResult>()
    }

    return res
  }

  suspend fun addStreetArt(streetArt: StreetArtPost): Int {
    val res = withContext(Dispatchers.Default) {
      val result = streetArtApi.addStreetArt(streetArt)

      if (result.isSuccessful) {
        return@withContext result.body()!!.sta_id
      }

      return@withContext 0
    }

    return res
  }

  fun getSingleStreetArt(id: Int) =
    _streetArts.find { it.sta_id == id }
      ?: StreetArt(
        sta_id = 0,
        sta_usr_id = 0,
        sta_artist = "",
        sta_project = "",
        sta_year = 0,
        sta_photo_credits = "",
        sta_address = "",
        sta_coords = Coords(0.0, 0.0),
        sta_status = "",
        img_url = ""
      )
}