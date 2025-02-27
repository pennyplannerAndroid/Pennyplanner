package com.penny.planner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExpensePieChart(expenses: List<ExpenseCategory>, modifier: Modifier = Modifier) {
    val totalAmount = expenses.sumOf { it.amount }.toFloat()

    Canvas(modifier = modifier.size(400.dp)) {
        var startAngle = -90f // Start at the top
        expenses.forEach { expense ->
            val sweepAngle = (expense.amount / totalAmount) * 360f
            drawArc(
                color = expense.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true, // Close the arc to the center
            )
            startAngle += sweepAngle
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Legend
    Column(modifier = Modifier.padding(16.dp)) {
        expenses.forEach { expense ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(expense.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicText(text = " ${expense.category}: ₹${expense.amount}")
            }
        }
    }
}

data class ExpenseCategory(
    val category: String,
    val amount: Int,
    val color: Color
)

@Preview
@Composable
fun MonthlyExpensesScreen() {
    val expenses = listOf(
        ExpenseCategory("Rent", 25700, Color(0xFFE57373)),
        ExpenseCategory("Transport", 7000, Color(0xFF64B5F6)),
        ExpenseCategory("Food", 10000, Color(0xFF81C784)),
        ExpenseCategory("Entertainment", 5000, Color(0xFFFFB74D)),
        ExpenseCategory("Utilities", 3000, Color(0xFFBA68C8))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExpensePieChart(
            expenses = expenses,
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )
    }
}