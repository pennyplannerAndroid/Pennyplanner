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
import com.penny.planner.R
import com.penny.planner.data.repositories.implementations.OnboardingRepositoryImpl
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.SignupAndLoginComposable
import com.penny.planner.ui.components.buildText
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.OnboardingViewModel

@Composable
fun SignupScreen(
    modifier : Modifier,
    viewModel: OnboardingViewModel,
    onBackPressed : () -> Unit,
    navToLogin : () -> Unit,
    navToVerification: (String) -> Unit
    ) {
    var email by remember { mutableStateOf("") }
    var isLoadingShown by remember {
        mutableStateOf(false)
    }
    val isLoggedIn = viewModel.signUpResult.observeAsState().value
    if (isLoggedIn != null && isLoadingShown) {
        if (isLoggedIn.isSuccess) {
            navToVerification.invoke(email)
        } else
            Toast.makeText(
                LocalContext.current,
                isLoggedIn.exceptionOrNull()?.message ?: stringResource(id = R.string.retry_verification),
                Toast.LENGTH_LONG
            ).show()
    }
    FullScreenProgressIndicator(isLoadingShown)

    SignupAndLoginComposable(
        modifier = modifier,
        title = stringResource(id = R.string.signup),
        googleButtonString = stringResource(id = R.string.signup_with_google),
        facebookButtonString = stringResource(id = R.string.signup_with_facebook),
        mainButtonString = R.string.signup,
        text = buildText(
            start = R.string.login_from_signup,
            end = R.string.login
        ),
        onBackPressed = onBackPressed,
        buttonClicked = { emailId, password ->
            isLoadingShown = true
            email = emailId
            viewModel.signup(emailId, password)
        },
        googleButtonClicked = {
            viewModel.signupWithGoogle()
        },
        facebookButtonClicked = {
            viewModel.signupWithFacebook()
        },
        navigationButtonClicked = navToLogin
    )
}

@Preview
@Composable
fun PreviewSignupScreen() {
    PennyPlannerTheme {
        SignupScreen(
            modifier = Modifier,
            OnboardingViewModel(OnboardingRepositoryImpl()),
            {}, {}) {}
    }
}