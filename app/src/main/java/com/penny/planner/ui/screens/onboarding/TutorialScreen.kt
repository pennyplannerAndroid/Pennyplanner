package com.penny.planner.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penny.planner.R
import com.penny.planner.ui.components.DotImageWithAnimation
import com.penny.planner.ui.components.InformationWithIconAndBody
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.SecondaryButton

@Composable
fun TutorialScreen(
    modifier: Modifier,
    onLoginClick : () -> Unit,
    onSignupClick : () -> Unit
) {
    val state = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxSize()
        .navigationBarsPadding()
    ) {
        HorizontalPager(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            state = state) { page ->
            when (page) {
                0 -> {
                    InformationWithIconAndBody(
                        iconId = R.drawable.onboarding_1,
                        text1 = stringResource(id = R.string.onboarding_header_text_1),
                        text2 = stringResource(id = R.string.onboarding_body_text_1)
                    )
                }
                1 -> {
                    InformationWithIconAndBody(
                        iconId = R.drawable.onboarding_2,
                        text1 = stringResource(id = R.string.onboarding_header_text_2),
                        text2 = stringResource(id = R.string.onboarding_body_text_2)
                    )
                }
                2 -> {
                    InformationWithIconAndBody(
                        iconId = R.drawable.onboarding_3,
                        text1 = stringResource(id = R.string.onboarding_header_text_3),
                        text2 = stringResource(id = R.string.onboarding_body_text_3)
                    )
                }
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            DotImageWithAnimation(selected = state.currentPage == 0)
            DotImageWithAnimation(selected = state.currentPage == 1)
            DotImageWithAnimation(selected = state.currentPage == 2)
        }
        PrimaryButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .size(48.dp),
            textRes = R.string.signup,
            onClick = onSignupClick,
            enabled = true
        )
        SecondaryButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 24.dp)
                .size(48.dp),
            onClick = onLoginClick,
            textRes = R.string.login
        )
    }
}

@Composable
@Preview
fun Preview() {
    TutorialScreen(modifier = Modifier, {}, {})
}