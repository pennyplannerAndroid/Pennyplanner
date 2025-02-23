package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel

@Composable
fun ExpenseListItem(
    item: GroupDisplayModel
) {
    Column {
        Row(
            modifier = Modifier
                    .width((LocalConfiguration.current.screenWidthDp * 0.6).dp)
                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = "${Utils.RUPEE}${item.price.toInt()}",
                maxLines = 1,
                fontSize = 24.sp,
                color = colorResource(R.color.loginText),
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically),
                text = "paid by ${item.paymentType}",
                maxLines = 1,
                fontSize = 13.sp,
                color = colorResource(id = R.color.or_with_color),
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier
                .width((LocalConfiguration.current.screenWidthDp * 0.6).dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = colorResource(id = R.color.loginButton),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = item.icon,
                    fontSize = 24.sp
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.category,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.expense_text_color),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.subCategory.ifEmpty { item.content },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = colorResource(id = R.color.or_with_color)
                )
            }
        }
    }
}

@Composable
fun ExpenseListItem(
    item: ExpenseEntity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = colorResource(id = R.color.loginButton),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = item.icon,
                fontSize = 24.sp
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.category,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 16.sp,
                color = colorResource(id = R.color.expense_text_color)
            )
            Text(
                text = item.subCategory.ifEmpty { item.content },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 13.sp,
                color = colorResource(id = R.color.or_with_color)
            )
        }
        Column(
            modifier = Modifier
                .padding(4.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${Utils.RUPEE}${item.price.toInt()}",
                maxLines = 1,
                fontSize = 16.sp,
                color = Color.Red,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = Utils.convertMillisToTime(item.time.toDate()),
                maxLines = 1,
                fontSize = 13.sp,
                color = colorResource(id = R.color.or_with_color)
            )
        }
    }
}

@Preview
@Composable
fun PreviewExpenseItem() {
    ExpenseListItem(
        item = ExpenseEntity(
            content = "Date Night",
            category = "Food",
            subCategory = "Restaurant",
            price = 500.0,
            icon = Utils.DEFAULT_ICON,
            paymentType = "UPI"
        )
    )

}

@Preview
@Composable
fun PreviewGroupExpenseItem() {
    ExpenseListItem(
        item = GroupDisplayModel(
            content = "Date Night",
            category = "Food",
            subCategory = "Restaurant",
            price = 500.0,
            icon = Utils.DEFAULT_ICON,
            paymentType = "UPI",
            isSentTransaction = true
        )
    )

}