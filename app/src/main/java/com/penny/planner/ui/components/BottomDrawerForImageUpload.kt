package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column {
                    Image(
                        modifier = Modifier
                            .clickable { onKeyClick(0) }
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = icons[0]),
                        contentDescription = texts[0]
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        text = texts[0]
                    )
                }
                Column {
                    Image(
                        modifier = Modifier
                            .clickable { onKeyClick(1) }
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = icons[1]),
                        contentDescription = texts[1]
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        text = texts[1]
                    )
                }
                if (texts.size > 2) {
                    Column {
                        Image(
                            modifier = Modifier
                                .clickable { onKeyClick(2) }
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(id = icons[2]),
                            contentDescription = texts[2]
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                                .align(Alignment.CenterHorizontally),
                            text = texts[2]
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
    texts: String,
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
                    text = texts
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
    BottomDrawerForImageUpload(Modifier, listOf("Gallery", "Camera", "Delete"), listOf(R.drawable.gallery_icon, R.drawable.camera_icon, R.drawable.delete_icon), true, {}) {

    }
}