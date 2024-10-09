package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.groups.GroupEntity

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GroupItem(
    modifier: Modifier,
    entity: GroupEntity,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier
                .padding(12.dp)
            .wrapContentSize()
            .background(color = colorResource(id = R.color.white)),
        onClick = {
            onClick.invoke()
        },
    ) {
        Column(
            modifier = modifier
                .padding(12.dp)
        ) {
            GlideImage(
                modifier = Modifier
                    .padding(12.dp)
                    .border(
                        color = colorResource(id = R.color.black),
                        width = 2.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape),
                model = entity.profileUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop
            ) {
                it.load(entity.profileUrl)
                    .placeholder(R.drawable.default_user_display)
                    .error(R.drawable.default_user_display)
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = entity.name,
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun PreviewGroup() {
    GroupItem(
        modifier = Modifier,
        GroupEntity(name = "Family")
    ) {}
}