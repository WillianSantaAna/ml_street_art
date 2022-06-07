package com.iade.streetart.models

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

data class Image (
  val img_id: Int = 0,
  val img_sta_id: Int = 0,
  val img_usr_id: Int = 0,
  val img_url: String = "",
)

data class ImageResult (
  val img_url: String = "",
)

interface ImagesApi {
  @Multipart
  @POST("api/images/upload/street_art/{streetArtId}/user/{userId}")
  suspend fun addImage(
    @Path("streetArtId") streetArtId: Int,
    @Path("userId") userId: Int,
    @Part body: MultipartBody.Part
  ) : Response<ImageResult>
}