package com.penny.planner.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.penny.planner.R
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel
import com.penny.planner.ui.components.BigFabMenuOption
import com.penny.planner.ui.components.ExpenseListItem
import com.penny.planner.ui.components.GroupSessionTopBar
import com.penny.planner.ui.components.PendingApprovalsPopup
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
    var message by remember {
        mutableStateOf("")
    }
    var addExpense by remember {
        mutableStateOf(false)
    }
    var showPendingApprovalPopup by remember {
        mutableStateOf(false)
    }

    val approvalList = viewModel.approvalList.observeAsState().value
    if (approvalList != null) {
        if(approvalList) {
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
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                GroupSessionTopBar(entity = group) {

                }
            },
            bottomBar = {
                Row {
                    BigFabMenuOption(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        addExpense = true
                    }
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(
                                id = if (message.isEmpty()) R.color.textField_border
                                else R.color.loginText
                            ),
                        ),
                        shape = RoundedCornerShape(12.dp),
                        value = message,
                        onValueChange = {
                            message = it
                        },
                        label = {
                            Text(text = "Type a message...")
                        },
                        trailingIcon = {
                            if (message.isNotEmpty()) {
                                Image(
                                    modifier = Modifier.clickable {
                                        viewModel.addMessage(message)
                                        message = ""
                                    },
                                    painter = painterResource(id = R.drawable.send_icon),
                                    contentDescription = ""
                                )
                            }
                        }
                    )
                }
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                if (showPendingApprovalPopup) {
                    PendingApprovalsPopup(
                        modifier = Modifier
                    ) {
                        if (Utils.isNetworkAvailable(context))
                            onPendingApprovalClick.invoke(groupId)
                        else
                            Toast.makeText(context, "Network not available!", Toast.LENGTH_SHORT).show()
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(contentPadding)
                ) {
                    items(transitionList) { item ->
                        if (item.entityType == 0) {
                            TextTransaction(content = item.content, isSent = item.isSentTransaction)
                        } else {
                            ExpenseListItem(item = item)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun TextTransaction(content: String, isSent: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .wrapContentWidth()
        ) {
            Text(
                modifier = Modifier
                    .align(if (isSent) Alignment.End else Alignment.Start)
                    .padding(12.dp),
                text = content
            )
        }
    }
}

@Preview
@Composable
fun PreviewGroupSessionScreen() {
    GroupSessionScreen("") {}
}