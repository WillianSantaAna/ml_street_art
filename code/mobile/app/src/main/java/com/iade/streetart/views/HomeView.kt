package com.iade.streetart.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.iade.streetart.R

@Composable
fun HomeView(navController: NavController) {

  Scaffold { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .padding(paddingValues),
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

      Text(
        text = "Street Art Lisbon",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        letterSpacing = 5.sp,
      )

      Text(
        text = "Discover more than 1000 street art in this open-air gallery that is Lisbon",
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp,
      )

      Button(
        modifier = Modifier.fillMaxWidth(0.5f),
        onClick = { navController.navigate("login") }
      ) {
        Text(text = "Login Now")
      }

      OutlinedButton(
        modifier = Modifier.fillMaxWidth(0.5f),
        onClick = { navController.navigate("signIn") }
      ) {
        Text(text = "Create Account")
      }
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeViewPreview() {
  MaterialTheme {
    HomeView(rememberNavController())
  }
}