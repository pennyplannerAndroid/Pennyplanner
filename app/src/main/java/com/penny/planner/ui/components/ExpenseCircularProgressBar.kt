package com.penny.planner.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R

@Composable
fun ExpenseCircularProgressBar(
    expensePercent: Float, // Value between 0f and 1f
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 10.dp,
    percentageColor: Color = Color.Black,
    backgroundColor: Color = colorResource(id = R.color.textField_border),
    onClick: () -> Unit
) {
    val size = LocalConfiguration.current.screenWidthDp / 4
    val animatedProgress by animateFloatAsState(
        targetValue = expensePercent,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ), label = "ExpenseProgress"
    )
    val color = getColorForCircularExpenseBar(progress = (expensePercent * 100).toInt())
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = 360 * animatedProgress
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)

            // Draw background circle
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )

            // Draw progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = stroke
            )
        }

        // Display percentage text
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = percentageColor,
                fontSize = 18.sp
            )
        )
    }
}

@Preview
@Composable
fun PreviewExpenseCircle() {
    ExpenseCircularProgressBar(expensePercent = 0.75f) {}
}
