package com.penny.planner.ui.screens.mainpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.penny.planner.R
import com.penny.planner.ui.components.BottomDrawerForLogout
import com.penny.planner.viewmodels.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navigateToEditProfile: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showLogoutBottomSheet by remember {
        mutableStateOf(false)
    }
    var profileUrl by remember {
        mutableStateOf("")
    }
    var selfName by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = true) {
        scope.launch(Dispatchers.IO) {
            val profile = viewModel.getSelfProfile()
            withContext(Dispatchers.Main) {
                profileUrl = profile.localImagePath.ifEmpty {
                    profile.profileImageURL
                }
                selfName = profile.name
                email = profile.email
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            ShowProfilePicture(modifier = Modifier, imageUrl = profileUrl)
            Column (
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 12.dp, end = 12.dp)
                    .weight(1f)
            ){
                Text(
                    text = selfName,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 22.sp,
                    color = Color.Black
                )
                Text(
                    text = email,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = navigateToEditProfile),
                tint = Color.Black,
                painter = painterResource(id = R.drawable.profile_edit),
                contentDescription = stringResource(id = R.string.search)
            )
        }
        Card(
            modifier = Modifier
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
            colors = CardDefaults.cardColors()
                .copy(containerColor = Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                RowWithIconAndName(
                    icon = R.drawable.settings_icon,
                    text = stringResource(id = R.string.settings)
                ) {

                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color.LightGray)
                )
                RowWithIconAndName(
                    icon = R.drawable.logout_icon,
                    text = stringResource(id = R.string.logout),
                    backgroundColor = R.color.logout_background
                ) {
                    showLogoutBottomSheet = true
                }
            }
        }
    }
    BottomDrawerForLogout(
        modifier = Modifier,
        showSheet = showLogoutBottomSheet,
        onClose = { showLogoutBottomSheet = false }) {
            viewModel.logout()
    }
}

@Composable
fun RowWithIconAndName(
    icon: Int,
    text: String,
    backgroundColor:
    Int = R.color.loginButton,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = colorResource(id = backgroundColor),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(id = icon),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp, end = 12.dp)
                .weight(1f),
            text = text,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowProfilePicture(
    modifier: Modifier,
    imageUrl: String
) {
    GlideImage(
        modifier = modifier
            .size(96.dp)
            .border(
                color = colorResource(id = R.color.loginText),
                width = 2.dp,
                shape = CircleShape
            )
            .clip(CircleShape),
        model = imageUrl,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        loading = placeholder(R.drawable.default_user_display),
        failure = placeholder(R.drawable.default_user_display)
    )
}