package com.penny.planner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.noRippleClickable
import com.penny.planner.helpers.pxToDp
import com.penny.planner.ui.enums.JoinGroupUIStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGroupDrawer(
    modifier: Modifier,
    showSheet: Boolean,
    onClose: () -> Unit,
    pageState: JoinGroupUIStatus,
    groupId: String,
    group: GroupEntity?,
    joinGroupClicked: () -> Unit,
    showLoader: Boolean,
    searchGroup: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var loader by remember {
        mutableStateOf(false)
    }
    var size by remember {
        mutableIntStateOf(0)
    }
    if (!showLoader)
        loader = false

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
            modifier = modifier
                .imePadding(),
            onDismissRequest = onClose,
            sheetState = sheetState
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .onPlaced {
                        size = it.size.height
                    }
            ) {
                Column {
                    Text(
                        modifier = modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(id = R.string.join_group),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                    when (pageState) {
                        JoinGroupUIStatus.GroupIdPage -> {
                            JoinGroupGroupIdComponent(
                                modifier = modifier,
                                groupIdFetchedFromLink = groupId
                            ) {
                                loader = true
                                searchGroup.invoke(it)
                            }
                        }

                        JoinGroupUIStatus.GroupDetailPage -> {
                            JoinGroupDetailComponent(
                                modifier = modifier.align(Alignment.CenterHorizontally),
                                foundGroup = group!!
                            ) {
                                loader = true
                                joinGroupClicked.invoke()
                            }
                        }

                        JoinGroupUIStatus.SuccessPage -> {
                            JoinGroupSuccessComponent(
                                modifier = modifier
                                    .align(Alignment.CenterHorizontally),
                                foundGroup = group!!
                            ) {
                                onClose.invoke()
                            }
                        }
                    }
                }
                if (loader) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(size.pxToDp())
                            .noRippleClickable { }
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                shape = MaterialTheme.shapes.medium
                            )
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Row {
                            CircularProgressIndicator()
                            Text(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .align(Alignment.CenterVertically),
                                text = Utils.PLEASE_WAIT,
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JoinGroupGroupIdComponent(
    modifier: Modifier,
    groupIdFetchedFromLink: String,
    onClick: (String) -> Unit
) {
    var groupId by remember {
        mutableStateOf(groupIdFetchedFromLink)
    }
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        text = stringResource(id = R.string.join_existing_group),
        fontSize = 16.sp,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold
    )
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorResource(id = R.color.textField_border),
            focusedBorderColor = colorResource(
                id = if (groupId.isNotEmpty())
                    R.color.loginText else
                    R.color.red
            )
        ),
        shape = RoundedCornerShape(12.dp),
        value = groupId,
        onValueChange = {
            groupId = it
        },
        label = {
            Text(stringResource(id = R.string.group_id))
        },
    )
    PrimaryButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        textRes = R.string.search_group,
        onClick = {
            onClick.invoke(groupId)
        },
        enabled = groupId.isNotEmpty()
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun JoinGroupDetailComponent(
    modifier: Modifier,
    foundGroup: GroupEntity,
    onClick: () -> Unit
) {
    val group by remember {
        mutableStateOf(foundGroup)
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        GlideImage(
            modifier = Modifier
                .padding(12.dp)
                .size(64.dp)
                .border(
                    color = colorResource(id = R.color.textField_border),
                    width = 2.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            model = group.profileImage,
            contentDescription = "",
            contentScale = ContentScale.Crop
        ) {
            it.load(group.profileImage)
                .placeholder(R.drawable.default_user_display)
                .error(R.drawable.default_user_display)
        }

        Text(
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            text = group.name,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            text = String.format(
                stringResource(id = R.string.member_count_in_group),
                group.members.size
            ),
            color = Color.LightGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    PrimaryButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        textRes = R.string.request_to_join,
        onClick = {
            onClick.invoke()
        },
        enabled = true
    )
}

@Composable
fun JoinGroupSuccessComponent(
    modifier: Modifier,
    foundGroup: GroupEntity,
    onClick: () -> Unit
) {
    val group by remember {
        mutableStateOf(foundGroup)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .padding(12.dp)
                .size(64.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.success_icon),
            contentDescription = stringResource(id = R.string.success),
        )
        Text(
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
            text = String.format(stringResource(id = R.string.join_group_request_sent), group.name),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = modifier
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.join_group_intimation_information),
            color = Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }

    PrimaryButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        textRes = R.string.ok,
        onClick = {
            onClick.invoke()
        },
        enabled = true
    )
}


@Preview
@Composable
fun JoinGroupDrawerPreview() {
    JoinGroupDrawer(
        Modifier,
        true,
        {},
        JoinGroupUIStatus.GroupIdPage,
        "",
        GroupEntity(name = "Test Group"),
        {},
        true
    ) {}
}
