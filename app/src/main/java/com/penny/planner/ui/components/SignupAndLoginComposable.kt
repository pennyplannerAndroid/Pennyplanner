package com.penny.planner.ui.components

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.ui.theme.PennyPlannerTheme
import java.util.regex.Pattern

@Composable
fun SignupAndLoginComposable(
    modifier : Modifier,
    title: String,
    googleButtonString : String,
    facebookButtonString : String,
    mainButtonString : Int,
    text: AnnotatedString,
    needForgotPassword: Boolean = false,
    onBackPressed : () -> Unit,
    buttonClicked : (String, String) -> Unit,
    googleButtonClicked : () -> Unit,
    facebookButtonClicked : () -> Unit,
    navigationButtonClicked: () -> Unit,
    forgotPasswordClicked: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val pattern = Pattern.compile(Utils.PASSWORD_REGEX)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold (
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { focusManager.clearFocus() }
            )
        },
        topBar = {
            TopBar(
                modifier = modifier,
                title = title,
                onBackPressed = onBackPressed
            )
        },
        content = { paddingValues ->
            Column (
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 50.dp)
            ) {
                OutLinedTextFieldForEmail(
                    modifier = modifier,
                    email = email,
                    onValueChange = {
                        email = it
                    }
                )
                TextFieldErrorIndicator(
                    modifier = modifier,
                    textRes = R.string.invalid_email,
                    show = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
                OutlinedTextFieldForPassword(
                    modifier = modifier,
                    password = password,
                    onValueChange = {
                        password = it
                    }
                )
                TextFieldErrorIndicator(
                    modifier = modifier,
                    textRes = R.string.invalid_password,
                    show = password.isNotEmpty() && !pattern.matcher(password).matches()
                )
                if (needForgotPassword) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 20.dp, top = 12.dp)
                            .clickable(onClick = forgotPasswordClicked),
                        text = stringResource(id = R.string.forgot_password_text),
                        color = colorResource(id = R.color.loginText),
                        fontWeight = FontWeight.Bold
                    )
                }
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp),
                    onClick = { buttonClicked.invoke(email, password) },
                    enabled = (
                            Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                    && pattern.matcher(password).matches()
                            ),
                    textRes = mainButtonString
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(
                        id = R.string.or_with
                    ),
                    color = colorResource(id = R.color.or_with_color),
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButtonWIthIcon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp)
                        .size(48.dp),
                    onClick = googleButtonClicked,
                    imageRes = R.drawable.goole_login,
                    text = googleButtonString
                )
                OutlinedButtonWIthIcon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .size(48.dp),
                    onClick = facebookButtonClicked,
                    imageRes = R.drawable.facebook_login,
                    text = facebookButtonString
                )
                ClickableText(
                    text = text,
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp),
                    onClick = { offset ->
                        text.getStringAnnotations(
                            tag = Utils.CLICK_TAG,
                            start = offset,
                            end = offset
                        )[0].let{
                            navigationButtonClicked.invoke()
                        }
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewSignUpLoginScreen() {
    PennyPlannerTheme {
        SignupAndLoginComposable(
            modifier = Modifier,
            title = "Sign Up",
            "Signup with google",
            "SignUp with facebook",
            R.string.signup,
            buildText(
                start = R.string.login_from_signup,
                end = R.string.login
            ),
            false,
            {},
            {name, email -> },
            {},
            {},
            {}
        )
    }
}