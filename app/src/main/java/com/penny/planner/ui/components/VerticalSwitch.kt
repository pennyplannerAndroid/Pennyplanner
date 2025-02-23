package com.penny.planner.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

@Composable
fun VerticalSwitch(
    modifier: Modifier,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val switchHeight = 80.dp
    val switchWidth = 46.dp
    val handleHeight = 40.dp

    // Animation for switch position
    val offsetY by animateFloatAsState(targetValue = if (isOn) 0f else 1f, label = "")

    Box(
        modifier = modifier
            .size(width = switchWidth, height = switchHeight)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle(!isOn) }
            .background(Color.Transparent),
        contentAlignment = Alignment.TopCenter
    ) {
        // Switch labels
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ON", fontSize = 12.sp, color = if (isOn) Color.Green else Color.LightGray)
            Text("OFF", fontSize = 12.sp, color = if (isOn) Color.White else Color.Red)
        }

        // Toggle handle
        Box(
            modifier = Modifier
                .offset(y = offsetY * (switchHeight - handleHeight - 4.dp).value.dp) // Adjust for padding
                .size(width = switchWidth - 8.dp, height = handleHeight)
                .padding(2.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isOn) Color.White else Color.LightGray)
        )
    }
}