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
import com.penny.planner.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    modifier : Modifier,
    onBackPressed : () -> Unit,
    forgotPassword : () -> Unit,
    navToSignup : () -> Unit,
    loginSuccess: () -> Unit,
    navToVerification: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val viewModel = hiltViewModel<LoginViewModel>()
    val result = viewModel.result.observeAsState().value
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
            Toast.makeText(context, result.exceptionOrNull()?.message ?: "Invalid User", Toast.LENGTH_LONG).show()
        }
    }

    SignupAndLoginComposable(
        modifier = modifier,
        title = stringResource(id = R.string.login),
        googleButtonString = stringResource(id = R.string.login_with_google),
        facebookButtonString = stringResource(id = R.string.login_with_facebook),
        mainButtonString = stringResource(id = R.string.signup),
        text = buildText(
            start = R.string.signup_from_login,
            end = R.string.signup
        ),
        needForgotPassword = true,
        onBackPressed = onBackPressed,
        buttonClicked = { user ->
            isLoadingShown = true
            email = user.email
            viewModel.login(user)
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

}

@Composable
@Preview
fun PreviewLogin() {
    LoginScreen(modifier = Modifier, {}, {}, {}, {}, {})
}