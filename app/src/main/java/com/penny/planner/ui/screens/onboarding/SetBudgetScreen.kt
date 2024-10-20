package com.penny.planner.ui.screens.onboarding

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.pxToDp
import com.penny.planner.models.MonthlyBudgetInfoModel
import com.penny.planner.ui.components.BottomDrawerForInfo
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.viewmodels.OnboardingViewModel

@Composable
fun SetBudgetScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit
) {
    var budget by remember { mutableStateOf("") }
    var showLoader by remember {
        mutableStateOf(false)
    }
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val context = LocalContext.current
    val view = LocalView.current
    var sliderPosition by remember { mutableFloatStateOf(80f) }
    var showInfo by remember {
        mutableStateOf(false)
    }

    SideEffect {
        val window = (context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    val result = viewModel.monthlyBudgetStatus.observeAsState().value
    if (result != null && showLoader) {
        showLoader = false
        if (result.isSuccess) {
            onFinish.invoke()
        } else
            Toast.makeText(
                LocalContext.current,
                result.exceptionOrNull()?.message ?: stringResource(id = R.string.operation_failed),
                Toast.LENGTH_LONG
            ).show()
    }
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(screenHeight.dp / 3)
                .clip(CustomShape())
                .background(
                    colorResource(id = R.color.loginText)
                )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = (screenHeight / 4).pxToDp(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                text = stringResource(id = R.string.set_monthly_budget),
                fontSize = 36.sp,
                color = Color.White,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Normal
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 36.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colorResource(id = R.color.textField_border),
                focusedBorderColor = colorResource(
                    id = if (budget.isNotEmpty())
                        R.color.loginText else
                        R.color.red
                )
            ),
            shape = RoundedCornerShape(12.dp),
            value = budget,
            onValueChange = { budget = if(Utils.lengthHint(it.length, Utils.PRICE_LIMIT) > 0) it else budget },
            label = {
                Text(stringResource(id = R.string.enter_budget))
            },
            trailingIcon = {
                if (budget.isNotEmpty())
                    Text(text = Utils.lengthHint(budget.length, Utils.PRICE_LIMIT).toString())
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 6.dp),
            text = stringResource(id = R.string.budget_editable_info),
            color = colorResource(id = R.color.or_with_color),
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp
        )
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ){
            Row {
                Text(
                    text = stringResource(id = R.string.safe_to_spend),
                    fontSize = 13.sp
                )
                Image(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            showInfo = true
                        },
                    painter = painterResource(id = R.drawable.info_image),
                    contentDescription = stringResource(id = R.string.info)
                )
            }
            Text(
                color = colorResource(id = R.color.or_with_color),
                text = "${sliderPosition.toInt()}${stringResource(id = R.string.trailing_text_spend_limit)}",
                fontSize = 13.sp
            )
        }
        Slider(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            value = sliderPosition,
            onValueChange = { sliderPosition = it.toInt().toFloat() },
            valueRange=0f..100f,
            colors = SliderDefaults.colors().copy(
                thumbColor = colorResource(id = R.color.loginText),
                activeTrackColor = colorResource(id = R.color.loginText)
            )
        )
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
            text = stringResource(id = R.string.safe_to_spend_recommended),
            color = colorResource(id = R.color.or_with_color),
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp
        )
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                .size(48.dp),
            textRes = R.string.general_continue,
            onClick = {
                showLoader = true
                viewModel.setMonthlyLimit(
                    MonthlyBudgetInfoModel(
                        monthlyBudget = budget,
                        safeToSpendLimit = sliderPosition.toInt()
                    )
                )
            },
            enabled = budget.isNotEmpty()
        )
    }
    BottomDrawerForInfo(
        texts = stringResource(id = R.string.safe_to_spend_info),
        showSheet = showInfo
    ) {
            showInfo = false
    }
    FullScreenProgressIndicator(show = showLoader)
}