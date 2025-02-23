package com.penny.planner.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.models.GroupDisplayModel
import com.penny.planner.viewmodels.GroupSessionViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupSessionScreen(
    groupId: String,
    onPendingApprovalClick: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<GroupSessionViewModel>()
    val lifeCycle = LocalLifecycleOwner.current
    var group by remember {
        mutableStateOf(GroupEntity())
    }
    var transitionList by remember {
        mutableStateOf(listOf<GroupDisplayModel>())
    }

    var monthlyExpenses by remember {
        mutableStateOf(MonthlyExpenseEntity())
    }

    var addExpense by remember {
        mutableStateOf(false)
    }
    var showPendingApprovalPopup by remember {
        mutableStateOf(false)
    }

    val approvalList = viewModel.approvalList.observeAsState().value
    if (approvalList != null) {
        if (approvalList) {
            showPendingApprovalPopup = true
        }
    }
    LaunchedEffect(key1 = "") {
        viewModel.setGroupId(groupId)
        scope.launch {
            group = viewModel.getGroup()
            viewModel.updateMembers(group)
            if (viewModel.isAdmin(group) && group.status == 1) {
                if (group.hasPendingMembers)
                    showPendingApprovalPopup = true
                else
                    viewModel.getApprovalList()
            }
            viewModel.getAllExpenses().observe(lifeCycle) {
                transitionList = it
                scope.launch {
                    val monthlyExpense = viewModel.getMonthlyExpenses()
                    if (monthlyExpense != null)
                        monthlyExpenses = monthlyExpense
                }
            }
        }
    }

    if (addExpense) {
        AddExpenseScreen(
            onDismiss = { addExpense = false },
            groupId = groupId
        ) {
            addExpense = false
            viewModel.addExpense(it)
        }
    } else {
        GroupSession(
            group = group,
            transitionList = transitionList,
            monthlyExpenseEntity = monthlyExpenses,
            addExpenseClick = {
              addExpense = true
            }
        ) {
            viewModel.addMessage(it)
        }
//        Row(
//        ) {
//            Card(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .statusBarsPadding()
//                    .navigationBarsPadding()
//                    .width(72.dp)
//                    .padding(4.dp)
//                    .background(
//                        color = colorResource(id = R.color.white),
//                        shape = RoundedCornerShape(12.dp)
//                    )
//                    .padding(top = 4.dp, bottom = 12.dp),
//                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
//                colors = CardDefaults.cardColors().copy(containerColor =colorResource(id = R.color.loginText))
//            ) {
//                GlideImage(
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .size(40.dp)
//                        .border(
//                            color = colorResource(id = R.color.textField_border),
//                            width = 2.dp,
//                            shape = CircleShape
//                        )
//                        .clip(CircleShape),
//                    model = if (group.localImagePath.isNotEmpty()) group.localImagePath else group.profileImage,
//                    contentDescription = "",
//                    contentScale = ContentScale.Crop
//                ) {
//                    it.load(if (group.localImagePath.isNotEmpty()) group.localImagePath else group.profileImage)
//                        .placeholder(R.drawable.default_user_display)
//                        .error(R.drawable.default_user_display)
//                }
//                BigFabMenuOption(
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .align(Alignment.End)
//                ) {
//                    addExpense = true
//                }
//            }
//            Scaffold(
//                modifier = Modifier
//                    .statusBarsPadding()
//                    .navigationBarsPadding(),
//                topBar = {
//                    GroupSessionTopBar(entity = group) {
//
//                    }
//                },
//                bottomBar = {
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        OutlinedTextField(
//                            modifier = Modifier
//                                .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
//                                .weight(1f),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                unfocusedBorderColor = colorResource(
//                                    id = if (message.isEmpty()) R.color.textField_border
//                                    else R.color.loginText
//                                ),
//                            ),
//                            shape = RoundedCornerShape(12.dp),
//                            value = message,
//                            onValueChange = {
//                                message = it
//                            },
//                            label = {
//                                Text(text = "Type a message")
//                            },
//                            trailingIcon = {
//                                if (message.isNotEmpty()) {
//                                    Image(
//                                        modifier = Modifier.clickable {
//                                            viewModel.addMessage(message)
//                                            message = ""
//                                        },
//                                        painter = painterResource(id = R.drawable.send_icon),
//                                        contentDescription = ""
//                                    )
//                                }
//                            }
//                        )
//                    }
//                }
//            ) { contentPadding ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding)
//                ) {
//                    if (showPendingApprovalPopup) {
//                        PendingApprovalsPopup(
//                            modifier = Modifier
//                        ) {
//                            if (Utils.isNetworkAvailable(context))
//                                onPendingApprovalClick.invoke(groupId)
//                            else
//                                Toast.makeText(context, "Network not available!", Toast.LENGTH_SHORT)
//                                    .show()
//                        }
//                    }
//                    LazyColumn {
//                        items(transitionList) { item ->
//                            Column(
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Card(
//                                    modifier = Modifier
//                                        .padding(start = 12.dp)
//                                        .align(
//                                            if (item.isSentTransaction) Alignment.End else Alignment.Start
//                                        ),
//                                    colors = CardDefaults
//                                        .cardColors()
//                                        .copy(
//                                            containerColor = if (item.isSentTransaction) CardDefaults.cardColors().containerColor else Color.White
//                                        ),
//                                    elevation = CardDefaults.elevatedCardElevation()
//                                ) {
//                                    if (item.entityType == 0) {
//                                        Text(
//                                            modifier = Modifier
//                                                .align(if (item.isSentTransaction) Alignment.End else Alignment.Start)
//                                                .padding(12.dp),
//                                            text = item.content
//                                        )
//                                    } else {
//                                        ExpenseListItem(item = item)
//                                    }
//                                }
//                            }
//                            Text(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 6.dp),
//                                text = Utils.convertMillisToTime(item.time.toDate()),
//                                maxLines = 1,
//                                fontSize = 13.sp,
//                                color = colorResource(id = R.color.or_with_color),
//                                textAlign = TextAlign.End
//                            )
//
//                        }
//                    }
//                }
//            }
//
//        }
    }

}

@Preview
@Composable
fun PreviewGroupSessionScreen() {
    GroupSessionScreen("") {}
}