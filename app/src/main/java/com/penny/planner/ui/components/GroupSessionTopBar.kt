package com.penny.planner.ui.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.penny.planner.helpers.keyboardAsState
import com.penny.planner.helpers.noRippleClickable

@Composable
fun GroupSessionTopBar(
    adminApprovals: Boolean,
    group: GroupEntity,
    monthlyExpenseEntity: MonthlyExpenseEntity,
    memberClick: () -> Unit,
    onClick: () -> Unit,
    expandClicked: () -> Unit
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var isExpanded by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing), label = ""
    )
    val isKeyboardOpen by keyboardAsState()

    LaunchedEffect(key1 = true, key2 = isKeyboardOpen) {
        if (!isKeyboardOpen || !isExpanded) {
            isExpanded = !isExpanded
            expandClicked.invoke()
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onClick.invoke()
            }
            .statusBarsPadding(),
        shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(id = R.color.loginText))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            backDispatcher?.onBackPressed()
                        },
                    tint = Color.White
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                        .weight(1f),
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Row(
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                        .clickable {
                            memberClick.invoke()
                        }
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
                        color = if (adminApprovals) Color.Red else Color.LightGray
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
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Bottom)
                        .rotate(rotation)
                        .clickable {
                            isExpanded = !isExpanded
                            expandClicked.invoke()
                        },
                    painter = painterResource(id = R.drawable.open_menu_icon),
                    contentDescription = "",
                    tint = Color.White
                )
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
                        color = Color.LightGray
                    )
                    Text(
                        modifier = Modifier,
                        text = "${Utils.RUPEE}${group.monthlyBudget.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier,
                        text = String.format(stringResource(id = R.string.expense_in_month), Utils.getCurrentMonthShort()),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.LightGray
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = "${Utils.RUPEE}${monthlyExpenseEntity.expense.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                ExpenseCircularProgressBar(Utils.getProgress(group.monthlyBudget, monthlyExpenseEntity.expense).toFloat())

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