package com.penny.planner.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDrawerForLogout(
    modifier: Modifier,
    showSheet: Boolean,
    onClose: () -> Unit,
    onLogoutConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (sheetState.isVisible && !showSheet) {
        LaunchedEffect(key1 = "") {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onClose.invoke()
                }
            }
        }
    } else if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onClose,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = stringResource(id = R.string.logout_header),
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                text = stringResource(id = R.string.logout_body),
                fontSize = 16.sp,
                color = colorResource(id = R.color.or_with_color),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                SecondaryButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    onClick = onClose,
                    textRes = R.string.no
                )
                PrimaryButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    onClick = onLogoutConfirm,
                    textRes = R.string.yes,
                    enabled = true
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLogoutDrawer() {
    BottomDrawerForLogout(modifier = Modifier, showSheet = true, onClose = { /*TODO*/ }) {

    }
}