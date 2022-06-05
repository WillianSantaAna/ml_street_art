package com.iade.streetart.models

import retrofit2.Response
import retrofit2.http.GET

data class Image (
  val img_id: Int,
//  val img_sta_id: Int,
//  val img_usr_id: Int,
  val img_url: String,
//  val img_published: Boolean,
//  val img_active: Boolean,
)

interface ImagesApi {
  @GET("api/images")
  suspend fun getStreetArts() : Response<List<User>>
}