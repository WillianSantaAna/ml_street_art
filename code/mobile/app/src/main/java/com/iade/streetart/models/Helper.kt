package com.iade.streetart.models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
//  private const val baseUrl = "http://192.168.1.11:3000/"
  private const val baseUrl = "https://street-art-lisbon.herokuapp.com/"

  fun getInstance(): Retrofit {
    return Retrofit.Builder().baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}