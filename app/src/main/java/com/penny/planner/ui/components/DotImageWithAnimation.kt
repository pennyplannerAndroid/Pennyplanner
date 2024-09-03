package com.penny.planner.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.penny.planner.R


@Composable
fun DotImageWithAnimation(selected: Boolean) {
    Image(
        painter = painterResource(
            id = if (selected) R.drawable.selected_dot else R.drawable.not_selected_dot
        ),
        contentDescription = stringResource(id = R.string.selected),
        modifier = Modifier
            .padding(5.dp)
            .animateContentSize { _, _ -> 16.dp }
    )
}
