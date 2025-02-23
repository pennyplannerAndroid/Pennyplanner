package com.penny.planner.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.helpers.dpToPx
import com.penny.planner.helpers.keyboardAsState
import com.penny.planner.helpers.pxToDp
import kotlin.math.roundToInt

@Composable
fun GroupChatTextField(
    addExpenseClick: () -> Unit,
    sendClick: (String) -> Unit
) {

    var message by remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current
    val isKeyboardOpen by keyboardAsState()
    val transition1 = updateTransition(targetState = isKeyboardOpen, label = "SlideAnimation")
    val offsetXForAddIcon by transition1.animateFloat(
        transitionSpec = { tween(durationMillis = 100, easing = FastOutSlowInEasing) },
        label = "offsetXForAddIcon"
    ) { open ->
        if (open) -(38.dp.dpToPx())  else 0f // Slide in from left
    }
    if (!isKeyboardOpen) {
        focusManager.clearFocus()
    }

    val alphaForAddIcon by transition1.animateFloat(
        transitionSpec = { tween(durationMillis = 100) },
        label = "Alpha"
    ) { open -> if (open) 0f else 1f }

    val transition2 = updateTransition(targetState = message.isEmpty(), label = "SlideAnimation")
    val offsetXForSendIcon by transition2.animateFloat(
        transitionSpec = { tween(durationMillis = 100, easing = FastOutSlowInEasing) },
        label = "offsetXForSendIcon"
    ) { empty ->
        if (empty) 100f else 0f
    }

    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.white),
                    shape = RoundedCornerShape(12.dp)
                ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
            colors = CardDefaults.cardColors().copy(containerColor = colorResource(id = R.color.white))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .animateContentSize()
                        .offset { IntOffset(offsetXForAddIcon.roundToInt(), 0) }
                        .width(
                            40.dp + offsetXForAddIcon
                                .roundToInt()
                                .pxToDp()
                        )
                        .height(40.dp)
                        .alpha(alphaForAddIcon),
                    onClick = {
                        addExpenseClick.invoke()
                    },
                    shape = CircleShape,
                    containerColor = colorResource(id = R.color.loginText)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add_without_background),
                        contentDescription = stringResource(id = R.string.add)
                    )
                }

                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if(isKeyboardOpen) 0.dp else 4.dp)
                        .align(Alignment.CenterVertically),
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, color = Color.Black),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            if (message.isEmpty()) {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 4.dp),
                                    text = "Type here...",
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Image(
                    modifier = Modifier
                        .clickable {
                            sendClick.invoke(message)
                            message = ""
                        }
                        .padding(start = 8.dp, end = 8.dp)
                        .size(32.dp)
                        .align(Alignment.CenterVertically)
                        .animateContentSize()
                        .offset { IntOffset(offsetXForSendIcon.roundToInt(), 0) },
                    painter = painterResource(id = R.drawable.send_icon),
                    contentDescription = ""
                )
            }
        }
    }
}