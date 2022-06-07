package com.iade.streetart.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class User (
  val usr_id: Int = 0,
  val usr_name: String = "",
  val usr_type: String = ""
)

data class UserForm(
  val name:String = "",
  val email: String = "",
  val password: String = ""
)

interface UsersApi {
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

    return User(usrId, usrName, usrType)
  }
}
