package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R


@Composable
fun CategoryAddPage(
    limit: Boolean,
    onBack: () -> Unit,
    onAddClicked: (String, String) -> Unit
) {
    var value by remember {
        mutableStateOf("")
    }
    var spendLimit by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(end = 24.dp)
                .size(32.dp)
                .clickable(onClick = onBack),
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = stringResource(id = R.string.back)
        )
        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = stringResource(id = R.string.add),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorResource(id = R.color.textField_border),
            focusedBorderColor = colorResource(id = R.color.loginText)
        ),
        shape = RoundedCornerShape(12.dp),
        value = value,
        onValueChange = { value = it },
        label = {
            Text(stringResource(id = R.string.description))
        }
    )
    if (limit) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colorResource(id = R.color.textField_border),
                focusedBorderColor = colorResource(id = R.color.loginText)
            ),
            shape = RoundedCornerShape(12.dp),
            value = spendLimit,
            onValueChange = { spendLimit = it },
            label = {
                Text(stringResource(id = R.string.spend_limit))
            }
        )
    }
    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
            .size(48.dp),
        textRes = R.string.add,
        onClick = { onAddClicked.invoke(value, spendLimit) },
        enabled = value.isNotEmpty()
    )
}

@Preview
@Composable
fun PreviewAddPage() {
    CategoryAddPage(limit = true, onBack = { }) { _, _ ->

    }
}