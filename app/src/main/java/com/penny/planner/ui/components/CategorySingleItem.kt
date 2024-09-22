package com.penny.planner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategorySingleItem(
    modifier: Modifier,
    item: Pair<String, String>,
    onItemClick: () -> Unit
) {
    Row(
    modifier = modifier
    .padding(bottom = 8.dp)
    .clickable { onItemClick() }
    ) {
        Text(text = item.second)
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically),
            text = item.first,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}