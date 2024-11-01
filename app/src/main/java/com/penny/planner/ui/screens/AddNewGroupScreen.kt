package com.penny.planner.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Picture
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.Utils.Const.createBitmapFromPicture
import com.penny.planner.helpers.createImageFile
import com.penny.planner.helpers.keyboardAsState
import com.penny.planner.ui.components.BottomDrawerForImageUpload
import com.penny.planner.ui.components.BottomDrawerForInfo
import com.penny.planner.ui.components.ColoredTopBar
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.viewmodels.GroupViewModel
import java.util.Objects

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddNewGroupScreen(
    groupCreated: () -> Unit
) {
    val viewModel = hiltViewModel<GroupViewModel>()
    val focusManager = LocalFocusManager.current
    var name by remember {
        mutableStateOf("")
    }
    var showImageUploadBottomSheet by remember {
        mutableStateOf(false)
    }
    var budget by remember { mutableStateOf("") }

    var sliderPosition by remember { mutableFloatStateOf(80f) }
    var showInfo by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val picture = remember { Picture() }
    var showLoader by remember {
        mutableStateOf(false)
    }
    val isKeyboardOpen by keyboardAsState()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val context = LocalContext.current
    val view = LocalView.current

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        Utils.PROVIDER, file
    )

    SideEffect {
        val window = (context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    val groupCreationStatus = viewModel.newGroupResult.observeAsState().value
    if (groupCreationStatus != null) {
        showLoader = false
        if (groupCreationStatus.isSuccess)
            groupCreated.invoke()
        else
            Toast.makeText(
                context,
                groupCreationStatus.exceptionOrNull()?.message,
                Toast.LENGTH_SHORT
            ).show()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { result: Uri? ->
        imageUri = result
        showImageUploadBottomSheet = false
    }
    if (!isKeyboardOpen)
        focusManager.clearFocus()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        showImageUploadBottomSheet = false
        if (it)
            imageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                context.resources.getString(R.string.permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            ColoredTopBar(
                modifier = Modifier,
                title = stringResource(id = R.string.new_group),
                color = Color.Gray,
                onBackPressed = {}
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .background(color = Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { focusManager.clearFocus() }
                    )
                }
                .padding(contentPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight.dp / 3)
                    .padding(top = 8.dp)
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(72.dp)
                        .border(
                            color = colorResource(id = R.color.textField_border),
                            width = 2.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .background(color = Color.White)
                        .clickable {
                            showImageUploadBottomSheet = true
                        }
                        .drawWithCache {
                            val width = this.size.width.toInt()
                            val height = this.size.height.toInt()
                            onDrawWithContent {
                                val pictureCanvas =
                                    androidx.compose.ui.graphics.Canvas(
                                        picture.beginRecording(
                                            width,
                                            height
                                        )
                                    )
                                draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                    this@onDrawWithContent.drawContent()
                                }
                                picture.endRecording()

                                drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                            }
                        },
                    model = imageUri,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                ) {
                    it.load(imageUri)
                        .placeholder(R.drawable.group_default_icon)
                        .error(R.drawable.group_default_icon)
                }
                Box(modifier = Modifier
                    .padding(start = 68.dp, top = 36.dp)
                    .size(18.dp)
                    .background(
                        color = colorResource(id = R.color.loginText),
                        shape = CircleShape
                    )
                    .align(Alignment.Center)
                    .clickable {
                        showImageUploadBottomSheet = true
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = ""
                    )
                }
            }
            Column(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.textField_border),
                        focusedBorderColor = colorResource(
                            id = if (name.isNotEmpty())
                                R.color.loginText else
                                R.color.red
                        )
                    ),
                    shape = RoundedCornerShape(12.dp),
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(stringResource(id = R.string.group_name))
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.textField_border),
                        focusedBorderColor = colorResource(
                            id = if (budget.isNotEmpty())
                                R.color.loginText else
                                R.color.red
                        )
                    ),
                    shape = RoundedCornerShape(12.dp),
                    value = budget,
                    onValueChange = {
                        budget =
                            if (Utils.lengthHint(it.length, Utils.PRICE_LIMIT) >= 0) it else budget
                    },
                    label = {
                        Text(stringResource(id = R.string.group_budget))
                    },
                    trailingIcon = {
                        if (budget.isNotEmpty() && isKeyboardOpen)
                            Text(
                                text = Utils.lengthHint(budget.length, Utils.PRICE_LIMIT).toString()
                            )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 6.dp),
                    text = stringResource(id = R.string.budget_editable_info),
                    color = colorResource(id = R.color.or_with_color),
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Row {
                        Text(
                            text = stringResource(id = R.string.safe_to_spend_limit),
                            fontSize = 13.sp
                        )
                        Image(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    showInfo = true
                                },
                            painter = painterResource(id = R.drawable.info_image),
                            contentDescription = stringResource(id = R.string.info)
                        )
                    }
                    Text(
                        color = colorResource(id = R.color.or_with_color),
                        text = "${sliderPosition.toInt()}${stringResource(id = R.string.trailing_text_spend_limit)}",
                        fontSize = 13.sp
                    )
                }
                Slider(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it.toInt().toFloat() },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors().copy(
                        thumbColor = colorResource(id = R.color.loginText),
                        activeTrackColor = colorResource(id = R.color.loginText)
                    )
                )
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                    text = stringResource(id = R.string.safe_to_spend_recommended),
                    color = colorResource(id = R.color.or_with_color),
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp),
                    textRes = R.string.general_continue,
                    onClick = {
                        showLoader = true
                        val byteArray = if (imageUri != null) createBitmapFromPicture(picture) else null
                        viewModel.newGroup(
                            name = name,
                            path = imageUri.toString(),
                            monthlyBudget = budget.toDouble(),
                            safeToSpendLimit = sliderPosition.toInt(),
                            byteArray = byteArray
                        )
                    },
                    enabled = budget.isNotEmpty() && name.isNotEmpty()
                )
            }
        }

        FullScreenProgressIndicator(show = showLoader)
        val texts = if (imageUri != null && !imageUri!!.path.isNullOrBlank())
            listOf(
                stringResource(id = R.string.camera),
                stringResource(id = R.string.gallery),
                stringResource(id = R.string.delete)
            )
        else
            listOf(stringResource(id = R.string.camera), stringResource(id = R.string.gallery))
        val icons = if (imageUri != null)
            listOf(R.drawable.camera_icon, R.drawable.gallery_icon, R.drawable.delete_icon)
        else
            listOf(R.drawable.camera_icon, R.drawable.gallery_icon)

        BottomDrawerForImageUpload(
            modifier = Modifier,
            texts = texts,
            icons = icons,
            showSheet = showImageUploadBottomSheet,
            onCLose = { showImageUploadBottomSheet = false }) {
            when (it) {
                0 -> {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }

                1 -> galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                2 -> {
                    imageUri = null
                    showImageUploadBottomSheet = false
                }
            }
        }
        BottomDrawerForInfo(
            text = stringResource(id = R.string.safe_to_spend_info),
            showSheet = showInfo
        ) {
            showInfo = false
        }
    }
}

@Preview
@Composable
fun PreviewAddNewGroupScreen() {
    AddNewGroupScreen{}
}