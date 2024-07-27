package com.penny.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.penny.planner.screens.EmailVerificationScreen
import com.penny.planner.screens.ForgotPasswordScreen
import com.penny.planner.screens.LoginScreen
import com.penny.planner.screens.PasswordResetEmailScreen
import com.penny.planner.screens.SignupScreen
import com.penny.planner.screens.TutorialScreen
import com.penny.planner.ui.theme.PennyPlannerTheme
import dagger.hilt.android.AndroidEntryPoint
import org.intellij.lang.annotations.Pattern

const val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
const val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PennyPlannerTheme {
                App()
            }
        }
    }

    @Composable
    fun App() {
        val controller = rememberNavController()
        NavHost(navController = controller, startDestination = "tutorial") {
            composable(route = "tutorial") {
                TutorialScreen(
                    modifier = Modifier,
                    onLoginClick = { controller.navigate("login") },
                    onSignupClick = { controller.navigate("signup") }
                    )
            }
            composable(route = "signup") {
                SignupScreen(
                    modifier =  Modifier,
                    onBackPressed = {
                        controller.navigate("tutorial")
                    },
                    navToLogin = {
                        controller.navigate("login")
                    },
                    navToVerification = {
                        controller.navigate(
                            route = "emailVerification/${it}"
                        )
                    }
                )
            }
            composable(route = "login") {
                LoginScreen(
                    modifier = Modifier,
                    onBackPressed = { controller.navigate("tutorial") },
                    forgotPassword = { controller.navigate("forgotPassword") },
                    navToSignup = { controller.navigate("signup") },
                    loginSuccess = {},
                    navToVerification = {
                        controller.navigate(
                            route = "emailVerification/${it}"
                        )
                    }
                )
            }
            composable(
                route = "emailVerification/{email}",
                arguments = listOf(
                    navArgument("email") {
                        type = NavType.StringType
                        defaultValue = "@"
                    }
                )
            ) {
                EmailVerificationScreen(
                    modifier = Modifier,
                    email = it.arguments?.getString("email") ?: "@",
                    onBackPressed = {
                        controller.popBackStack()
                    }
                ) {
                    finish()
                }
            }
            composable(route = "forgotPassword") {
                ForgotPasswordScreen(
                    modifier =  Modifier,
                    onBackPressed = {
                        controller.navigate("tutorial")
                    },
                    buttonClick = {
                        controller.navigate(
                            route = "emailSent/${it}"
                        )
                    }
                )
            }
            composable(
                route = "emailSent/{email}",
                arguments = listOf(
                    navArgument("email") {
                        type = NavType.StringType
                        defaultValue = "@"
                    }
                )
            ) {
                PasswordResetEmailScreen(
                    modifier = Modifier,
                    onLoginClick = {
                        controller.navigate("login")
                    },
                    email = it.arguments?.getString("email") ?: "@"
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