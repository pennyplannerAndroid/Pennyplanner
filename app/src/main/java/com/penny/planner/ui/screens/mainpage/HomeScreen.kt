package com.penny.planner.ui.screens.mainpage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.ui.components.CircularBudgetItem
import com.penny.planner.ui.components.ExpenseListItem
import com.penny.planner.viewmodels.ExpenseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    addExpenseClick: () -> Unit) {
    val viewModel = hiltViewModel<ExpenseViewModel>()
    val scope = rememberCoroutineScope()
    var expenseList by remember {
        mutableStateOf(listOf<ExpenseEntity>())
    }
    val lifeCycle = LocalLifecycleOwner.current
    LaunchedEffect(keys = emptyArray()) {
        scope.launch {
            viewModel.getAllExpense().observe(lifeCycle) {
                expenseList = it
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Hi ${viewModel.getName()}") },
                actions = {
                    Image(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(id = R.drawable.notification_icon),
                        contentDescription = stringResource(id = R.string.notification)
                    )
                },
                navigationIcon = {
                    GlideImage(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(32.dp)
                            .border(
                                color = colorResource(id = R.color.textField_border),
                                width = 2.dp,
                                shape = CircleShape
                            )
                            .clip(CircleShape),
                        model = R.drawable.default_user_display,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    ) {
                        it.load(viewModel.getPicturePath())
                            .placeholder(R.drawable.default_user_display)
                            .error(R.drawable.default_user_display)
                    }
                },
            )
        }
    ) { contentPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Expense in ${getCalculatedMonths()}"
                )
                CircularBudgetItem(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    progress = 0.5
                ) {

                }
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = "Recent Transactions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                LazyColumn {
                    items(expenseList) {
                        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top= 8.dp, bottom = 8.dp)) {
                            ExpenseListItem(it)
                        }
                    }
                }
            }
            FloatingActionButton(
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = { addExpenseClick.invoke() },
            ) {
                Image(painter = painterResource(id = R.drawable.add_group_icon), contentDescription = stringResource(
                    id = R.string.add_expense
                ))
            }
        }
    }
}

@Composable
@SuppressLint("SimpleDateFormat")
fun getCalculatedMonths(): String {
    return SimpleDateFormat("MMMM").format(Calendar.getInstance().time)
}

@Preview
@Composable
fun PreviewHomeScreen() {
//    HomeScreen(MainActivityViewModel())
}
