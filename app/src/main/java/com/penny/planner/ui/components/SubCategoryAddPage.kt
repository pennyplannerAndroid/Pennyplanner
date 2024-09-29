package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.ui.enums.EnteredTextInfo

@Composable
fun SubCategoryAddPage(
    emojis: List<String>,
    selectedSubCategory: SubCategoryEntity?,
    savedList: Map<String, String>,
    recommendedList: Map<String, String>,
    onBack: () -> Unit,
    onAddClicked: (String, String, Boolean) -> Unit
) {
    var showEmojiList by remember {
        mutableStateOf(false)
    }
    var value by remember {
        mutableStateOf(selectedSubCategory?.name ?: "")
    }
    var icon by remember {
        mutableStateOf(selectedSubCategory?.icon ?: Utils.DEFAULT_ICON)
    }
    var enteredTextInfo by remember {
        mutableStateOf(EnteredTextInfo.VALID)
    }

    Box(modifier = Modifier) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, end = 24.dp)
                        .size(32.dp)
                        .clickable(onClick = onBack),
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = stringResource(id = R.string.back)
                )
                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = stringResource(id = R.string.add),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorResource(id = R.color.textField_border),
                    focusedBorderColor = colorResource(
                        id = if (enteredTextInfo == EnteredTextInfo.SAVED) R.color.red else R.color.loginText)
                ),
                leadingIcon = {
                    Row(
                        modifier = Modifier
                            .clickable {
                                showEmojiList = true
                            }
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = icon,
                            fontSize = 16.sp
                        )
                        Icon(
                            tint = colorResource(id = R.color.loginText),
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Bottom),
                            painter = painterResource(id = R.drawable.down_arrow_icon),
                            contentDescription = ""
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                value = value,
                onValueChange = {
                    if (it.length < 20) value = it
                    enteredTextInfo = if (savedList.filter { savedItem -> savedItem.key.lowercase() == it.lowercase() }.isNotEmpty())
                        EnteredTextInfo.SAVED
                    else if (recommendedList.filter { recommendedItem -> recommendedItem.key.lowercase() == it.lowercase() }.isNotEmpty())
                        EnteredTextInfo.RECOMMENDED
                    else
                        EnteredTextInfo.VALID
                },
                label = {
                    Text(stringResource(id = R.string.sub_category))
                },
                singleLine = true
            )
            TextFieldErrorIndicator(
                modifier = Modifier,
                textRes = if (enteredTextInfo == EnteredTextInfo.SAVED)
                    R.string.name_match_error_subcategory else R.string.suggested_text_subcategory,
                show = enteredTextInfo != EnteredTextInfo.VALID,
                showAsSuggestion = enteredTextInfo == EnteredTextInfo.RECOMMENDED,
                onClick = {
                    val item = recommendedList.filter { it.key.lowercase() == value.lowercase() }.toList()[0]
                    onAddClicked.invoke(item.first, item.second, false)
                }
            )
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                    .size(48.dp),
                textRes = R.string.add,
                onClick = { onAddClicked.invoke(value, icon, true) },
                enabled = value.isNotEmpty()
            )
        }
        if (showEmojiList) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.BottomEnd)
                    .padding(start = 24.dp, end = 48.dp)
                    .background(color = Color.White)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(5)) {
                    items(emojis) { item ->
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    icon = item
                                    showEmojiList = false
                                },
                            text = item
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSubCategoryAddPage() {
    SubCategoryAddPage(
        emojis = listOf(),
        selectedSubCategory = SubCategoryEntity(),
        savedList = mapOf(),
        recommendedList = mapOf(),
        onBack = { }) { _, _, _->

    }
}