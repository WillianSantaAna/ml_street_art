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
import androidx.compose.material.icons.filled.Person
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

@Composable
fun SignInViewState(navController: NavController, userViewModel: UserViewModel) {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val focusManager = LocalFocusManager.current
  var name by rememberSaveable { mutableStateOf("") }
  var email by rememberSaveable { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }

  fun onSignInClick() {
    scope.launch {
      if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
        val res = userViewModel.register(name.trim(), email.trim(), password.trim())

        if (res == "OK") {
          navController.navigate("map") {
            popUpTo("home") { inclusive = true }
          }
        } else {
          email = ""
          password = ""
          focusManager.clearFocus(true)
          scaffoldState.snackbarHostState.showSnackbar("Failed to create a new user")
        }
      } else {
        scaffoldState.snackbarHostState.showSnackbar("Please enter your name, email and password")
      }
    }
  }

  SignInView(
    scaffoldState = scaffoldState,
    focusManager = focusManager,
    name = name,
    onNameChange = { name = it },
    email = email,
    onEmailChange = { email = it },
    password = password,
    onPasswordChange = { password = it },
    onSignInClick = { onSignInClick() },
    navigateBackClick = { navController.popBackStack() }
  )
}

@Composable
fun SignInView(
  scaffoldState: ScaffoldState,
  focusManager: FocusManager,
  name: String,
  onNameChange: (String) -> Unit,
  email: String,
  onEmailChange: (String) -> Unit,
  password: String,
  onPasswordChange: (String) -> Unit,
  onSignInClick: () -> Unit,
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
        title = { Text(text = "Create Account") },
        elevation = 4.dp,
      )
    },
    content = { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .padding(paddingValues)
//          .padding(top = 25.dp, bottom = 50.dp)
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
          value = name,
          leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "name") },
          onValueChange = onNameChange,
          label = { Text(text = "Name") },
          placeholder = { Text(text = "Enter your name") },
          keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
          )
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
            onSignInClick()
          }
        ) {
          Text(text = "Create Account")
        }
      }
    }
  )

}
