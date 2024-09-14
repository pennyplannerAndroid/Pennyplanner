package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.penny.planner.R

@Composable
fun TextFieldWithTrailingIcon(
    value: String,
    title: Int,
    onClick: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
            .clickable(onClick = onClick),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorResource(
                id = if (value.isEmpty()) R.color.textField_border
                else R.color.loginText
            ),
        ),
        enabled = false,
        shape = RoundedCornerShape(12.dp),
        value = value,
        onValueChange = { },
        label = {
            Text(stringResource(id = title))
        },
        trailingIcon = {
            Image(
                painter = painterResource(id = R.drawable.down_arrow_icon),
                contentDescription = ""
            )
        }
    )
}