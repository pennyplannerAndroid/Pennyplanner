package com.penny.planner.ui.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penny.planner.R
import com.penny.planner.ui.components.InformationWithIconAndBody
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.theme.PennyPlannerTheme

@Composable
fun PasswordResetEmailScreen (
    modifier: Modifier,
    email: String,
    onLoginClick : () -> Unit
) {
    Column {
        InformationWithIconAndBody(
            iconId = R.drawable.email_sent,
            text1 = stringResource(id = R.string.password_reset_body1),
            text2 = stringResource(id = R.string.password_reset_body2, email)
        )
        PrimaryButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .size(48.dp)
                .align(Alignment.End),
            textRes = R.string.back_to_login,
            onClick = onLoginClick,
            enabled = true
        )
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