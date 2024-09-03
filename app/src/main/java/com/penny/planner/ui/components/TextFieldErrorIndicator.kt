package com.penny.planner.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.penny.planner.R

@Composable
fun TextFieldErrorIndicator(
    modifier: Modifier,
    textRes: Int,
    show: Boolean
) {
    if (show) {
        Row(
            modifier = modifier
                .padding(start = 20.dp, end = 20.dp, top = 4.dp)
                .animateContentSize()
        ) {
            Image(
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp),
                painter = painterResource(id = R.drawable.error_image),
                contentDescription = "invalid"
            )
            Text(
                text = stringResource(id = textRes),
                color = colorResource(id = R.color.red)
            )
        }
    }
}