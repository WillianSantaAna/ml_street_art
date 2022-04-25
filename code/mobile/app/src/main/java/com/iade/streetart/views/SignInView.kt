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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.iade.streetart.R
import com.iade.streetart.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignInView(navController: NavController, userViewModel: UserViewModel) {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val focusManager = LocalFocusManager.current
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  Scaffold (
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar (
        navigationIcon = {
          IconButton(onClick = {
            navController.popBackStack()
          }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
          }
        },
        title = { Text(text = "Create Account") },
        elevation = 4.dp,
      )
    },
    content = {
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .padding(top = 25.dp, bottom = 50.dp)
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
          onValueChange = { name = it },
          label = { Text(text = "Name") },
          placeholder = { Text(text = "Enter your name") },
          keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
          value = email,
          leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "email") },
          onValueChange = { email = it },
          label = { Text(text = "Email") },
          placeholder = { Text(text = "Enter your email") },
          keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
          value = password,
          leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "password") },
          onValueChange = { password = it },
          label = { Text(text = "Password") },
          placeholder = { Text(text = "Enter your password") },
          visualTransformation = PasswordVisualTransformation(),
          keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password)
        )

        Button(
          modifier = Modifier.fillMaxWidth(0.5f),
          onClick = {
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
              scope.launch {
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
              }
            } else {
              scope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Please enter your name, email and password")
              }
            }
          }
        ) {
          Text(text = "Create Account")
        }
      }
    }
  )



}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInViewPreview() {
  MaterialTheme {
    SignInView(rememberNavController(), viewModel())
  }
}