package com.penny.planner.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TopBar
import com.penny.planner.viewmodels.OnboardingViewModel

@Composable
fun EmailVerificationScreen (
    modifier: Modifier,
    viewModel: OnboardingViewModel,
    onBackPressed: () -> Unit,
    emailVerified: () -> Unit
){
    val emailSentStatus = viewModel.emailSent.observeAsState().value
    val isVerified = viewModel.isVerified.observeAsState().value
    if (emailSentStatus != null) {
        if (emailSentStatus.isSuccess) {
            Toast.makeText(
                LocalContext.current,
                stringResource(id = R.string.email_resent),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                LocalContext.current,
                emailSentStatus.exceptionOrNull()?.message ?: stringResource(id = R.string.operation_failed),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    if (isVerified != null) {
        if (isVerified.isSuccess) {
            emailVerified.invoke()
        } else {
            Toast.makeText(
                LocalContext.current,
                isVerified.exceptionOrNull()?.message ?: stringResource(id = R.string.email_not_verified),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                modifier = modifier,
                title = stringResource(id = R.string.verification),
                onBackPressed = onBackPressed
            )
        }, content = { paddingValues ->
            Column (
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(id = R.string.verify_your_email),
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp
                )
                Text(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    text = textWithPartialColored(email = viewModel.getEmail()),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable(
                            onClick = {
                                viewModel.sendVerificationEmail()
                            }
                        ),
                    text = stringResource(id = R.string.verify_your_email_body_3),
                    textDecoration = TextDecoration.Underline,
                    color = colorResource(id = R.color.loginText),
                    fontWeight = FontWeight.SemiBold
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 20.dp, end = 20.dp)
                        .size(48.dp),
                    textRes = R.string.verify_email_button,
                    onClick = {  viewModel.checkVerificationStatus() },
                    enabled = true
                )
            }
        }
    )
}

@Composable
fun textWithPartialColored(email : String): AnnotatedString {
    val builder = StringBuilder()
    val emailArr = email.split("@").toMutableList()
    builder.append(if (emailArr[0].length > 6) email.substring(0, 6) else email[0])
    builder.append("*".repeat(emailArr[0].length - builder.length))
        .append(emailArr[1])
        .append(". ")
    return buildAnnotatedString {
        append(
            stringResource(id = R.string.verify_your_email_body_1).plus(" ")
        )
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.loginText)
            )
        ) {
            append(
                builder
            )
        }
        append(
            stringResource(id = R.string.verify_your_email_body_2)
        )
    }
}