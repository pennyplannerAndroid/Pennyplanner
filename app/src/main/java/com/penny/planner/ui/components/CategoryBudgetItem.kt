package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.models.CategoryExpenseModel

@Composable
fun CategoryBudgetItem(
    categoryExpenseModel: CategoryExpenseModel,
    onClick: () -> Unit
) {
    val isAboveLimit = categoryExpenseModel.expenses >= categoryExpenseModel.spendLimit
    val progress = (categoryExpenseModel.expenses/categoryExpenseModel.spendLimit).toFloat()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
            .background(
                color = colorResource(id = R.color.white),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(id = R.color.white))
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.textField_border),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(start = 8.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.selected_dot),
                        contentDescription = "",
                        tint = if (isAboveLimit) colorResource(id = R.color.limit_cross_color) else colorResource(id = R.color.loginText)
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        text = "${categoryExpenseModel.icon} ${categoryExpenseModel.category}"
                    )
                }
                if (isAboveLimit) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.error_image),
                        contentDescription = ""
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                text = String.format(
                    stringResource(id = R.string.remaining_amount),
                    (categoryExpenseModel.spendLimit - categoryExpenseModel.expenses).coerceAtLeast(0.0).toInt()
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
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
                            .background(colorResource(id = if (isAboveLimit) R.color.limit_cross_color else R.color.loginText))
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically),
                    text = "${(progress * 100).toInt()}%",
                    color = colorResource(id = if (isAboveLimit) R.color.limit_cross_color else R.color.loginText),
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = "₹${categoryExpenseModel.expenses.toInt()} of ₹${categoryExpenseModel.spendLimit.toInt()}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.or_with_color)
                )
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = String.format(stringResource(id = R.string.transaction_count), categoryExpenseModel.totalTransactions),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = colorResource(id = if (isAboveLimit) R.color.limit_cross_color else R.color.loginText)
                )
            }
            if (isAboveLimit) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = stringResource(id = R.string.exceed_limit_warning_text),
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.above_limit_text_color)
                )
            }
        }
    }
}

@Composable
fun GroupBudgetHeader(
    groupBudgetDetails: CategoryExpenseModel
) {
    val isAboveLimit = groupBudgetDetails.expenses >= groupBudgetDetails.spendLimit
    val progress = (groupBudgetDetails.expenses / groupBudgetDetails.spendLimit).toFloat()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            text = groupBudgetDetails.category,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
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
                        .background(colorResource(id = if (isAboveLimit) R.color.limit_cross_color else R.color.expense_text_color))
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically),
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "₹${groupBudgetDetails.expenses.toInt()} of ₹${groupBudgetDetails.spendLimit.toInt()}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = colorResource(id = R.color.loginButton)
            )
            Text(
                text = String.format(
                    stringResource(id = R.string.transaction_count),
                    groupBudgetDetails.totalTransactions
                ),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = colorResource(id = if (isAboveLimit) R.color.limit_cross_color else R.color.loginButton)
            )
        }
    }
}

@Preview
@Composable
fun PreviewCategoryBudgetItem() {
    Column(
        modifier = Modifier
            .background(color = colorResource(id = R.color.loginText))
    ) {
        GroupBudgetHeader(
            CategoryExpenseModel(
                id = 1,
                category = "Shopping",
                spendLimit = 5000.0,
                entityId = "",
                alertAdded = true,
                alertLimit = 80,
                icon = Utils.DEFAULT_ICON,
                expenses = 6000.0,
                totalTransactions = 5
            )
        )
    }
}