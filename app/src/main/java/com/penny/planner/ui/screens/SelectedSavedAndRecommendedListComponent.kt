package com.penny.planner.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.ui.components.CategoryListViewItem
import com.penny.planner.ui.components.CircularButtonWithIcon
import com.penny.planner.ui.components.PreSelectedItem

@Composable
fun SelectedSavedAndRecommendedListComponent(
    addNeeded: Boolean,
    title: String,
    selectedItem: Pair<String, String>,
    recommendedList: Map<String, String>,
    savedList: Map<String, String>,
    selectedItemDeleted: () -> Unit,
    editNeeded: Boolean,
    editClicked: () -> Unit,
    savedItemClicked: (String) -> Unit,
    recommendedItemClicked: (String) -> Unit,
    onAddClicked: () -> Unit
) {
    var searchMode by remember {
        mutableStateOf(false)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(bottom = 4.dp)) {
                CircularButtonWithIcon(
                    icon = R.drawable.search_without_background,
                    contentDescription = stringResource(id = R.string.search)) {
                    searchMode = true
                }
                if (addNeeded) {
                    CircularButtonWithIcon(
                        icon = R.drawable.add_without_background,
                        contentDescription = stringResource(id = R.string.add),
                        onClick = onAddClicked)
                }
            }
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        AnimatedVisibility(searchMode) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = colorResource(id = R.color.loginButton),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .weight(1f)
                ) {
                    Image(
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                        painter = painterResource(id = R.drawable.search_without_background),
                        contentDescription = stringResource(id = R.string.search)
                    )
                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            if (it.length < 20) {
                                searchText = it
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .height(32.dp)
                            .weight(1f),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                if (searchText.isEmpty()) {
                                    Text(text = stringResource(id = R.string.search), color = Color.Black)
                                }
                                innerTextField()
                            }
                        }
                    )
                    if (searchText.isNotEmpty()) {
                        Image(
                            modifier = Modifier
                                .padding(start = 4.dp, top = 4.dp, end = 4.dp)
                                .clickable {
                                    searchText = ""
                                },
                            painter = painterResource(id = R.drawable.cancel_button),
                            contentDescription = stringResource(id = R.string.cancel)
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            searchMode = false
                        }
                        .padding(start = 12.dp),
                    text = stringResource(id = R.string.cancel),
                    color = colorResource(id = R.color.loginText),
                    maxLines = 1
                )
            }
        }
        if (selectedItem.first.isNotEmpty()) {
            PreSelectedItem(
                item = selectedItem,
                editNeeded = editNeeded,
                deleteClicked = selectedItemDeleted,
                onEditClicked = editClicked
            )
        }
        CategoryListViewItem(
            title = stringResource(id = R.string.saved),
            categoryList = if (searchText.isEmpty()) savedList else Utils.filterMap(savedList, searchText)
        ) {
            savedItemClicked(it)
        }

        CategoryListViewItem(
            title = stringResource(id = R.string.recommended),
            categoryList = if (searchText.isEmpty()) recommendedList else Utils.filterMap(recommendedList, searchText)
        ) {
            recommendedItemClicked(it)
        }
    }
}


@Preview
@Composable
fun PreviewSelectedSavedAndRecommended() {
    SelectedSavedAndRecommendedListComponent(
        addNeeded = true,
        title = "Select a Category",
        selectedItem = Pair("Food", "\uD83C\uDF7D"),
        recommendedList = mapOf(
            "1" to "1", "2" to "2", "3" to "3", "4" to "4", "5" to "5"),
        savedList = mapOf(
            "1" to "1", "2" to "2", "3" to "3", "4" to "4", "5" to "5"),
        selectedItemDeleted = {},
        editNeeded = true,
        editClicked = {},
        savedItemClicked = {},
        recommendedItemClicked = {},
    ) {

    }
}