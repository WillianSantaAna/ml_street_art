package com.iade.streetart.models

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first
import retrofit2.Response
import retrofit2.http.Body
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

class UserLocalDataStore(private val dataStore: DataStore<Preferences>) {

  private object PreferencesKeys {
    val ID = intPreferencesKey("usr_id")
    val NAME = stringPreferencesKey("usr_name")
    val TYPE = stringPreferencesKey("usr_type")
  }

//  val userPreferencesFlow = dataStore.data.catch { exception ->
//    if (exception is IOException) {
//      Log.e("UserLocalDataStorage", "error reading preferences.", exception)
//      emit(emptyPreferences())
//    } else {
//      throw exception
//    }
//  }.map { preferences ->
//    mapUserPreferences(preferences)
//  }

  suspend fun updatePreferences(id: Int, name: String, type: String) {
    dataStore.edit { preferences ->
      preferences[PreferencesKeys.ID] = id
      preferences[PreferencesKeys.NAME] = name
      preferences[PreferencesKeys.TYPE] = type
    }
  }

  suspend fun fetchInitialPreferences() =
    mapUserPreferences(dataStore.data.first().toPreferences())

  private fun mapUserPreferences(preferences: Preferences): User {
    val usrId = preferences[PreferencesKeys.ID] ?: 0
    val usrName = preferences[PreferencesKeys.NAME] ?: ""
    val usrType = preferences[PreferencesKeys.TYPE] ?: ""

    Log.i("localUser", User(usrId, usrName, usrType).toString())
    return User(usrId, usrName, usrType)
  }
}
