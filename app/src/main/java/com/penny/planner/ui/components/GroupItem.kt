package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.models.GroupListDisplayModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GroupItem(
    modifier: Modifier,
    entity: GroupListDisplayModel,
    isAdmin: Boolean,
    friendsForDisplayPicture: List<UsersEntity>,
    onClick: () -> Unit,
    addCLick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.white)),
        onClick = {
            onClick.invoke()
        },
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(color = colorResource(id = R.color.group_list_item_top))
            )
            Column(
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                GlideImage(
                    modifier = Modifier
                        .border(
                            color = colorResource(id = R.color.black),
                            width = 2.dp,
                            shape = CircleShape
                        )
                        .size(48.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    model = entity.profileImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                ) {
                    it.load(entity.profileImage)
                        .placeholder(R.drawable.default_user_display)
                        .error(R.drawable.default_user_display)
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = entity.name,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    minLines = 2,
                    maxLines = 2
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Start),
                    text = "${entity.expense.toInt()} of ${entity.monthlyBudget.toInt()}",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier) {
                        ShowGlideImage(
                            modifier = Modifier.padding(6.dp),
                            size = 32.dp,
                            image = friendsForDisplayPicture[0].localImagePath
                        )
                        if (friendsForDisplayPicture.size > 1) {
                            ShowGlideImage(
                                modifier = Modifier.padding(start = 32.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                size = 32.dp,
                                image = friendsForDisplayPicture[1].localImagePath
                            )
                        }
                        if(friendsForDisplayPicture.size > 2) {
                            Text(modifier = Modifier.padding(start = 66.dp, end = 6.dp, top = 12.dp, bottom = 6.dp),
                                text = "+ ${friendsForDisplayPicture.size - 2}"
                            )
                        }
                    }
                    CircularButtonWithIcon(
                        icon = R.drawable.add_group_member,
                        contentDescription = stringResource(id = R.string.add),
                        onClick = addCLick
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowGlideImage(modifier: Modifier, size: Dp, image: String) {
    GlideImage(
        modifier = modifier
            .border(
                color = colorResource(id = R.color.black),
                width = 2.dp,
                shape = CircleShape
            )
            .size(size)
            .clip(CircleShape),
        model = image,
        contentDescription = "",
        contentScale = ContentScale.Crop
    ) {
        it.load(image)
            .placeholder(R.drawable.default_user_display)
            .error(R.drawable.default_user_display)
    }
}

@Preview
@Composable
fun PreviewGroup() {
    GroupItem(
        modifier = Modifier,
        GroupListDisplayModel(name = "Family"),
        true,
        listOf(UsersEntity(), UsersEntity(), UsersEntity()),
        {}
    ) {}
}