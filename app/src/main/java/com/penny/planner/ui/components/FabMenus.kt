package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.penny.planner.R

@Composable
fun BigFabMenuOption(
    modifier: Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        shape = CircleShape,
        onClick = onClick,
        containerColor = colorResource(id = R.color.loginText)
    ) {
        Image(
            painter = painterResource(id = R.drawable.add_without_background),
            contentDescription = stringResource(id = R.string.add)
        )
    }
}

@Composable
fun SmallFabMenuWithDescription(
    modifier: Modifier,
    description: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Absolute.Right
    ) {
        Text(
            modifier = modifier
                .align(Alignment.CenterVertically)
                .padding(end = 6.dp),
            text = description,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.loginText)
        )
        SmallFloatingActionButton(
            modifier = modifier
                .align(Alignment.CenterVertically),
            onClick = onClick,
            shape = CircleShape,
            containerColor = colorResource(id = R.color.loginText)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = R.string.add)
            )
        }
    }
}