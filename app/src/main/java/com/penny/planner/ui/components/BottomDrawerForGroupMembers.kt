package com.penny.planner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.penny.planner.R
import com.penny.planner.data.db.friends.UsersEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDrawerForGroupMembers(
    modifier: Modifier,
    list: List<UsersEntity>,
    showPendingApproval: Boolean,
    showSheet: Boolean,
    onClose: () -> Unit,
    self: String,
    admin: String,
    onAdminApprovalClick: () -> Unit,
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
            if (showPendingApproval) {
                PendingApprovalsPopup(modifier = modifier) {
                    onClose.invoke()
                    onAdminApprovalClick.invoke()
                }
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (list.size == 1) "1 member" else "${list.size} members",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                )
                Icon(
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            scope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onClose.invoke()
                                    }
                                }
                        },
                    painter = painterResource(id = R.drawable.cancel_button),
                    contentDescription = stringResource(id = R.string.cancel),
                    tint = colorResource(id = R.color.loginText)
                )
            }
            LazyColumn {
                items(list) {
                    FriendInfoForMemberList(
                        modifier = modifier,
                        model = it,
                        self = self,
                        admin = admin
                    )
                }
            }
        }
    }
}