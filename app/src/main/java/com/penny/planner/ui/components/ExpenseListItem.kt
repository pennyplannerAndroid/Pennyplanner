package com.penny.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.helpers.Utils

@Composable
fun ExpenseListItem(
    item: ExpenseEntity
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(48.dp)
                .background(
                    color = colorResource(id = R.color.transparent_60),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = item.icon,
                fontSize = 24.sp
            )
        }
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    text = item.category,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.expense_text_color)
                )
                Text(
                    modifier = Modifier
                        .padding(4.dp),
                    text = "${Utils.RUPEE}${item.price}",
                    maxLines = 1,
                    fontSize = 16.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    text = item.subCategory.ifEmpty { item.content },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = colorResource(id = R.color.or_with_color)
                )
                Text(
                    modifier = Modifier
                        .padding(4.dp),
                    text = Utils.convertMillisToTime(item.time),
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = colorResource(id = R.color.or_with_color)
                )
            }
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