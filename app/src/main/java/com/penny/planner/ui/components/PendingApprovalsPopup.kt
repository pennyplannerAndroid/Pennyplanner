package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R

@Composable
fun PendingApprovalsPopup(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
            .background(
                color = colorResource(id = R.color.loginText),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp),
                painter = painterResource(id = R.drawable.warning_icon),
                contentDescription = ""
            )
            Column {
                Text(
                    text = stringResource(id = R.string.pending_approvals),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    modifier = modifier.padding(top = 4.dp),
                    text = stringResource(id = R.string.tap_here_to_manage),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

    }
}

@Preview
@Composable
fun PreviewApprovalPopup() {
    PendingApprovalsPopup(Modifier) {}
}