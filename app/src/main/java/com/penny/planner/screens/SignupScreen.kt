package com.penny.planner.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.components.SignupAndLoginComposable
import com.penny.planner.components.buildText
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.SignUpViewModel

@Composable
fun SignupScreen(
    modifier : Modifier,
    onBackPressed : () -> Unit,
    navToLogin : () -> Unit,
    navToVerification: (String) -> Unit
    ) {
    var email by remember { mutableStateOf("") }
    var isLoadingShown by remember {
        mutableStateOf(false)
    }
    val viewModel = hiltViewModel<SignUpViewModel>()
    val isLoggedIn = viewModel.signUpResult.observeAsState().value
    if (isLoggedIn != null && isLoadingShown) {
        if (isLoggedIn.isSuccess) {
            navToVerification.invoke(email)
        } else
            Toast.makeText(
                LocalContext.current,
                isLoggedIn.exceptionOrNull()?.message ?: "Verification failed! Please try again.",
                Toast.LENGTH_LONG
            ).show()
    }

    if (isLoadingShown) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = colorResource(id = R.color.transparent_60)
                )
        ) {
            CircularProgressIndicator()
        }
    }

    SignupAndLoginComposable(
        modifier = modifier,
        title = stringResource(id = R.string.signup),
        googleButtonString = stringResource(id = R.string.signup_with_google),
        facebookButtonString = stringResource(id = R.string.signup_with_facebook),
        mainButtonString = stringResource(id = R.string.signup),
        text = buildText(
            start = R.string.login_from_signup,
            end = R.string.login
        ),
        onBackPressed = onBackPressed,
        buttonClicked = { user ->
            isLoadingShown = true
            email = user.email
            viewModel.signup(user)
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
        SignupScreen(modifier = Modifier, {}, {}) {

        }
    }
}