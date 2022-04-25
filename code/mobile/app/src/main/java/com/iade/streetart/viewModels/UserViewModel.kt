package com.iade.streetart.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iade.streetart.models.RetrofitHelper
import com.iade.streetart.models.User
import com.iade.streetart.models.UserForm
import com.iade.streetart.models.UsersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
  private var _user by mutableStateOf<User?>(null)

  private val usersApi = RetrofitHelper.getInstance().create(UsersApi::class.java)

  val user: User
    get() = _user ?: throw Exception("Failed to get the user")

  suspend fun login(email: String, password: String): String? {
    val res = withContext(Dispatchers.Default) {
      val form = UserForm(email = email, password = password)

      val result = usersApi.login(form)

      if (result.isSuccessful) {
        _user = result.body()!!
      }

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

      result.message()
    }

    Log.i("user", _user.toString())
    return res
  }

  fun logout() {
    _user = null
  }
}