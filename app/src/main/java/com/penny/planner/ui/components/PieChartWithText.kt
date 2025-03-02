package com.penny.planner.ui.components

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

data class PieChartData(val value: Float, val color: Color, val label: String)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier.size(250.dp)
) {
    val totalSum = data.sumOf { it.value.toDouble() }.toFloat()
    val animatedProgress by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(1f, animationSpec = tween(1000))
    }

    Canvas(modifier = modifier) {
        val size = 500f
        val radius = size / 2
        val center = Offset(radius, radius)

        var startAngle = -90f

        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        data.forEach { item ->
            val sweepAngle = (item.value / totalSum) * 360f * animatedProgress.value

            // Draw Pie Slice
            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size, size)
            )

            // Calculate Label Position
            val angle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val labelX = center.x + (radius * 0.6f) * cos(angle).toFloat()
            val labelY = center.y + (radius * 0.6f) * sin(angle).toFloat()

            // Draw Text Label
            drawContext.canvas.nativeCanvas.drawText(
                item.label,
                labelX,
                labelY,
                textPaint
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun PieChartDemo() {
    val pieData = listOf(
        PieChartData(40f, Color.Red, "Red"),
        PieChartData(30f, Color.Blue, "Blue"),
        PieChartData(20f, Color.Green, "Green"),
        PieChartData(10f, Color.Yellow, "Yellow")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pie Chart Example", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        PieChart(data = pieData)
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPieChart() {
    PieChartDemo()
}
