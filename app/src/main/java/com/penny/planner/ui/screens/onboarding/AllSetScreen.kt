package com.penny.planner.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.penny.planner.R

@Composable
fun AllSetScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.success_icon),
                contentDescription = stringResource(id = R.string.ok)
            )
            Text(
                text = stringResource(id = R.string.you_are_set),
                color = Color.Black,
                fontSize = 24.sp
            )
        }
    }
}

@Preview
@Composable
fun PreviewAllSetScreen() {
    AllSetScreen()
}