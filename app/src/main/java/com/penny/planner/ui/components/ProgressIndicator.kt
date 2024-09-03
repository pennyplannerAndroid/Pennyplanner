package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.penny.planner.R

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