package com.iade.streetart.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iade.streetart.R
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

@Composable
fun LoginViewState(navController: NavController, userViewModel: UserViewModel) {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val focusManager = LocalFocusManager.current
  var email by rememberSaveable { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }

  fun onLoginClick() {
    scope.launch {
      try {
        if (email.isNotEmpty() && password.isNotEmpty()) {
          val res = userViewModel.login(email.trim(), password.trim())

          if (res == "OK") {
            navController.navigate("map") {
              popUpTo("home") { inclusive = true }
            }
          } else {
            email = ""
            password = ""
            focusManager.clearFocus(true)
            scaffoldState.snackbarHostState.showSnackbar("Invalid credentials")
          }
        } else {
          scaffoldState.snackbarHostState.showSnackbar("Please enter your email and password")
        }
      } catch (ex: Exception) {
        email = ""
        password = ""
        focusManager.clearFocus(true)

        if (ex is TimeoutException) {
          scaffoldState.snackbarHostState.showSnackbar("Login failed, try again in a moment.")
        } else {
          scaffoldState.snackbarHostState.showSnackbar(ex.message ?: "Login failed, try again.")
        }
      }

    }
  }

  LoginView(
    scaffoldState = scaffoldState,
    focusManager = focusManager,
    email = email,
    onEmailChange = { email = it },
    password = password,
    onPasswordChange = { password = it },
    onLoginClick = { onLoginClick() },
    navigateBackClick = { navController.popBackStack() }
  )

}

@Composable
fun LoginView(
  scaffoldState: ScaffoldState,
  focusManager: FocusManager,
  email: String,
  onEmailChange: (String) -> Unit,
  password: String,
  onPasswordChange: (String) -> Unit,
  onLoginClick: () -> Unit,
  navigateBackClick: () -> Unit,
) {

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = {
            navigateBackClick()
          }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
          }
        },
        title = { Text(text = "Login") },
        elevation = 4.dp,
      )
    },
    content = { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .padding(paddingValues)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Image(
          painter = painterResource(id = R.drawable.street_art),
          contentDescription = "street art",
          contentScale = ContentScale.FillWidth,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp)
        )

        OutlinedTextField(
          value = email,
          leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "email") },
          onValueChange = onEmailChange,
          label = { Text(text = "Email") },
          placeholder = { Text(text = "Enter your email") },
          keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Email
          )
        )

        OutlinedTextField(
          value = password,
          leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "password") },
          onValueChange = onPasswordChange,
          label = { Text(text = "Password") },
          placeholder = { Text(text = "Enter your password") },
          visualTransformation = PasswordVisualTransformation(),
          keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
          )
        )

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = {
            onLoginClick()
          }
        ) {
          Text(text = "Login")
        }
      }
    }
  )
}