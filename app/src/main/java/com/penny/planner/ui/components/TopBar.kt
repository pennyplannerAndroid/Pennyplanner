package com.penny.planner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R

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
                textAlign = TextAlign.Center,
                fontSize = 20.sp
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

@Preview
@Composable
fun PreviewTopBar() {

}