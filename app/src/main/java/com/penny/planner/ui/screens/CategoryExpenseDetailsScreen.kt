package com.penny.planner.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel
import com.penny.planner.ui.components.ColoredTopBar
import com.penny.planner.ui.components.ExpenseListItem
import com.penny.planner.ui.components.ExpensePieChart

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CategoryExpenseDetailsScreen(
    category: String,
    expenses: List<GroupDisplayModel>?,
    onDismiss: () -> Unit
) {
    BackHandler(
        onBack = {
            onDismiss.invoke()
        }
    )
    Scaffold(
        topBar = {
            ColoredTopBar(modifier = Modifier, title = category, color = colorResource(id = R.color.loginText)) {
                onDismiss.invoke()
            }
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.loginText))
            ) {
                if (expenses != null)
                    ExpensePieChart(expenses, Modifier.fillMaxWidth())
                Card(
                    modifier = Modifier
                        .padding(contentPadding)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
                    colors = CardDefaults.cardColors()
                        .copy(containerColor = colorResource(id = R.color.loginButton))
                ) {
                    LazyColumn {
                        if (!expenses.isNullOrEmpty()) {
                            items(expenses) { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .align(
                                                if (item.isSentTransaction) Alignment.End else Alignment.Start
                                            ),
                                        colors = CardDefaults
                                            .cardColors()
                                            .copy(
                                                containerColor = if (item.isSentTransaction) CardDefaults.cardColors().containerColor else Color.White
                                            ),
                                        elevation = CardDefaults.elevatedCardElevation(
                                            defaultElevation = 24.dp
                                        )
                                    ) {
                                        Box {
                                            Column {
                                                if (!item.isSentTransaction) {
                                                    Row(
                                                        modifier = Modifier
                                                            .align(Alignment.CenterHorizontally)
                                                            .background(color = colorResource(id = R.color.group_list_item_top))
                                                            .padding(start = 6.dp, top = 6.dp)
                                                    ) {
                                                        GlideImage(
                                                            modifier = Modifier
                                                                .size(32.dp)
                                                                .align(Alignment.CenterVertically)
                                                                .border(
                                                                    color = colorResource(id = R.color.textField_border),
                                                                    width = 2.dp,
                                                                    shape = CircleShape
                                                                )
                                                                .clip(CircleShape),
                                                            model = item.localImagePath,
                                                            contentDescription = "",
                                                            contentScale = ContentScale.Crop
                                                        ) {
                                                            it.load(item.localImagePath)
                                                                .placeholder(R.drawable.default_user_display)
                                                                .error(R.drawable.default_user_display)
                                                        }
                                                        Text(
                                                            modifier = Modifier
                                                                .padding(start = 4.dp)
                                                                .align(Alignment.CenterVertically),
                                                            text = item.senderName,
                                                            maxLines = 1,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colorResource(id = R.color.loginText),
                                                            textAlign = TextAlign.Start
                                                        )
                                                    }
                                                }
                                                if (item.entityType == 0) {
                                                    Text(
                                                        modifier = Modifier
                                                            .align(if (item.isSentTransaction) Alignment.End else Alignment.Start)
                                                            .padding(
                                                                start = 6.dp,
                                                                end = 6.dp,
                                                                top = 6.dp
                                                            )
                                                            .widthIn(
                                                                max = (LocalConfiguration.current.screenWidthDp * 0.6).dp
                                                            ),
                                                        text = item.content,
                                                        color = Color.Black
                                                    )
                                                } else {
                                                    ExpenseListItem(item = item)
                                                }
                                                Text(
                                                    modifier = Modifier
                                                        .align(Alignment.End)
                                                        .padding(
                                                            start = 12.dp,
                                                            end = 8.dp,
                                                            bottom = 4.dp
                                                        ),
                                                    text = Utils.formatFirebaseTimestampToProperTime(
                                                        item.time
                                                    ),
                                                    maxLines = 1,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = colorResource(id = R.color.or_with_color),
                                                    textAlign = TextAlign.End
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    )
}