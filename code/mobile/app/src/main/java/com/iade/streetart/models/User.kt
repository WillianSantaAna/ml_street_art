package com.iade.streetart.models

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class User (
  val usr_id: Int,
  val usr_name: String,
  val usr_type: String
)

data class UserForm(
  val name:String = "",
  val email: String = "",
  val password: String = ""
)

interface UsersApi {
//  @GET("api/users")
//  suspend fun getUsers() : Response<List<User>>

  @POST("/api/users/login")
  suspend fun login(@Body form: UserForm) : Response<User>

  @POST("/api/users/register")
  suspend fun register(@Body form: UserForm) : Response<User>
}