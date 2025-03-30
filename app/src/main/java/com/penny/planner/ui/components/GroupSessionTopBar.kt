package com.penny.planner.ui.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.noRippleClickable
import com.penny.planner.ui.screens.ShowGroupPicture

@Composable
fun GroupSessionTopBar(
    adminApprovals: Boolean,
    group: GroupEntity,
    monthlyExpenseEntity: MonthlyExpenseEntity,
    memberClick: () -> Unit,
    onClick: () -> Unit,
    circularBarClicked: () -> Unit,
    searchEnabled: (Boolean) -> Unit,
    searchTextChanged: (String) -> Unit,
    expenseModeEnabled: (Boolean) -> Unit
) {
    val progress = (monthlyExpenseEntity.expense / group.monthlyBudget).toFloat()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var searchMode by remember {
        mutableStateOf(false)
    }
    var expenseMode by remember {
        mutableStateOf(false)
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    if (searchText.isNotEmpty()) {
        searchTextChanged.invoke(searchText.lowercase())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
        colors = CardDefaults.cardColors()
            .copy(containerColor = Color.White)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            backDispatcher?.onBackPressed()
                        }
                        .align(Alignment.CenterVertically),
                    tint = Color.Black
                )
                AnimatedVisibility(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                    visible = !searchMode,
                    enter = expandHorizontally(expandFrom = Alignment.Start),
                    exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
                ) {
                    Row {
                        ShowGroupPicture(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .align(Alignment.CenterVertically),
                            imageUrl = group.localImagePath.ifEmpty { group.profileImage }
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            text = group.name,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                        .align(Alignment.CenterVertically),
                    visible = searchMode,
                    enter = expandHorizontally(expandFrom = Alignment.CenterHorizontally),
                    exit = shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                            .padding(start = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    color = colorResource(id = R.color.loginButton),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(start = 4.dp, top = 4.dp)
                                    .align(Alignment.CenterVertically),
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
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
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
                                        }
                                        .align(Alignment.CenterVertically),
                                    painter = painterResource(id = R.drawable.cancel_button),
                                    contentDescription = stringResource(id = R.string.cancel)
                                )
                            }
                        }
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    searchEnabled.invoke(false)
                                    searchMode = false
                                }
                                .padding(start = 12.dp),
                            text = stringResource(id = R.string.cancel),
                            color = colorResource(id = R.color.loginText),
                            maxLines = 1
                        )
                    }
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically),
                    visible = !searchMode,
                    enter = expandHorizontally(expandFrom = Alignment.End),
                    exit = shrinkHorizontally(shrinkTowards = Alignment.End)
                ) {
                    Row {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    searchEnabled.invoke(true)
                                    searchMode = true
                                },
                            tint = Color.Black,
                            painter = painterResource(id = R.drawable.search_without_background),
                            contentDescription = stringResource(id = R.string.search)
                        )
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp)
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.group_notification),
                            contentDescription = "",
                            tint = Color.Black
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = fadeOut()
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 12.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                text = String.format(
                                    stringResource(id = R.string.expense_in_month),
                                    Utils.getCurrentMonthShort()
                                ),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                text = "${Utils.RUPEE}${monthlyExpenseEntity.expense.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier,
                                text = stringResource(id = R.string.budget),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                            Text(
                                modifier = Modifier,
                                text = "${Utils.RUPEE}${group.monthlyBudget.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        ExpenseCircularProgressBar(
                            Utils.getProgress(
                                group.monthlyBudget,
                                monthlyExpenseEntity.expense
                            ).toFloat()
                        ) {
                            circularBarClicked.invoke()
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !isExpanded,
                    enter = expandVertically(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            modifier = Modifier,
                            text = "${Utils.RUPEE}${monthlyExpenseEntity.expense.toInt()}",
                            color = colorResource(id = R.color.above_limit_text_color),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            modifier = Modifier,
                            text = " of ${Utils.RUPEE}${group.monthlyBudget.toInt()}",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 24.dp, end = 4.dp)
                                    .weight(1f)
                                    .height(8.dp)
                                    .background(
                                        color = colorResource(id = R.color.progress_background_color),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress.coerceAtMost(1f))
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colorResource(id = R.color.above_limit_text_color))
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .align(Alignment.CenterVertically),
                                text = "${(progress * 100).toInt()}%",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    Modifier
                        .clickable {
                            memberClick.invoke()
                        }
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 4.dp),
                        text = if (group.members.size == 1) "1 member" else "${group.members.size} members",
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (adminApprovals) colorResource(id = R.color.loginText)
                        else Color.Gray
                    )
                    if (adminApprovals) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 4.dp),
                            painter = painterResource(id = R.drawable.warning_icon),
                            contentDescription = "",
                            tint = Color.Red
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = stringResource(id = R.string.expense_mode),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Switch(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 4.dp),
                        checked = expenseMode,
                        onCheckedChange = {
                            expenseMode = it
                            expenseModeEnabled.invoke(expenseMode)
                        }
                    )
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.loginText)
                )
                .clickable {
                    isExpanded = !isExpanded
                }
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .rotate(if (isExpanded) 180f else 0f),
                    painter = painterResource(id = R.drawable.down_icon),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }

    }
}

@Preview
@Composable
fun PreviewGroupSessionTopBar() {
    GroupSessionTopBar(
        adminApprovals = true,
        group = GroupEntity(name = "Home Monthly Expenses", monthlyBudget = 80000.0),
        memberClick = {},
        monthlyExpenseEntity = MonthlyExpenseEntity(expense = 40000.0),
        onClick = {},
        circularBarClicked = {},
        searchEnabled = {},
        searchTextChanged = {}
    ) {}
}