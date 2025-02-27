package com.penny.planner.ui.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
    expenseModeEnabled: (Boolean) -> Unit
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var searchMode by remember {
        mutableStateOf(false)
    }
    var expenseMode by remember {
        mutableStateOf(false)
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
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
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
                ShowGroupPicture(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically),
                    imageUrl = group.localImagePath.ifEmpty { group.profileImage }
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .weight(1f),
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
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
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 4.dp),
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
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
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
        onClick = {}
    ) {}
}