package com.penny.planner.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.penny.planner.helpers.Utils
import com.penny.planner.ui.screens.EmailVerificationScreen
import com.penny.planner.ui.screens.ForgotPasswordScreen
import com.penny.planner.ui.screens.LoginScreen
import com.penny.planner.ui.screens.PasswordResetEmailScreen
import com.penny.planner.ui.screens.SignupScreen
import com.penny.planner.ui.screens.TutorialScreen
import com.penny.planner.ui.screens.UpdateProfileScreen
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                        controller.navigate(Utils.LOGIN)
                    },
                    navToVerification = {
                        controller.navigate(
                            route = "${Utils.EMAIL_VERIFICATION}/${it}"
                        )
                    }
                )
            }
            composable(route = Utils.LOGIN) {
                LoginScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    onBackPressed = { controller.navigate(Utils.TUTORIAL) },
                    forgotPassword = { controller.navigate(Utils.FORGOT_PASSWORD) },
                    navToSignup = { controller.navigate(Utils.SIGNUP) },
                    loginSuccess = { controller.navigate(Utils.UPDATE_PROFILE) },
                    navToVerification = {
                        controller.navigate(
                            route = "${Utils.EMAIL_VERIFICATION}/${it}"
                        )
                    }
                )
            }
            composable(
                route = "${Utils.EMAIL_VERIFICATION}/{${Utils.EMAIL}}",
                arguments = listOf(
                    navArgument(Utils.EMAIL) {
                        type = NavType.StringType
                        defaultValue = Utils.DEFAULT_EMAIL_STRING
                    }
                )
            ) {
                EmailVerificationScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    email = it.arguments?.getString(Utils.EMAIL) ?: Utils.DEFAULT_EMAIL_STRING,
                    onBackPressed = {
                        controller.popBackStack()
                    }
                ) {
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
                        controller.navigate(Utils.LOGIN)
                    },
                    email = it.arguments?.getString(Utils.EMAIL) ?: viewModel.getEmail()
                )
            }
            composable(route = Utils.UPDATE_PROFILE) {
                UpdateProfileScreen(
                    viewModel = viewModel,
                    buttonClicked = {
                        startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    @Preview
    fun UIPreview() {
        TutorialScreen(modifier = Modifier.fillMaxSize(), {}, {})
    }
}