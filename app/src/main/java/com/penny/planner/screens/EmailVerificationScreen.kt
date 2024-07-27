package com.penny.planner.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.ui.theme.PennyPlannerTheme
import com.penny.planner.viewmodels.EmailVerificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen (
    modifier: Modifier,
    email: String,
    onBackPressed: () -> Unit,
    emailVerified: () -> Unit
){
    val viewModel = hiltViewModel<EmailVerificationViewModel>()
    val emailSentStatus = viewModel.emailSent.observeAsState().value
    val isVerified = viewModel.isVerified.observeAsState().value
    if (emailSentStatus != null) {
        if (emailSentStatus.isSuccess) {
            Toast.makeText(
                LocalContext.current,
                "Email resent!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                LocalContext.current,
                emailSentStatus.exceptionOrNull()?.message ?: "Operation failed! Please try again.",
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
                isVerified.exceptionOrNull()?.message ?: "Email not verified!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.verification),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                        modifier = modifier
                            .clickable(onClick = onBackPressed)
                            .padding(start = 20.dp)
                    )
                }
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
                    text = textWithPartialColored(email = email),
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 20.dp, end = 20.dp)
                        .size(48.dp),
                    onClick = {
                        viewModel.checkVerificationStatus()
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.verify_email_button),
                        fontSize = 16.sp
                    )
                }
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

@Preview
@Composable
fun PreviewVerificationScreen() {
    PennyPlannerTheme {
        EmailVerificationScreen(modifier = Modifier,"as@gmail.com", {}, {})
    }
}