package com.penny.planner.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.models.HomeNavigationItem
import com.penny.planner.ui.screens.AddNewGroupScreen
import com.penny.planner.ui.screens.mainpage.BudgetScreen
import com.penny.planner.ui.screens.mainpage.GroupScreen
import com.penny.planner.ui.screens.mainpage.HomeScreen
import com.penny.planner.ui.screens.mainpage.ProfileScreen
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val POSITION_HOME = 0
        const val POSITION_GROUP = 1
        const val POSITION_BUDGET = 2
        const val POSITION_PROFILE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[MainActivityViewModel::class]
        if (!viewModel.getIsUserLoggedIn()) {
            val intent = Intent(this, OnboardingActivity::class.java)
            intent.putExtra(Utils.NAVIGATION_DESTINATION, viewModel.getOnboardingNavigation())
            startActivity(intent)
            finish()
            return
        }
        enableEdgeToEdge()
        lifecycleScope.launch(Dispatchers.Main) {
            if (viewModel.getIsBudgetSet()) {
                setContent {
                    PennyPlannerTheme {
                        Home()
                    }
                }
            } else {
                val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
                intent.putExtra(Utils.NAVIGATION_DESTINATION, Utils.SET_MONTHLY_BUDGET)
                startActivity(intent)
                finish()
            }
        }
    }

    @Composable
    fun Home() {
        var showAddGroup by remember {
            mutableStateOf(false)
        }
        val pagerState = rememberPagerState(pageCount = { 4 })
        val scope = rememberCoroutineScope()
        val routes = listOf(
            HomeNavigationItem(name = Utils.HOME, selectedIcon = R.drawable.home_selected_icon, unselectedIcon = R.drawable.home_unselected_icon, position = POSITION_HOME),
            HomeNavigationItem(name = Utils.GROUPS, selectedIcon = R.drawable.group_selected_icon, unselectedIcon = R.drawable.group_unselected_icon, position = POSITION_GROUP),
            HomeNavigationItem(name = Utils.BUDGET, selectedIcon = R.drawable.budget_selected_icon, unselectedIcon = R.drawable.budget_unselected_icon, position = POSITION_BUDGET),
            HomeNavigationItem(name = Utils.PROFILE, selectedIcon = R.drawable.profile_selected_icon, unselectedIcon = R.drawable.profile_unselected_icon, position = POSITION_PROFILE),
        )
        if (showAddGroup) {
            AddNewGroupScreen {
                showAddGroup = false
            }
        } else {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        modifier = Modifier.navigationBarsPadding(),
                        backgroundColor = androidx.compose.ui.graphics.Color.White,
                        elevation = 16.dp
                    ) {
                        routes.forEach { item ->
                            BottomNavigationItem(
                                icon = {
                                    Image(
                                        modifier = Modifier.padding(top = 8.dp),
                                        painter = painterResource(
                                            id = if (pagerState.currentPage == item.position) item.selectedIcon
                                            else item.unselectedIcon
                                        ),
                                        contentDescription = item.name
                                    )
                                },
                                label = {
                                    Text(
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        text = item.name
                                    )
                                },
                                selected = pagerState.currentPage == item.position,
                                onClick = {
                                    scope.launch {
                                        pagerState.scrollToPage(item.position)
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                HorizontalPager(
                    modifier = Modifier,
                    state = pagerState
                ) { page ->
                    when (page) {
                        POSITION_HOME -> HomeScreen(modifier = Modifier.padding(innerPadding))
                        POSITION_GROUP -> GroupScreen(modifier = Modifier.padding(innerPadding)) {
                            showAddGroup = true
                        }
                        POSITION_BUDGET -> BudgetScreen()
                        POSITION_PROFILE -> ProfileScreen()
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Home()
    }

}