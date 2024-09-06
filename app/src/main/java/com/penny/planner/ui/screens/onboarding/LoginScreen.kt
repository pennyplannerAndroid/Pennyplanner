package com.penny.planner.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.R
import com.penny.planner.data.repositories.OnboardingRepositoryImpl
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.SignupAndLoginComposable
import com.penny.planner.ui.components.buildText
import com.penny.planner.viewmodels.OnboardingViewModel

@Composable
fun LoginScreen(
    modifier : Modifier,
    viewModel: OnboardingViewModel,
    onBackPressed : () -> Unit,
    forgotPassword : () -> Unit,
    navToSignup : () -> Unit,
    loginSuccess: () -> Unit,
    navToVerification: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val result = viewModel.loginResult.observeAsState().value
    var isLoadingShown by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    if (result != null && isLoadingShown) {
        isLoadingShown = false
        if (result.isSuccess) {
            if (result.getOrNull()!!.isEmailVerified)
                loginSuccess.invoke()
            else
                navToVerification.invoke(email)
        } else {
            Toast.makeText(context, result.exceptionOrNull()?.message ?: stringResource(id = R.string.invalid_user), Toast.LENGTH_LONG).show()
        }
    }
    SignupAndLoginComposable(
        modifier = modifier,
        title = stringResource(id = R.string.login),
        googleButtonString = stringResource(id = R.string.login_with_google),
        facebookButtonString = stringResource(id = R.string.login_with_facebook),
        mainButtonString = R.string.signup,
        text = buildText(
            start = R.string.signup_from_login,
            end = R.string.signup
        ),
        needForgotPassword = true,
        onBackPressed = onBackPressed,
        buttonClicked = { emailId, password ->
            isLoadingShown = true
            email = emailId
            viewModel.login(emailId, password)
        },
        googleButtonClicked = {
            viewModel.loginWithGoogle()
        },
        facebookButtonClicked = {
            viewModel.loginWithFacebook()
        },
        navigationButtonClicked = navToSignup,
        forgotPasswordClicked = forgotPassword
    )
    FullScreenProgressIndicator(show = isLoadingShown)
}

@Composable
@Preview
fun PreviewLogin() {
    LoginScreen(modifier = Modifier, OnboardingViewModel(OnboardingRepositoryImpl(FirebaseAuth.getInstance())), {}, {}, {}, {}, {})
}