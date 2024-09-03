package com.penny.planner.learning

import android.util.Patterns
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.OutLinedTextFieldForEmail
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TextFieldErrorIndicator
import com.penny.planner.viewmodels.MainActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    startService: () -> Unit,
    endService: () -> Unit
){
    var email by remember { mutableStateOf("") }
    var check by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<MainActivityViewModel>()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Hello " + viewModel.getName(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        content = { paddingValues ->
            Column (
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Lets add someone to make a expense group with!",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colorResource(id = R.color.black)
                )
                OutLinedTextFieldForEmail(modifier = Modifier, email = email) {
                    email = it
                }
                TextFieldErrorIndicator(
                    modifier = Modifier,
                    textRes = R.string.invalid_email,
                    show = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp), 
                    textRes = R.string.new_group,
                    onClick = {
                        check = true
                        viewModel.newGroup(email)
                    },
                    enabled = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
                FullScreenProgressIndicator(show = check)
            }
        }
    )
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen({}, {})
}