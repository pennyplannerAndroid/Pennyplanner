package com.penny.planner.ui.screens.mainpage

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.penny.planner.ui.components.GroupItem
import com.penny.planner.viewmodels.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen() {
    var email by remember { mutableStateOf("") }
    var check by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<GroupViewModel>()
    val friendResult = viewModel.searchEmailResult.observeAsState().value
    var createGroup by remember {
        mutableStateOf(false)
    }
    var friends by remember {
        mutableStateOf(listOf<String>())
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Groups",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        content = { paddingValues ->
            viewModel.getAllGroups()
            val groups = viewModel.allGroups.observeAsState().value
            if(groups != null) {
                LazyVerticalGrid(
                    modifier = Modifier.padding(24.dp),
                    columns = GridCells.Fixed(count = 2)
                ) {
                    items(groups) {
                        Log.d("PendingGroups :: ", it.name)
                        GroupItem(modifier = Modifier.padding(paddingValues), entity = it)
                    }
                }
            }
//            Column (
//                modifier = Modifier.padding(paddingValues)
//            ) {
//                Text(
//                    modifier = Modifier.padding(16.dp),
//                    text = "Lets add someone to make a expense group with!",
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 22.sp,
//                    color = colorResource(id = R.color.black)
//                )
//                OutLinedTextFieldForEmail(modifier = Modifier, email = email) {
//                    email = it
//                }
//                TextFieldErrorIndicator(
//                    modifier = Modifier,
//                    textRes = R.string.invalid_email,
//                    show = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
//                )
//                if (friendResult != null && friendResult.isSuccess) {
//                    val friend = friendResult.getOrNull()
//                    if (friend != null) {
//                        friends = listOf(friend.email!!)
//                        FriendInfoCard(
//                            modifier = Modifier,
//                            model = friend,
//                            onCLick = { createGroup = true }
//                        ) {
//                                viewModel.resetFoundFriend()
//                        }
//                    }
//                } else if (friendResult != null && friendResult.isFailure) {
//                    Toast.makeText(
//                        context,
//                        friendResult.exceptionOrNull()?.message ?: stringResource(
//                            id = R.string.operation_failed
//                        ),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                PrimaryButton(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
//                        .size(48.dp),
//                    textRes = R.string.new_group,
//                    onClick = {
//                        check = true
//                        viewModel.findUser(email)
//                    },
//                    enabled = Patterns.EMAIL_ADDRESS.matcher(email).matches()
//                )
//                FullScreenProgressIndicator(show = check)
//            }
//            AddNewGroupDrawer(onClose = { createGroup = false }, showSheet = createGroup) { name, imageArray, imageUri ->
//                viewModel.newGroup(name = name, path = imageUri, members = friends, byteArray = imageArray)
//
//            }
        }
    )
}

@Preview
@Composable
fun PreviewGroupScreen() {
    GroupScreen()
}