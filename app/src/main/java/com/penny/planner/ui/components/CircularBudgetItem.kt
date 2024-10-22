package com.penny.planner.ui.components

import androidx.compose.foundation.Image
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
    monthlyBudgetInfoModel: Double,
    expenseSoFar: Double,
    navigationClicked: () -> Unit
) {
    val progress =  Utils.getProgress(monthlyBudgetInfoModel, expenseSoFar)
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
            trackColor = colorResource(id = R.color.light_gray),
            color = getColorForCircularExpenseBar((progress * 100).toInt())
        )
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.navigate_icon),
                contentDescription = ""
            )
            Text(
                text = "${Utils.RUPEE}${expenseSoFar.toInt()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            text = "${(progress * 100).toInt()}%",
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun getColorForCircularExpenseBar(progress: Int): Color {
    return when(progress) {
        in 0 until 20 -> colorResource(id = R.color.light_gray)
        in 20 until 50 -> colorResource(id = R.color.light_green)
        in 50 until 70 -> colorResource(id = R.color.green)
        in 70 until 90 -> colorResource(id = R.color.yellow)
        in 90 until 110 -> colorResource(id = R.color.orange)
        else -> colorResource(id = R.color.red)
    }
}

@Preview
@Composable
fun PreviewCircularItem() {
    CircularBudgetItem(
        Modifier,
        50000.0,
        20000.0
    )
    {}
}