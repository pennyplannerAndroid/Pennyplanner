package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R

@Composable
fun PrimaryButton(
    modifier: Modifier,
    textRes: Int,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonColors(
            colorResource(id = R.color.loginText),
            colorResource(id = R.color.white),
            colorResource(id = R.color.teal_200),
            colorResource(id = R.color.teal_200)
        ),
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = textRes),
            color = if (enabled) Color.White else Color.Gray,
        )
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier,
    onClick: () -> Unit,
    textRes: Int
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonColors(
            colorResource(id = R.color.loginButton),
            colorResource(id = R.color.loginText),
            colorResource(id = R.color.teal_200),
            colorResource(id = R.color.teal_200)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = stringResource(id = textRes))
    }
}

@Composable
fun OutlinedButtonWIthIcon(
    modifier: Modifier,
    onClick: () -> Unit,
    imageRes: Int,
    text: String
) {
    OutlinedButton(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.textField_border),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row {
            Image(
                modifier = Modifier.align(Alignment.CenterVertically),
                painter = painterResource(id = imageRes),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.CenterVertically),
                text = text,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black),
                fontSize = 18.sp
            )
        }
    }
}