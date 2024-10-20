package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDrawerForImageUpload(
    modifier: Modifier,
    texts: List<String>,
    icons: List<Int>,
    showSheet: Boolean,
    onCLose: () -> Unit,
    onKeyClick: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (sheetState.isVisible && !showSheet) {
        LaunchedEffect(key1 = "") {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onCLose.invoke()
                }
            }
        }
    } else if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onCLose,
            sheetState = sheetState
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.add_profile_picture),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onCLose.invoke()
                                }
                            }
                        },
                    painter = painterResource(id = R.drawable.cancel_button),
                    contentDescription = stringResource(id = R.string.cancel),
                    tint = colorResource(id = R.color.loginText)
                )

            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clickable { onKeyClick(0) }
                        .background(
                            color = colorResource(id = R.color.loginButton),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = icons[0]),
                        contentDescription = texts[0]
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        text = texts[0],
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = R.color.loginText)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clickable { onKeyClick(1) }
                        .background(
                            color = colorResource(id = R.color.loginButton),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = icons[1]),
                        contentDescription = texts[1]
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        text = texts[1],
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = R.color.loginText)
                    )
                }
                if (texts.size > 2) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .clickable { onKeyClick(2) }
                            .background(
                                color = colorResource(id = R.color.loginButton),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = icons[2]),
                            contentDescription = texts[2]
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            text = texts[2],
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.loginText)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDrawerForInfo(
    text: String,
    showSheet: Boolean,
    onCLose: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (sheetState.isVisible && !showSheet) {
        LaunchedEffect(key1 = "") {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onCLose.invoke()
                }
            }
        }
    } else if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onCLose,
            sheetState = sheetState
        ) {
            Column{
                Image(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(id = R.drawable.tip_bulb_icon),
                    contentDescription = stringResource(id = R.string.info)
                )
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                        .align(Alignment.CenterHorizontally),
                    text = text
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    textRes = R.string.ok,
                    onClick = {
                        onCLose.invoke()
                    },
                    enabled = true
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewBottomDrawer() {
    BottomDrawerForImageUpload(Modifier, listOf("Camera", "Gallery", "Delete"), listOf(R.drawable.camera_icon, R.drawable.gallery_icon, R.drawable.delete_icon), true, {}) {

    }
}