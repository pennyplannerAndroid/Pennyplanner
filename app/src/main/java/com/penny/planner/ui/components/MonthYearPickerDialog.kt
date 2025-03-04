package com.penny.planner.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun MonthYearPicker() {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (1900..currentYear).toList().reversed() // List of years (1900 to Current Year)

    var selectedMonth by remember { mutableStateOf(months[0]) }
    var selectedYear by remember { mutableStateOf(currentYear) }

    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Month Picker Dropdown
        Box {
            Button(onClick = { expandedMonth = true }) {
                Text(text = "Month: $selectedMonth")
            }
            DropdownMenu(expanded = expandedMonth, onDismissRequest = { expandedMonth = false }) {
                months.forEach { month ->
                    DropdownMenuItem(text = { Text(month) }, onClick = {
                        selectedMonth = month
                        expandedMonth = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Year Picker Dropdown
        Box {
            Button(onClick = { expandedYear = true }) {
                Text(text = "Year: $selectedYear")
            }
            DropdownMenu(expanded = expandedYear, onDismissRequest = { expandedYear = false }) {
                years.forEach { year ->
                    DropdownMenuItem(text = { Text(year.toString()) }, onClick = {
                        selectedYear = year
                        expandedYear = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Selected Month and Year
        Text(text = "Selected: $selectedMonth $selectedYear", fontSize = 20.sp)
    }
}
