package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.models.UserModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FriendInfoCard(
    modifier: Modifier,
    model: UserModel,
    onCLick: () -> Unit,
    deleteClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.loginText)),
        onClick = onCLick,
    ) {
        Row {
            GlideImage(
                modifier = Modifier
                    .padding(12.dp)
                    .size(48.dp)
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
                    .weight(0.6f)
            ) {
                model.name?.let {
                    Text(
                        text = it,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold

                    )
                }
                model.email?.let { Text(text = it) }
            }
            Image(
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .padding(12.dp)
                    .clickable(onClick = deleteClick),
                painter = painterResource(id = R.drawable.delete_icon),
                contentDescription = "",
            )
        }

    }
}

@Preview
@Composable
fun PreviewFriendCard() {
    FriendInfoCard(Modifier, UserModel(name = "Priyanka", email = "Priyanka@dummy.com", id = "SS", profileImageURL = ""), {}) {}
}