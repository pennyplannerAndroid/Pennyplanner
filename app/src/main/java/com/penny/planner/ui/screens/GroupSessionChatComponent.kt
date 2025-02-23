package com.penny.planner.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.penny.planner.R
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.dpToPx
import com.penny.planner.helpers.keyboardAsState
import com.penny.planner.helpers.noRippleClickable
import com.penny.planner.helpers.pxToDp
import com.penny.planner.models.GroupDisplayModel
import com.penny.planner.ui.components.ExpenseListItem
import com.penny.planner.ui.components.GroupChatTextField
import com.penny.planner.ui.components.GroupSessionTopBar
import com.penny.planner.ui.components.VerticalSwitch
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GroupSessionChatComponent(
    adminApprovals: Boolean,
    group: GroupEntity,
    transitionList: List<GroupDisplayModel>,
    monthlyExpenseEntity: MonthlyExpenseEntity,
    addExpenseClick: () -> Unit,
    memberClick: () -> Unit,
    sendClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val systemUiController = rememberSystemUiController()
    val statusBarColor = colorResource(id = R.color.loginText)
    val state: LazyListState = rememberLazyListState()
    var isSwitchOff by remember { mutableStateOf(true) }

    var isExpanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isExpanded, label = "SlideAnimation")
    val offsetXForOptions by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600, easing = FastOutSlowInEasing) },
        label = "OffsetX"
    ) { expanded ->
        if (expanded) 0f else -(56.dp.dpToPx())  // Slide in from left
    }

    val alphaForOptions by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "Alpha"
    ) { expanded -> if (expanded) 1f else 0f }

    LaunchedEffect(key1 = true) {
        systemUiController.statusBarDarkContentEnabled = true
        systemUiController.setSystemBarsColor(color = statusBarColor)
        systemUiController.setNavigationBarColor(color = Color.White)
    }
    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(key1 = transitionList, key2 = isKeyboardOpen) {
        if (transitionList.isNotEmpty()) {
            scope.launch {
                state.scrollToItem((transitionList.size - 1).coerceAtLeast(0))
            }
        }
    }

    Scaffold(
        topBar = {
            GroupSessionTopBar(
                adminApprovals = adminApprovals,
                group = group,
                monthlyExpenseEntity = monthlyExpenseEntity,
                onClick = {
                    focusManager.clearFocus()
                },
                memberClick = {
                    memberClick.invoke()
                }
            ) {
                isExpanded = !isExpanded
            }
        },
        content = { contentPadding ->
            Row(
                modifier = Modifier
                    .padding(contentPadding)
                    .noRippleClickable {
                        focusManager.clearFocus()
                    }
                    .padding(4.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .animateContentSize()
                        .offset { IntOffset(offsetXForOptions.roundToInt(), 0) }
                        .width(
                            56.dp + offsetXForOptions
                                .roundToInt()
                                .pxToDp()
                        )
                        .alpha(alphaForOptions),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
                    colors = CardDefaults.cardColors().copy(containerColor = colorResource(id = R.color.loginText))
                ) {
                    ShowGroupPicture(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        imageUrl = group.localImagePath.ifEmpty { group.profileImage }
                    )
                    VerticalSwitch(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        isOn = isSwitchOff
                    ) {
                        isSwitchOff = it
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 12.dp)
                            .width(56.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 24.dp)
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.group_notification),
                            contentDescription = "",
                            tint = Color.White
                        )
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 24.dp)
                                .size(32.dp),
                            painter = painterResource(id = R.drawable.app_setting_icon),
                            contentDescription = "",
                            tint = Color.White
                        )
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp)
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.add_to_group_icon),
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = if (isExpanded) 4.dp else 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp),
                    colors = CardDefaults.cardColors().copy(containerColor = colorResource(id = R.color.loginButton))
                ) {
                    LazyColumn(
                        state = state
                    ) {
                        items(transitionList) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                if (!item.isSentTransaction) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        GlideImage(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .align(Alignment.CenterVertically)
                                                .border(
                                                    color = colorResource(id = R.color.textField_border),
                                                    width = 2.dp,
                                                    shape = CircleShape
                                                )
                                                .clip(CircleShape),
                                            model = item.localImagePath,
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop
                                        ) {
                                            it.load(item.localImagePath)
                                                .placeholder(R.drawable.default_user_display)
                                                .error(R.drawable.default_user_display)
                                        }
                                        Text(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .align(Alignment.CenterVertically),
                                            text = item.senderName,
                                            maxLines = 1,
                                            fontSize = 13.sp,
                                            color = colorResource(id = R.color.loginText),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .align(
                                            if (item.isSentTransaction) Alignment.End else Alignment.Start
                                        ),
                                    colors = CardDefaults
                                        .cardColors()
                                        .copy(
                                            containerColor = if (item.isSentTransaction) CardDefaults.cardColors().containerColor else Color.White
                                        ),
                                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp)
                                ) {
                                    if (item.entityType == 0) {
                                        Text(
                                            modifier = Modifier
                                                .align(if (item.isSentTransaction) Alignment.End else Alignment.Start)
                                                .padding(8.dp)
                                                .widthIn(
                                                    min = 30.dp,
                                                    max = (LocalConfiguration.current.screenWidthDp * 0.6).dp
                                                ),
                                            text = item.content
                                        )
                                    } else {
                                        ExpenseListItem(item = item)
                                    }
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp, end = 4.dp, bottom = 6.dp),
                                text = Utils.convertMillisToTime(item.time.toDate()),
                                maxLines = 1,
                                fontSize = 10.sp,
                                color = colorResource(id = R.color.or_with_color),
                                textAlign = if (item.isSentTransaction) TextAlign.End else TextAlign.Start
                            )

                        }
                    }
                }
            }
        },
        bottomBar = {
            GroupChatTextField(
                addExpenseClick = addExpenseClick
            ) {
                sendClick.invoke(it)
            }
        }
    )
}

@Preview
@Composable
fun PreviewGroupSession() {
    val groupList = ArrayList<GroupDisplayModel>()

    groupList.add(GroupDisplayModel(
        category = "",
        content = "hello",
        entityType = 0,
        icon = "",
        paymentType = "",
        price = 0.0,
        subCategory = "",
        groupId = "8J3RBbIdiXUhq1QTAGsS",
        time = com.google.firebase.Timestamp(seconds = 1739905754, nanoseconds = 219000000),
        isSentTransaction = true,
        senderName = "A",
        senderEmail = "as.eemsrivastava452@gmail.com",
        senderImage = "https://firebasestorage.googleapis.com/v0/b/pennyplanner-d234f.appspot.com/o/UserImage%2F9T2m2SCdjKZVQyVWLVBdrf2dmxG3?alt=media&token=4d2c99d4-47f4-4ace-9896-6ccf596327af",
        localImagePath = ""
    ))

    groupList.add(GroupDisplayModel(
        category = "",
        content = "yahan pe Ghar ka kharcha dala jaye",
        entityType = 0,
        icon = "",
        paymentType = "",
        price = 0.0,
        subCategory = "",
        groupId = "8J3RBbIdiXUhq1QTAGsS",
        time = com.google.firebase.Timestamp(seconds = 1739905751, nanoseconds = 219000000),
        isSentTransaction = true,
        senderName = "A",
        senderEmail = "as.eemsrivastava452@gmail.com",
        senderImage = "https://firebasestorage.googleapis.com/v0/b/pennyplanner-d234f.appspot.com/o/UserImage%2F9T2m2SCdjKZVQyVWLVBdrf2dmxG3?alt=media&token=4d2c99d4-47f4-4ace-9896-6ccf596327af",
        localImagePath = ""
    ))

    groupList.add(GroupDisplayModel(
        category = "Shopping",
        content = "pochha and kitchen \ntowels \n",
        entityType = 1,
        icon = "🛍️",
        paymentType = "UPI",
        price = 50.0,
        subCategory = "Default",
        groupId = "8J3RBbIdiXUhq1QTAGsS",
        time = com.google.firebase.Timestamp(seconds = 1739905752, nanoseconds = 219000000),
        isSentTransaction = true,
        senderName = "A",
        senderEmail = "as.eemsrivastava452@gmail.com",
        senderImage = "https://firebasestorage.googleapis.com/v0/b/pennyplanner-d234f.appspot.com/o/UserImage%2F9T2m2SCdjKZVQyVWLVBdrf2dmxG3?alt=media&token=4d2c99d4-47f4-4ace-9896-6ccf596327af",
        localImagePath = ""
    ))

    groupList.add(GroupDisplayModel(
        category = "",
        content = "hello",
        entityType = 0,
        icon = "",
        paymentType = "",
        price = 0.0,
        subCategory = "",
        groupId = "8J3RBbIdiXUhq1QTAGsS",
        time = com.google.firebase.Timestamp(seconds = 1739905754, nanoseconds = 219000000),
        isSentTransaction = false,
        senderName = "A",
        senderEmail = "as.eemsrivastava452@gmail.com",
        senderImage = "https://firebasestorage.googleapis.com/v0/b/pennyplanner-d234f.appspot.com/o/UserImage%2F9T2m2SCdjKZVQyVWLVBdrf2dmxG3?alt=media&token=4d2c99d4-47f4-4ace-9896-6ccf596327af",
        localImagePath = ""
    ))

    groupList.add(GroupDisplayModel(
        category = "",
        content = "yahan pe Ghar ka kharcha dala jaye",
        entityType = 0,
        icon = "",
        paymentType = "",
        price = 0.0,
        subCategory = "",
        groupId = "8J3RBbIdiXUhq1QTAGsS",
        time = com.google.firebase.Timestamp(seconds = 1739905751, nanoseconds = 219000000),
        isSentTransaction = false,
        senderName = "A",
        senderEmail = "as.eemsrivastava452@gmail.com",
        senderImage = "https://firebasestorage.googleapis.com/v0/b/pennyplanner-d234f.appspot.com/o/UserImage%2F9T2m2SCdjKZVQyVWLVBdrf2dmxG3?alt=media&token=4d2c99d4-47f4-4ace-9896-6ccf596327af",
        localImagePath = ""
    ))

    groupList.add(GroupDisplayModel(
        category = "Shopping",
        content = "pochha and kitchen \ntowels \n",
        entityType = 1,
        icon = "🛍️",
        paymentType = "UPI",
        price = 50.0,
        subCategory = "Default",
        groupId = "8J3RBbIdiXUhq1QTAGsS",
        time = com.google.firebase.Timestamp(seconds = 1739905752, nanoseconds = 219000000),
        isSentTransaction = false,
        senderName = "A",
        senderEmail = "as.eemsrivastava452@gmail.com",
        senderImage = "https://firebasestorage.googleapis.com/v0/b/pennyplanner-d234f.appspot.com/o/UserImage%2F9T2m2SCdjKZVQyVWLVBdrf2dmxG3?alt=media&token=4d2c99d4-47f4-4ace-9896-6ccf596327af",
        localImagePath = ""
    ))
    GroupSessionChatComponent(
        adminApprovals = true,
        group = GroupEntity(name = "Ghar ka Kharcha", monthlyBudget = 82000.0),
        transitionList = groupList,
        monthlyExpenseEntity = MonthlyExpenseEntity(
            expense = 25000.0
        ), {}, {}
    ) {}
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowGroupPicture(
    modifier: Modifier,
    imageUrl: String
) {
    GlideImage(
        modifier = modifier
            .size(48.dp)
            .border(
                color = colorResource(id = R.color.white),
                width = 2.dp,
                shape = CircleShape
            )
            .clip(CircleShape),
        model = imageUrl,
        contentDescription = "",
        contentScale = ContentScale.Crop
    ) {
        it.load(imageUrl)
            .placeholder(R.drawable.default_user_display)
            .error(R.drawable.default_user_display)
    }
}
