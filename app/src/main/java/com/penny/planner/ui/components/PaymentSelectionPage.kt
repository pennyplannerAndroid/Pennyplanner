package com.penny.planner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSelectionPage (
    title: String,
    list: List<String>?,
    onDismiss: () -> Unit,
    enabled: Boolean,
    onItemClicked: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (sheetState.isVisible && !enabled) {
        LaunchedEffect(key1 = "") {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss.invoke()
                }
            }
        }
    } else if (enabled){
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(24.dp),
                    columns = GridCells.Fixed(count = 2)
                ) {
                    if (list != null) {
                        items(list) { item ->
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .clickable {
                                        onItemClicked(item)
                                    }
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    text = item,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomSheetWithName() {
    PaymentSelectionPage(
        "Sub Categories",
        listOf("UPI", "Cash", "Card"),
        {},
        true
    ) {

    }
}