package com.penny.planner.ui.screens.mainpage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.penny.planner.R
import com.penny.planner.helpers.noRippleClickable
import com.penny.planner.models.GroupListDisplayModel
import com.penny.planner.ui.components.BigFabMenuOption
import com.penny.planner.ui.components.GroupItem
import com.penny.planner.ui.components.SmallFabMenuWithDescription
import com.penny.planner.ui.enums.FloatingButtonState
import com.penny.planner.viewmodels.GroupViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    modifier: Modifier,
    addGroup: () -> Unit,
    sendInviteLink: (String) -> Unit,
    groupSession: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<GroupViewModel>()

    val lifeCycle = LocalLifecycleOwner.current

    var groups by remember {
        mutableStateOf(listOf<GroupListDisplayModel>())
    }
    var state by remember {
        mutableStateOf(FloatingButtonState.Collapsed)
    }
    LaunchedEffect(keys = emptyArray()) {
        scope.launch {
            viewModel.getAllGroups().observe(lifeCycle) {
                groups = it
            }
        }
    }

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
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(12.dp)
                        .navigationBarsPadding()
                        .blur(if (state == FloatingButtonState.Open) 10.dp else 0.dp),
                    columns = GridCells.Fixed(count = 2)
                ) {
                    items(groups) {
                        val friends = viewModel.getTwoFriends(it.members)
                        GroupItem(
                            modifier = Modifier,
                            entity = it,
                            isAdmin = viewModel.isAdmin(it.creatorId),
                            friendsForDisplayPicture = friends,
                            onClick = {
                                groupSession.invoke(it.groupId)
                            }
                        ) {
                            scope.launch {
                                sendInviteLink.invoke(viewModel.getJoinGroupLink(it.groupId))
                            }
                        }
                    }
                }
                val stateTransition: Transition<FloatingButtonState> =
                    updateTransition(targetState = state, label = "")
                val rotation: Float by stateTransition.animateFloat(
                    transitionSpec = {
                        if (state == FloatingButtonState.Open) {
                            spring(stiffness = Spring.StiffnessLow)
                        } else {
                            spring(stiffness = Spring.StiffnessMedium)
                        }
                    },
                    label = ""
                ) { state ->
                    if (state == FloatingButtonState.Open) 45f else 0f
                }
                AnimatedVisibility(
                    visible = state == FloatingButtonState.Open,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White.copy(alpha = 0.5f))
                        .noRippleClickable {
                            state = FloatingButtonState.Collapsed
                        }.blur(radius = 16.dp),
                        text = ""
                    )
                }
                Column(
                    modifier = modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp)
                ) {
                    AnimatedVisibility(
                        visible = state == FloatingButtonState.Open
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.End),
                            horizontalAlignment = Alignment.End
                        ) {
                            SmallFabMenuWithDescription(
                                modifier = Modifier
                                    .padding(bottom = 4.dp),
                                description = stringResource(id = R.string.create_group),
                                icon = R.drawable.create_group_small_fab
                            ) {
                                addGroup.invoke()
                            }
                            SmallFabMenuWithDescription(
                                modifier = Modifier
                                    .padding(bottom = 4.dp),
                                description = stringResource(id = R.string.join_group),
                                icon = R.drawable.join_group_fab_icon
                            ) {

                            }
                        }
                    }
                    BigFabMenuOption(
                        modifier = Modifier
                            .align(Alignment.End)
                            .rotate(rotation)
                    ) {
                        state = if (state == FloatingButtonState.Collapsed) FloatingButtonState.Open else FloatingButtonState.Collapsed
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewGroupScreen() {
    GroupScreen(Modifier, {}, {}) {}
}