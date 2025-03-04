package com.penny.planner.ui.screens

import android.app.Activity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.models.CategoryExpenseModel
import com.penny.planner.ui.components.CategoryBudgetItem
import com.penny.planner.ui.components.ColoredTopBar
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.GroupBudgetHeader
import com.penny.planner.viewmodels.BudgetAndCategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CategoryWiseBudgetScreen(entityId: String) {
    val scope = rememberCoroutineScope()
    val month by remember {
        mutableStateOf(Utils.getCurrentMonthYear())
    }
    var showLoader by remember {
        mutableStateOf(true)
    }
    var showCategoryDetailPage by remember {
        mutableStateOf(false)
    }
    var selectedCategory by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val view = LocalView.current
    var categoryDetailList by remember {
        mutableStateOf<List<CategoryExpenseModel>>(mutableListOf())
    }
    val viewModel = hiltViewModel<BudgetAndCategoryViewModel>()
    var groupBudgetDetails by remember {
        mutableStateOf(viewModel.getGroupBudgetDetails())
    }
    if (!showLoader) {
        groupBudgetDetails = viewModel.getGroupBudgetDetails()
        categoryDetailList = viewModel.getAllCategoryDetails()
    }

    SideEffect {
        val window = (context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    LaunchedEffect(key1 = true) {
        scope.launch(Dispatchers.IO) {
            viewModel.build(entityId = entityId)
            showLoader = false
        }
    }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    if (showCategoryDetailPage) {
        CategoryExpenseDetailsScreen(
            category = selectedCategory,
            expenses = viewModel.getALlExpenses(selectedCategory)
        ) {
            showCategoryDetailPage = false
        }
    } else {
        Scaffold(
            topBar = {
                ColoredTopBar(
                    modifier = Modifier,
                    title = month,
                    color = colorResource(id = R.color.loginText),
                    titleClickable = true,
                    onTitleClicked = {

                    },
                    onBackPressed = {
                        backDispatcher?.onBackPressed()
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(color = colorResource(id = R.color.loginText))
                ) {
                    GroupBudgetHeader(groupBudgetDetails = groupBudgetDetails)
                    Column(
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            )
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            contentPadding = PaddingValues(top = 12.dp)
                        ) {
                            items(categoryDetailList) {
                                CategoryBudgetItem(categoryExpenseModel = it) {
                                    selectedCategory = it.category
                                    showCategoryDetailPage = true
                                }
                            }
                        }
                    }
                }
            }
        )
        FullScreenProgressIndicator(show = showLoader)
    }
}

@Preview
@Composable
fun PreviewCategoryWiseBudgetScreen() {
    CategoryWiseBudgetScreen(
        entityId = ""
    )
}