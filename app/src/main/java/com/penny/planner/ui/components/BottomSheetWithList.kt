package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.data.db.category.CategoryEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetWithList (
    title: String,
    list: List<CategoryEntity>?,
    addNeeded: Boolean,
    onDismiss: () -> Unit,
    showSheet: Boolean,
    onItemClicked: (String, String) -> Unit
) {
    var add by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (sheetState.isVisible && !showSheet) {
        LaunchedEffect(key1 = "") {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss.invoke()
                }
            }
        }
    } else if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            if (add) {
                CategoryAddPage(
                    limit = true,
                    onBack = { add = false }) { name, limit ->
                    onItemClicked(name, limit)
                }
            } else {
                CategorySelectionPage(
                    addNeeded = addNeeded,
                    title = title,
                    list = list,
                    onItemClicked = { onItemClicked(it.name, it.limit) }
                ) {
                    add = true
                }
            }
        }
    }
}

@Composable
fun CategorySelectionPage(
    addNeeded: Boolean,
    title: String,
    list: List<CategoryEntity>?,
    onItemClicked: (CategoryEntity) -> Unit,
    onAddClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (addNeeded) {
            Image(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 24.dp)
                    .size(32.dp)
                    .clickable(onClick = onAddClicked),
                painter = painterResource(id = R.drawable.add_group_icon),
                contentDescription = stringResource(id = R.string.add)
            )
        }
        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
    LazyVerticalGrid(
        modifier = Modifier
            .padding(24.dp),
        columns = GridCells.Fixed(count = 2)
    ) {
        if (list != null) {
            itemsIndexed(list) { index, item ->
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable {
                            onItemClicked(item)
                        }
                ) {
                    Image(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        painter = painterResource(id = R.drawable.budget_unselected_icon),
                        contentDescription = item.name
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = item.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewBottomSheetWithList() {
//    BottomSheetWithList(
//        "Categories",
//        listOf("Gallery", "Camera", "Delete"),
//        listOf(R.drawable.gallery_icon, R.drawable.camera_icon, R.drawable.delete_icon),
//        true,
//        {},
//        true
//    ) {
//
//    }
}