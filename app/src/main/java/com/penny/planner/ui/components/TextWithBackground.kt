package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penny.planner.R

@Composable
fun TextWithBackground(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = TextStyle(
            color = if (isSelected) colorResource(id = R.color.loginText) else colorResource(id = R.color.or_with_color),
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .background(
                color = if (isSelected) colorResource(id = R.color.loginButton) else colorResource(id = R.color.teal_200),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .clickable {
                onClick.invoke()
            }
    )
}

@Preview
@Composable
fun PreviewTextWithBackground() {
    TextWithBackground(isSelected = true, text = "Joined") {}
}