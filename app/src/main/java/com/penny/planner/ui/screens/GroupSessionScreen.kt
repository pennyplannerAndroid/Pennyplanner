package com.penny.planner.ui.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel
import com.penny.planner.ui.components.BottomDrawerForGroupMembers
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
    var showMemberBottomSheet by remember {
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
        GroupSessionChatComponent(
            adminApprovals = showPendingApprovalPopup,
            group = group,
            transitionList = transitionList,
            monthlyExpenseEntity = monthlyExpenses,
            addExpenseClick = {
              addExpense = true
            },
            memberClick = {
                showMemberBottomSheet = true
            }
        ) {
            viewModel.addMessage(it)
        }
    }
    BottomDrawerForGroupMembers(
        modifier = Modifier,
        list = viewModel.getAllMembers(group),
        showPendingApproval = showPendingApprovalPopup,
        showSheet = showMemberBottomSheet,
        onClose = { showMemberBottomSheet = false },
        self = viewModel.getSelfId(),
        admin = group.creatorId
    ) {
        if (Utils.isNetworkAvailable(context))
            onPendingApprovalClick.invoke(groupId)
        else
            Toast.makeText(context, "Network not available!", Toast.LENGTH_SHORT)
                .show()
    }
}

@Preview
@Composable
fun PreviewGroupSessionScreen() {
    GroupSessionScreen("") {}
}