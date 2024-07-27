package com.penny.planner.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import kotlin.math.abs

@Composable
fun TutorialScreen(
    modifier: Modifier,
    onLoginClick : () -> Unit,
    onSignupClick : () -> Unit
) {
    var direction by remember { mutableIntStateOf(-1) }
    var position by remember { mutableIntStateOf(0) }
    var slideLeft by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            if (abs(x) > abs(y)) {
                                when {
                                    x > 0 -> direction = 0
                                    x < 0 -> direction = 1
                                }
                            }
                        },
                        onDragEnd = {
                            when (direction) {
                                0 -> {
                                    Log.d("SwipeDirection", "right $position")
                                    if (position > 0) {
                                        position--
                                        slideLeft = false
                                    }
                                }

                                1 -> {
                                    Log.d("SwipeDirection", "left $position")
                                    if (position < 2) {
                                        position++
                                        slideLeft = true
                                    }
                                }
                            }
                        }
                    )
                }
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SetBodyWithSwipe(value = position, slideLeft)
            Row(
               verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(
                        id =
                        if (position == 0) R.drawable.selected_dot else R.drawable.not_selected_dot
                    ),
                    contentDescription = "selected",
                    modifier = Modifier.padding(5.dp).animateContentSize { _, _ -> 16.dp }
                )
                Image(
                    painter = painterResource(
                        id =
                        if (position == 1) R.drawable.selected_dot else R.drawable.not_selected_dot
                    ),
                    contentDescription = "selected",
                    modifier = Modifier.padding(5.dp).animateContentSize { _, _ -> 16.dp }
                )
                Image(
                    painter = painterResource(
                        id =
                        if (position == 2) R.drawable.selected_dot else R.drawable.not_selected_dot
                    ),
                    contentDescription = "selected",
                    modifier = Modifier.padding(5.dp).animateContentSize { _, _ -> 16.dp }
                )
            }
        }
        Button(
            onClick = onSignupClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .size(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonColors(
                colorResource(id = R.color.loginText),
                colorResource(id = R.color.white),
                colorResource(id = R.color.teal_200),
                colorResource(id = R.color.teal_200)
            )
        ) {
            Text(text = stringResource(id = R.string.signup))
        }

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 24.dp)
                .size(48.dp),
            colors = ButtonColors(
                colorResource(id = R.color.loginButton),
                colorResource(id = R.color.loginText),
                colorResource(id = R.color.teal_200),
                colorResource(id = R.color.teal_200)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(id = R.string.login))
        }
    }
}

@Composable
fun SetBodyWithSwipe(value: Int, slideLeft : Boolean) {
    val duration = 100
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            if (slideLeft) {
                slideInHorizontally(
                    animationSpec = tween(duration),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(duration),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        )
            } else {
                slideInHorizontally(
                    animationSpec = tween(duration),
                    initialOffsetX = { fullWidth -> -fullWidth }
                ) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(duration),
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
            }
        }, label = ""
    ) { targetState ->
        when (targetState) {
            0 -> {
                Body(
                    iconId = R.drawable.onboarding_1,
                    text1 = stringResource(id = R.string.onboarding_header_text_1),
                    text2 = stringResource(id = R.string.onboarding_body_text_1)
                )
            }
            1 -> {
                Body(
                    iconId = R.drawable.onboarding_2,
                    text1 = stringResource(id = R.string.onboarding_header_text_2),
                    text2 = stringResource(id = R.string.onboarding_body_text_2)
                )
            }
            2 -> {
                Body(
                    iconId = R.drawable.onboarding_3,
                    text1 = stringResource(id = R.string.onboarding_header_text_3),
                    text2 = stringResource(id = R.string.onboarding_body_text_3)
                )
            }
        }
    }
}

@Composable
fun Body(iconId: Int, text1: String, text2: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "onboarding image",

        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text1,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text2,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
@Preview
fun Preview() {
    TutorialScreen(modifier = Modifier, {}, {})
}