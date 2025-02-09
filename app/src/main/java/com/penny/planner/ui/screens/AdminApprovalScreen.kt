package com.penny.planner.ui.screens

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.ui.components.FriendInfoCard
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TopBar
import com.penny.planner.viewmodels.AdminApprovalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AdminApprovalScreen(
    groupId: String
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<AdminApprovalViewModel>()
    var listToApprove: List<UsersEntity>? = remember {
        mutableStateListOf()
    }
    var showLoader by remember {
        mutableStateOf(true)
    }
    val approvalList = viewModel.approvalList.observeAsState().value
    if (approvalList != null) {
        showLoader = false
        if(approvalList.isSuccess) {
            listToApprove = approvalList.getOrNull()
            if (listToApprove!!.isEmpty()) {
                Toast.makeText(LocalContext.current, "No pending members!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(LocalContext.current, approvalList.exceptionOrNull()!!.message!!, Toast.LENGTH_SHORT).show()
        }
    }
    var group by remember {
        mutableStateOf(GroupEntity())
    }
    LaunchedEffect(key1 = "") {
        viewModel.setGroupId(groupId)
        scope.launch {
            group = viewModel.getGroup()
            viewModel.getApprovalList()
        }
    }
    Scaffold (
        topBar = {
            TopBar(modifier = Modifier, title = stringResource(id = R.string.pending_approvals)) {
                backDispatcher?.onBackPressed()
            }
        },
    ) { contentPadding ->
        Column(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .background(color = Color.White)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = colorResource(id = R.color.loginButton))
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp),
                    text = stringResource(id = R.string.pending_details),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.or_with_color),
                    lineHeight = TextUnit(18f, TextUnitType.Sp)
                )
            }
            GlideImage(
                modifier = Modifier
                    .padding(12.dp)
                    .border(
                        color = colorResource(id = R.color.black),
                        width = 2.dp,
                        shape = CircleShape
                    )
                    .size(64.dp)
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
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                text = group.name,
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 12.dp, bottom = 12.dp),
                text = stringResource(id = R.string.pending_members),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
                .height(1.dp)
                .background(color = Color.Gray),
                text = ""
            )
            if (!listToApprove.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyColumn {
                        items(listToApprove) {
                            FriendInfoCard(
                                modifier = Modifier,
                                model = it,
                                onCancel = {
                                    showLoader = true
                                    viewModel.reject(group = group, user = it, needUpdatePendingFlag = listToApprove.size == 1)
                                }
                            ) {
                                showLoader = true
                                viewModel.accept(group = group, user = it, needUpdatePendingFlag = listToApprove.size == 1)
                            }
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp)
                                .height(1.dp)
                                .background(color = Color.Gray),
                                text = ""
                            )
                        }
                    }
                    PrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textRes = R.string.reject_all_close,
                        onClick = {
                            showLoader = true
                            viewModel.rejectAll()
                        },
                        enabled = true
                    )
                }
            }

        }
    }
    FullScreenProgressIndicator(show = showLoader)
}

@Preview
@Composable
fun PreviewAdminApprovalScreen() {
    AdminApprovalScreen(
        ""
    )
}