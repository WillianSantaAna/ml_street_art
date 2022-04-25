package com.iade.streetart.models

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class StreetArt (
  val sta_id: Int,
  val sta_usr_id: Int,
  val sta_artist: String,
  val sta_project: String,
  val sta_year: Int,
  val sta_photo_credits: String,
  val sta_address: String,
  val sta_coords: Coords,
  val sta_status: String,
  val sta_published: Boolean,
  val sta_active: Boolean,
)

data class Coords (val lat: Double, val lng: Double)

data class PredictResult (val author: String, val prediction: Double)

interface StreetArtApi {
  @GET("api/streetArts")
  suspend fun getStreetArts() : Response<List<User>>

  @Multipart
  @POST("api/streetArts/predict")
  suspend fun predict(@Part body: MultipartBody.Part) : Response<List<PredictResult>>
}