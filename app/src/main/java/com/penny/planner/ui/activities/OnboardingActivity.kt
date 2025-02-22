package com.penny.planner.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.enums.LoginResult
import com.penny.planner.ui.screens.onboarding.AllSetScreen
import com.penny.planner.ui.screens.onboarding.EmailVerificationScreen
import com.penny.planner.ui.screens.onboarding.ForgotPasswordScreen
import com.penny.planner.ui.screens.onboarding.LoginScreen
import com.penny.planner.ui.screens.onboarding.PasswordResetEmailScreen
import com.penny.planner.ui.screens.onboarding.SetBudgetScreen
import com.penny.planner.ui.screens.onboarding.SignupScreen
import com.penny.planner.ui.screens.onboarding.TutorialScreen
import com.penny.planner.ui.screens.onboarding.UpdateProfileScreen
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    private var startDestination = Utils.TUTORIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startDestination = intent.getStringExtra(Utils.NAVIGATION_DESTINATION) ?: Utils.TUTORIAL
        setContent {
            PennyPlannerTheme {
                OnboardingNavigation()
            }
        }
    }

    @Composable
    fun OnboardingNavigation() {
        val viewModel = hiltViewModel<OnboardingViewModel>()

        val controller = rememberNavController()
        NavHost(navController = controller, startDestination = startDestination) {
            composable(route = Utils.TUTORIAL) {
                TutorialScreen(
                    modifier = Modifier,
                    onLoginClick = { controller.navigate(Utils.LOGIN) },
                    onSignupClick = { controller.navigate(Utils.SIGNUP) }
                )
            }
            composable(route = Utils.SIGNUP) {
                SignupScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    onBackPressed = {
                        controller.navigate(Utils.SIGNUP)
                    },
                    navToLogin = {
                        controller.popBackStack()
                        controller.navigate(Utils.LOGIN)
                    },
                    navToVerification = {
                        controller.popBackStack()
                        controller.navigate(route = Utils.EMAIL_VERIFICATION)
                    }
                )
            }
            composable(route = Utils.LOGIN) {
                LoginScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    onBackPressed = { controller.navigate(Utils.TUTORIAL) },
                    forgotPassword = { controller.navigate(Utils.FORGOT_PASSWORD) },
                    navToSignup = {
                        controller.popBackStack()
                        controller.navigate(Utils.SIGNUP)
                    },
                    loginResult = {
                        controller.popBackStack()
                        when (it) {
                            LoginResult.VERIFY_EMAIL -> controller.navigate(Utils.EMAIL_VERIFICATION)
                            LoginResult.ADD_NAME -> controller.navigate(Utils.UPDATE_PROFILE)
                            LoginResult.ADD_BUDGET -> controller.navigate(Utils.SET_MONTHLY_BUDGET)
                            LoginResult.VERIFY_SUCCESS -> {
                                val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                                intent.putExtra(Utils.NAVIGATION_DESTINATION, Utils.MAIN_PAGE)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                )
            }
            composable(route = Utils.EMAIL_VERIFICATION) {
                EmailVerificationScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    onBackPressed = {
                        controller.popBackStack()
                    }
                ) {
                    controller.popBackStack()
                    controller.navigate(Utils.UPDATE_PROFILE)
                }
            }
            composable(route = Utils.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    onBackPressed = {
                        controller.navigate(Utils.TUTORIAL)
                    },
                    buttonClick = {
                        controller.navigate(
                            route = "${Utils.EMAIL_SENT}/${it}"
                        )
                    }
                )
            }
            composable(
                route = "${Utils.EMAIL_SENT}/{${Utils.EMAIL}}",
                arguments = listOf(
                    navArgument(Utils.EMAIL) {
                        type = NavType.StringType
                        defaultValue = Utils.DEFAULT_EMAIL_STRING
                    }
                )
            ) {
                PasswordResetEmailScreen(
                    modifier = Modifier,
                    onLoginClick = {
                        controller.popBackStack()
                        controller.navigate(Utils.LOGIN)
                    },
                    email = it.arguments?.getString(Utils.EMAIL) ?: viewModel.getEmail()
                )
            }
            composable(route = Utils.UPDATE_PROFILE) {
                UpdateProfileScreen(
                    viewModel = viewModel,
                    buttonClicked = {
                        controller.popBackStack()
                        controller.navigate(Utils.SET_MONTHLY_BUDGET)
                    }
                )
            }
            composable(route = Utils.SET_MONTHLY_BUDGET) {
                SetBudgetScreen(viewModel = viewModel) {
                    controller.navigate(Utils.ALL_SET_SCREEN)
                }
            }
            composable(route = Utils.ALL_SET_SCREEN) {
                AllSetScreen()
                LaunchedEffect(key1 = Unit){
                    delay(3000)
                    val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                    intent.putExtra(Utils.NAVIGATION_DESTINATION, Utils.MAIN_PAGE)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    @Composable
    @Preview
    fun UIPreview() {
        TutorialScreen(modifier = Modifier.fillMaxSize(), {}, {})
    }
}