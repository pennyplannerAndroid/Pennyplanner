package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.helpers.Utils

@Composable
fun FullScreenProgressIndicator(show: Boolean) {
    if (show) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = colorResource(id = R.color.transparent_60)
                )
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun GroupSearchProgressIndicator(show: Boolean) {
    if (show) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = colorResource(id = R.color.transparent_60)
                )
        ) {
            Row {
                CircularProgressIndicator()
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically),
                    text = Utils.FETCHING_GROUP,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}