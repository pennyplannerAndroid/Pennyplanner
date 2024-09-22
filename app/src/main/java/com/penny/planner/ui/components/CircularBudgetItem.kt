package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.helpers.Utils

@Composable
fun CircularBudgetItem(
    modifier: Modifier,
    progress: Double,
    navigationClicked: () -> Unit
) {
    val size = LocalConfiguration.current.screenWidthDp/2
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = navigationClicked)
    ) {
        CircularProgressIndicator(
            modifier = modifier
                .size(size.dp),
            progress = { progress.toFloat() },
            strokeWidth = 12.dp,
            trackColor = colorResource(id = R.color.textField_border),
            color = Color.Magenta
        )
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column() {
                Image(
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    painter = painterResource(id = R.drawable.navigate_icon),
                    contentDescription = ""
                )
                Text(
                    text = "${Utils.RUPEE}500",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold

                )
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            text = "${progress * 100}%",
            fontWeight = FontWeight.Bold
        )
    }

}

@Preview
@Composable
fun PreviewCircularItem() {
    CircularBudgetItem(Modifier,0.3) {}
}