package com.penny.planner.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.groups.GroupEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar (
    modifier: Modifier,
    title: String,
    onBackPressed: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = null,
                modifier = modifier
                    .clickable(onClick = onBackPressed)
                    .padding(start = 16.dp)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoredTopBar(
    modifier: Modifier,
    title: String,
    color: Color,
    onBackPressed: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarColors(
            containerColor = color,
            actionIconContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.White,
            scrolledContainerColor = Color.Transparent
        ),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = null,
                modifier = modifier
                    .clickable(onClick = onBackPressed)
                    .padding(start = 16.dp),
                tint = Color.White
            )
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GroupSessionTopBar(
    entity: GroupEntity,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            GlideImage(
                modifier = Modifier
                    .padding(12.dp)
                    .size(32.dp)
                    .border(
                        color = colorResource(id = R.color.textField_border),
                        width = 2.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape),
                model = entity.profileImage,
                contentDescription = "",
                contentScale = ContentScale.Crop
            ) {
                it.load(entity.profileImage)
                    .placeholder(R.drawable.default_user_display)
                    .error(R.drawable.default_user_display)
            }
            Text(
                text = entity.name,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = onBackPressed)
                    .padding(start = 16.dp)
            )
        }
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    GroupSessionTopBar(entity = GroupEntity(name = "Family Group")) {

    }
}