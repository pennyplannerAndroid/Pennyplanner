package com.penny.planner.ui.screens.onboarding

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.R
import com.penny.planner.data.repositories.OnboardingRepositoryImpl
import com.penny.planner.ui.components.OutLinedTextFieldForEmail
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TextFieldErrorIndicator
import com.penny.planner.ui.components.TopBar
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.OnboardingViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    viewModel: OnboardingViewModel,
    onBackPressed: () -> Unit,
    buttonClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var check by remember { mutableStateOf(false) }
    val result = viewModel.forgetPasswordSentStatus.observeAsState().value
    if (result != null && check) {
        check = false
        if (result.isSuccess)
            buttonClick.invoke(email)
        else
            Toast.makeText(LocalContext.current,
                result.exceptionOrNull()?.message ?: stringResource(id = R.string.operation_failed),
                Toast.LENGTH_LONG
            ).show()
    }
    Scaffold (
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        topBar = {
            TopBar(
                modifier = modifier,
                title = stringResource(id = R.string.forgot_password),
                onBackPressed = onBackPressed
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 50.dp)
            ) {
                Text(
                    modifier = modifier.padding(16.dp),
                    text = stringResource(id = R.string.forgot_password_screen_body),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colorResource(id = R.color.black)
                )
                OutLinedTextFieldForEmail(
                    modifier = modifier,
                    email = email
                ) {
                    email = it
                }
                TextFieldErrorIndicator(
                    modifier = modifier,
                    textRes = R.string.invalid_email,
                    show = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp),
                    textRes = R.string.general_continue,
                    onClick = {
                        check = true
                        viewModel.sendForgetPasswordEmail(email)
                    },
                    enabled = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
            }
        }
    )
}

@Preview
@Composable
fun PasswordResetPreview() {
    PennyPlannerTheme {
        ForgotPasswordScreen(modifier = Modifier, OnboardingViewModel(
            OnboardingRepositoryImpl(
                FirebaseAuth.getInstance())
        ), {}) {
            
        }
    }
}