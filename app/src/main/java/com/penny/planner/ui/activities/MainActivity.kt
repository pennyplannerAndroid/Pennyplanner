package com.penny.planner.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.models.HomeNavigationItem
import com.penny.planner.ui.screens.AddExpenseScreen
import com.penny.planner.ui.screens.AddNewGroupScreen
import com.penny.planner.ui.screens.AdminApprovalScreen
import com.penny.planner.ui.screens.GroupSessionScreen
import com.penny.planner.ui.screens.mainpage.BudgetScreen
import com.penny.planner.ui.screens.mainpage.GroupScreen
import com.penny.planner.ui.screens.mainpage.HomeScreen
import com.penny.planner.ui.screens.mainpage.ProfileScreen
import com.penny.planner.ui.screens.mainpage.SplashScreen
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.ExpenseViewModel
import com.penny.planner.viewmodels.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val POSITION_HOME = 0
        const val POSITION_GROUP = 1
        const val POSITION_BUDGET = 2
        const val POSITION_PROFILE = 3
    }

    private var deeplink = ""
    private var navigationDestination = Utils.SPLASH_PAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        deeplink = if (intent.data?.lastPathSegment == Utils.JOIN) {
            intent.data?.getQueryParameter(Utils.GROUP_ID) ?: ""
        } else ""
        navigationDestination = intent.getStringExtra(Utils.NAVIGATION_DESTINATION) ?: Utils.SPLASH_PAGE
        lifecycleScope.launch(Dispatchers.Main) {
            setContent {
                PennyPlannerTheme {
                    MainPageNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        deeplink = if (intent.data?.lastPathSegment == Utils.JOIN) {
            intent.data?.getQueryParameter(Utils.GROUP_ID) ?: ""
        } else ""
        navigationDestination = Utils.MAIN_PAGE
        lifecycleScope.launch(Dispatchers.Main) {
            setContent {
                PennyPlannerTheme {
                    MainPageNavigation(true)
                }
            }
        }
    }

    @Composable
    fun MainPageNavigation(needNavigation: Boolean = false) {
        val viewModel = hiltViewModel<ExpenseViewModel>()
        val controller = rememberNavController()
        val scope = rememberCoroutineScope()
        NavHost(navController = controller, startDestination = navigationDestination) {
            composable(route = Utils.SPLASH_PAGE) {
                SplashScreen {
                    scope.launch (Dispatchers.IO) {
                        val destination = viewModel.needOnboardingNavigation()
                        if (destination != null) {
                            val intent = Intent(this@MainActivity, OnboardingActivity::class.java)
                            intent.putExtra(Utils.NAVIGATION_DESTINATION, destination)
                            startActivity(intent)
                            finish()
                        } else {
                            withContext(Dispatchers.Main) {
                                controller.navigate(Utils.MAIN_PAGE)
                            }
                        }
                    }
                }
            }
            composable(route = Utils.MAIN_PAGE) {
                Home(
                    createGroupClicked = { controller.navigate(Utils.CREATE_GROUP) },
                    addExpense = { controller.navigate(Utils.ADD_EXPENSE) },
                    openGroupSession = {
                        controller.navigate(
                            route = "${Utils.GROUP_SESSION}/${it}"
                        )
                    }
                )
            }
            composable(route = Utils.CREATE_GROUP) {
                AddNewGroupScreen {
                    controller.popBackStack()
                }
            }
            composable(route = Utils.ADD_EXPENSE) {
                AddExpenseScreen (
                    onDismiss = {
                        controller.popBackStack()
                    },
                    groupId = "",
                    addExpense = { expense ->
                        viewModel.addExpense(expense)
                        controller.popBackStack()
                    }
                )
            }
            composable(
                route = "${Utils.GROUP_SESSION}/{${Utils.GROUP_ID}}",
                arguments = listOf(
                    navArgument(Utils.GROUP_ID) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { it ->
                GroupSessionScreen(groupId = it.arguments?.getString(Utils.GROUP_ID) ?: "") {
                    controller.navigate(
                        route = "${Utils.PENDING_APPROVAL_PAGE}/$it"
                    )
                }
            }
            composable(
                route = "${Utils.PENDING_APPROVAL_PAGE}/{${Utils.GROUP_ID}}",
                arguments = listOf(
                    navArgument(Utils.GROUP_ID) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) {
                AdminApprovalScreen(groupId = it.arguments?.getString(Utils.GROUP_ID) ?: "")
            }
        }
        if (needNavigation) {
            if (controller.currentDestination?.route != Utils.MAIN_PAGE) {
                controller.popBackStack()
            }
        }
    }

    @Composable
    fun Home(
        createGroupClicked: () -> Unit,
        addExpense: () -> Unit,
        openGroupSession: (String) -> Unit
    ) {
        val pagerState = rememberPagerState(pageCount = { 4 })
        val scope = rememberCoroutineScope()
        val routes = listOf(
            HomeNavigationItem(name = Utils.HOME, selectedIcon = R.drawable.home_selected_icon, unselectedIcon = R.drawable.home_unselected_icon, position = POSITION_HOME),
            HomeNavigationItem(name = Utils.GROUPS, selectedIcon = R.drawable.group_selected_icon, unselectedIcon = R.drawable.group_unselected_icon, position = POSITION_GROUP),
            HomeNavigationItem(name = Utils.BUDGET, selectedIcon = R.drawable.budget_selected_icon, unselectedIcon = R.drawable.budget_unselected_icon, position = POSITION_BUDGET),
            HomeNavigationItem(name = Utils.PROFILE, selectedIcon = R.drawable.profile_selected_icon, unselectedIcon = R.drawable.profile_unselected_icon, position = POSITION_PROFILE),
        )
        val groupViewModel = hiltViewModel<GroupViewModel>()
        if (deeplink.isNotEmpty()) {
            groupViewModel.deepLinkGroupId = deeplink
            deeplink = ""
            SideEffect {
                scope.launch {
                    pagerState.scrollToPage(POSITION_GROUP)
                }
            }
        }
        Scaffold(
            bottomBar = {
                BottomNavigation(
                    modifier = Modifier.navigationBarsPadding(),
                    backgroundColor = Color.White,
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
                    POSITION_HOME -> HomeScreen(modifier = Modifier.padding(innerPadding)) {
                        addExpense.invoke()
                    }
                    POSITION_GROUP -> GroupScreen(
                        modifier = Modifier.padding(innerPadding),
                        addGroup = { createGroupClicked.invoke() },
                        sendInviteLink = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, it)
                                type = "text/plain"
                            }
                            try {
                                startActivity(sendIntent)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(this@MainActivity, Utils.FAILED, Toast.LENGTH_LONG).show()
                            }
                        }
                    ) {
                       openGroupSession.invoke(it)
                    }
                    POSITION_BUDGET -> BudgetScreen()
                    POSITION_PROFILE -> ProfileScreen()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MainPageNavigation()
    }

}