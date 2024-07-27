package com.penny.planner.components

import android.util.Patterns
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.PASSWORD_REGEX
import com.penny.planner.R
import com.penny.planner.models.FirebaseUser
import com.penny.planner.ui.theme.PennyPlannerTheme
import java.util.regex.Pattern

@Composable
fun SignupAndLoginComposable(
    modifier : Modifier,
    title: String,
    googleButtonString : String,
    facebookButtonString : String,
    mainButtonString : String,
    text: AnnotatedString,
    needForgotPassword: Boolean = false,
    onBackPressed : () -> Unit,
    buttonClicked : (FirebaseUser) -> Unit,
    googleButtonClicked : () -> Unit,
    facebookButtonClicked : () -> Unit,
    navigationButtonClicked: () -> Unit,
    forgotPasswordClicked: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val pattern = Pattern.compile(PASSWORD_REGEX)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
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
                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.textField_border),
                        focusedBorderColor = colorResource(
                            id = if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                                R.color.loginText else
                                R.color.red
                        )
                    ),
                    shape = RoundedCornerShape(12.dp),
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text("Email")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Row (
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 4.dp)
                            .animateContentSize()
                    ) {
                        Image(
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 8.dp),
                            painter = painterResource(id = R.drawable.error_image),
                            contentDescription = "invalid"
                        )
                        Text(
                            text = stringResource(id = R.string.invalid_email),
                            color = colorResource(id = R.color.red)
                        )
                    }
                }
                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.textField_border),
                        focusedBorderColor = colorResource(
                            id = if (pattern.matcher(password).matches())
                                R.color.loginText else
                                R.color.red
                        )
                    ),
                    shape = RoundedCornerShape(12.dp),
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text("Password")
                    },
                    visualTransformation = if (showPassword)
                        VisualTransformation.None else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = if (password.isNotEmpty()) {
                        {
                            Image(
                                modifier = Modifier.clickable {
                                    showPassword = !showPassword
                                },
                                painter = painterResource(id = R.drawable.show_password),
                                contentDescription = "Show Password"
                            )
                        }
                    } else null
                )
                if (password.isNotEmpty() && !pattern.matcher(password).matches()) {
                    Row (
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp)
                    ) {
                        Image(
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 8.dp),
                            painter = painterResource(id = R.drawable.error_image),
                            contentDescription = "invalid"
                        )
                        Text(
                            text = stringResource(id = R.string.invalid_password),
                            color = colorResource(id = R.color.red)
                        )
                    }
                }
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp),
                    onClick = { buttonClicked.invoke(FirebaseUser(email, password)) },
                    shape = RoundedCornerShape(12.dp),
                    enabled = (
                            Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                    && pattern.matcher(password).matches()
                            )
                ) {
                    Text(text = mainButtonString)
                }
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(
                        id = R.string.or_with
                    ),
                    color = colorResource(id = R.color.or_with_color),
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp)
                        .size(48.dp)
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.textField_border),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = googleButtonClicked
                ) {
                    Row {
                        Image(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.goole_login),
                            contentDescription = "google")
                        Text(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically),
                            text = googleButtonString,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black),
                            fontSize = 18.sp
                        )
                    }
                }
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .size(48.dp)
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.textField_border),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = facebookButtonClicked
                ) {
                    Row {
                        Image(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.facebook_login),
                            contentDescription = "facebook")
                        Text(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically),
                            text = facebookButtonString,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black),
                            fontSize = 18.sp
                        )
                    }
                }
                ClickableText(
                    text = text,
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp),
                    onClick = { offset ->
                        text.getStringAnnotations(
                            tag = "signup",
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

@Composable
fun buildText(start : Int, end: Int) : AnnotatedString {
    return buildAnnotatedString {
        append(stringResource(id = start).plus(" "))
        pushStringAnnotation(
            tag = "signup",
            annotation = stringResource(id = end)
        )
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.loginText),
                textDecoration = TextDecoration.Underline,
            )
        ) {
            append(
                stringResource(id = end)
            )
        }
    }
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
            "Sign up",
            buildText(
                start = R.string.login_from_signup,
                end = R.string.login
            ),
            false,
            {},
            {},
            {},
            {},
            {}
        )
    }
}