package com.iade.streetart.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iade.streetart.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userLocalDataStore: UserLocalDataStore) : ViewModel() {

  private var _user by mutableStateOf<User?>(null)

  val user: User
    get() = _user ?: User(0, "", "")

  private val usersApi = RetrofitHelper.getInstance().create(UsersApi::class.java)

  suspend fun login(email: String, password: String): String? {
    val res = withContext(Dispatchers.Default) {
      val form = UserForm(email = email, password = password)

      val result = usersApi.login(form)

      if (result.isSuccessful) {
        _user = result.body()!!
      }

      userLocalDataStore.updatePreferences(
        result.body()!!.usr_id,
        result.body()!!.usr_name,
        result.body()!!.usr_type
      )

      result.message()
    }

    Log.i("user", _user.toString())
    return res
  }

  suspend fun register(name:String, email: String, password: String): String? {
    val res = withContext(Dispatchers.Default) {
      val form = UserForm(name = name, email = email, password = password)

      val result = usersApi.register(form)

      if (result.isSuccessful) {
        _user = result.body()!!
      }

      userLocalDataStore.updatePreferences(
        result.body()!!.usr_id,
        result.body()!!.usr_name,
        result.body()!!.usr_type
      )

      result.message()
    }

    Log.i("user", _user.toString())
    return res
  }

  fun logout() {
    CoroutineScope(Dispatchers.IO).launch {
      userLocalDataStore.updatePreferences(0, "", "")
      _user = null
    }
  }

  suspend fun isUserLoggedIn(): Boolean {
    val res = withContext(Dispatchers.Default) {
      val localUser = userLocalDataStore.fetchInitialPreferences()
      var result = false

      Log.i("localUser", localUser.toString())
      if (localUser.usr_id != 0 && localUser.usr_name != "" && localUser.usr_type != "") {
        _user = localUser
        Log.i("localUserTrue", localUser.toString())

        result = true
      } else {
        _user = null
      }

      result
    }

    return res
  }
}