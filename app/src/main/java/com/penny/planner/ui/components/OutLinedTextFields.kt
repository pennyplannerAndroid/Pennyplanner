package com.penny.planner.ui.components

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import java.util.regex.Pattern

@Composable
fun OutLinedTextFieldForEmail(
    modifier: Modifier,
    email: String,
    onValueChange: (String) -> Unit
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
        onValueChange = { onValueChange(it) },
        label = {
            Text(stringResource(id = R.string.email))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun OutlinedTextFieldForPassword(
    modifier: Modifier,
    password: String,
    onValueChange: (String) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorResource(id = R.color.textField_border),
            focusedBorderColor = colorResource(
                id = if (Pattern.compile(Utils.PASSWORD_REGEX).matcher(password).matches())
                    R.color.loginText else
                    R.color.red
            )
        ),
        shape = RoundedCornerShape(12.dp),
        value = password,
        onValueChange = { onValueChange(it) },
        label = {
            Text(stringResource(id = R.string.password))
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
                    contentDescription = stringResource(id = R.string.show_password)
                )
            }
        } else null
    )
}