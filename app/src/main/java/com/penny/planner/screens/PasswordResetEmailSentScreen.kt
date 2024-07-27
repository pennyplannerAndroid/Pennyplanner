package com.penny.planner.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penny.planner.R
import com.penny.planner.ui.theme.PennyPlannerTheme

@Composable
fun PasswordResetEmailScreen (
    modifier: Modifier,
    email: String,
    onLoginClick : () -> Unit
) {
    Column {
        Body(
            iconId = R.drawable.email_sent,
            text1 = stringResource(id = R.string.password_reset_body1),
            text2 = stringResource(id = R.string.password_reset_body2, email)
        )
        Button(
            onClick = onLoginClick,
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .size(48.dp)
                .align(Alignment.End),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonColors(
                colorResource(id = R.color.loginText),
                colorResource(id = R.color.white),
                colorResource(id = R.color.teal_200),
                colorResource(id = R.color.teal_200)
            )
        ) {
            Text(text = stringResource(id = R.string.back_to_login))
        }
    }
}

@Preview
@Composable
fun PreviewEmailSentScreen() {
    PennyPlannerTheme {
        PasswordResetEmailScreen(modifier = Modifier, "aseem@penny.com") {

        }
    }
}