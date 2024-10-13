package com.penny.planner.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.R
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.ui.components.FriendInfoCard
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.OutLinedTextFieldForEmail
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.ui.components.TextFieldErrorIndicator
import com.penny.planner.viewmodels.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewGroupScreen(
    groupCreated: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var check by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<GroupViewModel>()
    val friendResult = viewModel.searchEmailResult.observeAsState().value

    var createGroup by remember {
        mutableStateOf(false)
    }
    var friends by remember {
        mutableStateOf(listOf<UsersEntity>())
    }
    val context = LocalContext.current

    val groupCreationStatus = viewModel.newGroupResult.observeAsState().value
    if (groupCreationStatus != null) {
        if (groupCreationStatus.isSuccess)
            groupCreated.invoke()
        else
            Toast.makeText(context, groupCreationStatus.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "New Group",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        content = { paddingValues ->
            Column (
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Lets add someone to make a expense group with!",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colorResource(id = R.color.black)
                )
                OutLinedTextFieldForEmail(modifier = Modifier, email = email) {
                    email = it
                }
                TextFieldErrorIndicator(
                    modifier = Modifier,
                    textRes = R.string.invalid_email,
                    show = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
                if (friendResult != null && friendResult.isSuccess) {
                    val friend = friendResult.getOrNull()
                    if (friend != null) {
                        friends = listOf(friend)
                        FriendInfoCard(
                            modifier = Modifier,
                            model = friend,
                            onCLick = { createGroup = true }
                        ) {
                                viewModel.resetFoundFriend()
                        }
                    }
                } else if (friendResult != null && friendResult.isFailure) {
                    Toast.makeText(
                        context,
                        friendResult.exceptionOrNull()?.message ?: stringResource(
                            id = R.string.operation_failed
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp),
                    textRes = R.string.new_group,
                    onClick = {
                        check = true
                        viewModel.findUser(email)
                    },
                    enabled = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )
                FullScreenProgressIndicator(show = check)
            }
            AddNewGroupDrawer(onClose = { createGroup = false }, showSheet = createGroup) { name, imageArray, imageUri ->
                viewModel.newGroup(name = name, path = imageUri, members = friends, byteArray = imageArray)

            }
        }
    )
}

@Preview
@Composable
fun PreviewAddNewGroupScreen() {
    AddNewGroupScreen {}
}