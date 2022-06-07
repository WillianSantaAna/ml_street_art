package com.iade.streetart.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.iade.streetart.models.StreetArt
import com.iade.streetart.viewModels.StreetArtViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchViewState(navController: NavController, streetArtViewModel: StreetArtViewModel, authorName: String = "") {

  val scope = rememberCoroutineScope()
  val scaffoldState = rememberScaffoldState()
  val focusManager = LocalFocusManager.current
  var search by rememberSaveable { mutableStateOf(authorName.trim()) }

  LaunchedEffect(search) {
    launch {
      streetArtViewModel.fetchStreetArts()
    }
  }

  val streetArts = if (search.isEmpty()) {
    streetArtViewModel.streetArts
  } else {
    streetArtViewModel.streetArts.filter { it.sta_artist.contains(search, ignoreCase = true) }
  }

  SearchView(
    scaffoldState = scaffoldState,
    focusManager = focusManager,
    search = search,
    onSearchChange = { search = it },
    navigateBackClick = { navController.popBackStack() },
    onCardClick = { streetArtId ->
      scope.launch {
        navController.navigate("streetArt/$streetArtId")
      }
    },
    streetArts = streetArts
  )
}

@Composable
fun SearchView(
  scaffoldState: ScaffoldState,
  focusManager: FocusManager,
  search: String,
  onSearchChange: (String) -> Unit,
  navigateBackClick: () -> Unit,
  onCardClick: (Int) -> Unit,
  streetArts: List<StreetArt>
) {

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TextField(
        modifier = Modifier
          .fillMaxWidth()
          .focusRequester(FocusRequester.Default),
        value = search,
        onValueChange = onSearchChange,
        maxLines = 1,
        singleLine = true,
        placeholder = { Text(text = "Enter the artist name")},
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions(
          imeAction = ImeAction.Done,
          keyboardType = KeyboardType.Text),
        leadingIcon = {
          IconButton(onClick = {
            navigateBackClick()
          }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
          }
        },
        trailingIcon = {
          if (search.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }) {
              Icon(
                imageVector = Icons.Rounded.Clear,
                contentDescription = "Clear icon"
              )
            }
          }
        },
      )
    },
    content = { paddingValues ->
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
      ) {
        items(streetArts) { streetArt ->
          ImageCard(streetArt = streetArt, onCardClick = { onCardClick(streetArt.sta_id) })
        }
      }
    }
  )
}

@Composable
fun ImageCard(streetArt: StreetArt, onCardClick: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .height(200.dp)
      .clickable { onCardClick() },
    shape = RoundedCornerShape(10),
    elevation = 4.dp,
  ) {
    Box {
      AsyncImage(
        modifier = Modifier.fillMaxSize(),
        model = ImageRequest.Builder(LocalContext.current)
          .data(streetArt.img_url)
          .crossfade(true)
          .allowHardware(false)
          .build(),
        contentDescription = "Street Art Image",
        contentScale = ContentScale.Crop,
      )

      Box(
        Modifier
          .fillMaxSize()
          .background(Brush.verticalGradient(0F to Color.Transparent, 1F to Color(18, 18, 18)))
      )

      Text(
        text = streetArt.sta_artist,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(8.dp)
      )
    }
  }
}
