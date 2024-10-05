package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
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
import com.penny.planner.models.NameIconPairWithKeyModel

@Composable
fun CategoryListViewItem(
    title: String,
    categoryList: List<NameIconPairWithKeyModel>,
    onItemClick: (String, String) -> Unit
) {
    var viewMore by remember {
        mutableStateOf(
            false
        )
    }
    if (categoryList.isNotEmpty()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .background(color = Color(0xFFCCC2DC))
            ) {
                Text(
                    modifier = Modifier.padding(start = 18.dp, top = 4.dp, bottom = 4.dp),
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp, top = 18.dp),
                    columns = GridCells.Fixed(count = 2)
                ) {
                    if (!viewMore && categoryList.size > 4) {
                        items(categoryList.toList().subList(0, 4)) { item ->
                            CategorySingleItem(
                                modifier = Modifier,
                                item = item.name to item.icon
                            ) {
                                onItemClick.invoke(item.name, item.icon)
                            }
                        }
                    } else {
                        items(categoryList.toList()) { item ->
                            CategorySingleItem(
                                modifier = Modifier,
                                item = item.name to item.icon
                            ) {
                                onItemClick.invoke(item.name, item.icon)
                            }
                        }
                    }
                }
            }

            if (categoryList.size > 4) {
                Row {
                    Text(
                        modifier = Modifier
                            .padding(start = 18.dp, end = 4.dp, bottom = 4.dp)
                            .clickable {
                                viewMore = !viewMore
                            },
                        text = stringResource(id = if (viewMore) R.string.view_less else R.string.view_more),
                        color = Color.Blue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        tint = colorResource(id = R.color.loginText),
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = if (viewMore) R.drawable.arrow_up_icon else R.drawable.down_icon),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
fun PreSelectedItem(
    item: Pair<String, String>,
    editNeeded: Boolean,
    deleteClicked: () -> Unit,
    onEditClicked: () -> Unit,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFCCC2DC))
        ) {
            Text(
                modifier = Modifier.padding(start = 18.dp, top = 4.dp, bottom = 4.dp),
                text = stringResource(id = R.string.currently_selected),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            CategorySingleItem(
                modifier = Modifier.align(Alignment.BottomStart),
                item = Pair(item.first, item.second)
            ) {}
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                if(editNeeded) {
                    CircularButtonWithIcon(
                        icon = R.drawable.edit_icon,
                        contentDescription = stringResource(id = R.string.delete),
                        onClick = onEditClicked
                    )
                }
                CircularButtonWithIcon(
                    icon = R.drawable.delete_icon,
                    contentDescription = stringResource(id = R.string.delete),
                    onClick = deleteClicked
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewListItem() {
    CategoryListViewItem(
        title = "Recommended",
        categoryList = listOf(
            NameIconPairWithKeyModel(name = "Food"),
            NameIconPairWithKeyModel(name = "Travel"),
            NameIconPairWithKeyModel(name = "Bills"),
            NameIconPairWithKeyModel(name = "Rent"),
            NameIconPairWithKeyModel(name = "Gym")
        )
    ) { _, _ ->

    }
}

@Preview
@Composable
fun PreviewSelectedListItem() {
    PreSelectedItem(
        item = Pair(
            first = "Food",
            second = "HH"
        ),
        editNeeded = true,
        onEditClicked = {
        },
        deleteClicked = {

        }
    )
}