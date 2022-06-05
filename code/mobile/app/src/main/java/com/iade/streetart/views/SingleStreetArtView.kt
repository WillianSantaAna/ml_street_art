package com.iade.streetart.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.iade.streetart.models.Image
import com.iade.streetart.models.StreetArt
import com.iade.streetart.viewModels.StreetArtViewModel
import kotlinx.coroutines.launch

@Composable
fun SingleStreetArtViewState(
  navController: NavController,
  streetArtViewModel: StreetArtViewModel,
  streetArtId: String
) {

  val scaffoldState = rememberScaffoldState()

  val streetArt = streetArtViewModel.getSingleStreetArt(streetArtId.toInt())
  var images by rememberSaveable { mutableStateOf(listOf<Image>()) }

  LaunchedEffect(streetArt) {
    launch {
      images = streetArtViewModel.fetchImages(streetArt.sta_id)
    }
  }

  SingleStreetArtView(
    scaffoldState = scaffoldState,
    navigateBackClick = { navController.popBackStack() },
    streetArt = streetArt,
    images = images
  )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SingleStreetArtView(
  scaffoldState: ScaffoldState,
  navigateBackClick: () -> Unit,
  streetArt: StreetArt,
  images: List<Image>
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
        title = { Text(text = "Street Art") },
        elevation = 4.dp,
      )
    },
    content = { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxHeight()
          .padding(paddingValues)
          .verticalScroll(rememberScrollState()),
      ) {
        HorizontalPager(
          count = images.size,
          state = rememberPagerState(),
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 8.dp)
        ) { page ->
          AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
              .data(images[page].img_url)
              .crossfade(true)
              .allowHardware(false)
              .build(),
            contentDescription = "Street Art Image",
            contentScale = ContentScale.Fit,
          )
        }
        Column(
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 15.dp)
        ) {
          if (streetArt.sta_artist.isNotEmpty()) {
            Row {
              Text(text = "Artist: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
              Text(text = streetArt.sta_artist, fontSize = 20.sp)
              Spacer(modifier = Modifier.height(35.dp))
            }
          }
          if (streetArt.sta_address.isNotEmpty()) {
            Row {
              Text(text = "Address: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
              Text(text = streetArt.sta_address, fontSize = 20.sp)
              Spacer(modifier = Modifier.height(35.dp))
            }
          }
          if (streetArt.sta_status.isNotEmpty()) {
            Row {
              Text(text = "Status: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
              Text(text = streetArt.sta_status, fontSize = 20.sp)
              Spacer(modifier = Modifier.height(35.dp))
            }
          }
          if (streetArt.sta_year != 0) {
            Row {
              Text(text = "Year: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
              Text(text = streetArt.sta_year.toString(), fontSize = 20.sp)
              Spacer(modifier = Modifier.height(35.dp))
            }
          }
        }
      }
    }
  )
}