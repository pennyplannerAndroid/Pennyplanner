package com.penny.planner.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.helpers.dpToPx
import com.penny.planner.models.GroupDisplayModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ExpensePieChart(expenses: List<GroupDisplayModel>, modifier: Modifier = Modifier) {
    val map = expenses.groupBy { it.subCategory }
    val totalAmount = expenses.sumOf { it.price }.toFloat()
    val screenWidth = LocalConfiguration.current.screenWidthDp/2
    val screenWidthPx = screenWidth.dp.dpToPx()
    val colors = listOf(
        Color(0xFFFFC107), // Amber
        Color(0xFF03DAC5), // Teal
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFF4081), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF00E676)  // Light Green
    )
    Row(
        modifier = modifier
            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Canvas(
            modifier = Modifier.size(screenWidth.dp)
        ) {
            val radius = screenWidthPx / 2
            val center = Offset(radius, radius)
            var startAngle = -90f // Start at the top
            var iterator = 0
            map.forEach { item ->
                val sweepAngle = (item.value.sumOf { it.price }.toFloat() / totalAmount) * 360f
                drawArc(
                    color = colors[iterator],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true // Close the arc to the center
                )
                val angle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val labelX = center.x + (radius * 0.8f) * cos(angle).toFloat()
                val labelY = center.y + (radius * 0.8f) * sin(angle).toFloat()
                val textPaint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                }

                // Draw Text Label
                drawContext.canvas.nativeCanvas.drawText(
                    "${(sweepAngle/360 * 100).toInt()}%",
                    labelX,
                    labelY,
                    textPaint
                )
                iterator++
                startAngle += sweepAngle
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        ) {
            var iterator = 0
            map.forEach { expenses ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colors[iterator])
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = " ${expenses.key}: ₹${expenses.value.sumOf { it.price }.toInt()}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                iterator++
            }
        }
    }
}