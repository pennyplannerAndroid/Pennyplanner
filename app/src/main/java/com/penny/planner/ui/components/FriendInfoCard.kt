package com.penny.planner.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.friends.UsersEntity

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FriendInfoCard(
    modifier: Modifier,
    model: UsersEntity,
    onCancel: () -> Unit,
    onAccept: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        GlideImage(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically)
                .border(
                    color = colorResource(id = R.color.textField_border),
                    width = 2.dp,
                    shape = CircleShape
                )
                .clip(CircleShape),
            model = model.profileImageURL,
            contentDescription = "",
            contentScale = ContentScale.Crop
        ) {
            it.load(model.profileImageURL)
                .placeholder(R.drawable.default_user_display)
                .error(R.drawable.default_user_display)
        }
        Column(
            modifier = modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = model.name,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis

            )
            Text(
                text = model.email,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = Color.Black,
                fontSize = 14.sp
            )
        }
        Row(
            modifier = modifier
                .align(Alignment.CenterVertically)
                .padding(start = 4.dp)
        ) {
            Icon(
                modifier = modifier
                    .padding(end = 4.dp)
                    .size(32.dp)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = onCancel),
                painter = painterResource(id = R.drawable.cancel_button),
                contentDescription = "",
                tint = Color.Red
            )
            Icon(
                modifier = modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = onAccept),
                painter = painterResource(id = R.drawable.success_icon),
                contentDescription = "",
                tint = colorResource(id = R.color.success_green)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FriendInfoForMemberList (
    modifier: Modifier,
    model: UsersEntity,
    self: String,
    admin: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GlideImage(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically)
                .border(
                    color = colorResource(id = R.color.textField_border),
                    width = 2.dp,
                    shape = CircleShape
                )
                .clip(CircleShape),
            model = model.profileImageURL,
            contentDescription = "",
            contentScale = ContentScale.Crop
        ) {
            it.load(model.profileImageURL)
                .placeholder(R.drawable.default_user_display)
                .error(R.drawable.default_user_display)
        }
        Column(
            modifier = modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            if (model.id != self) {
                Text(
                    text = model.name,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )
                Text(
                    text = model.email,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = "You",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (model.email == admin) {
            Text(
                modifier = Modifier.
                align(Alignment.CenterVertically),
                text = "(Admin)",
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun PreviewFriendCard() {
    FriendInfoCard(Modifier, UsersEntity(name = "Priyanka", email = "Priyanka@dummy.com", id = "SS", profileImageURL = ""), {}) {}
}