package com.penny.planner.ui.screens

import android.graphics.Picture
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.Utils.Navigation.createBitmapFromPicture
import com.penny.planner.helpers.createImageFile
import com.penny.planner.models.GroupModel
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.PrimaryButton
import kotlinx.coroutines.launch
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AddNewGroupDrawer(
    onClose: () -> Unit,
    showSheet: Boolean,
    onClick: (String, ByteArray?, Uri?) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val picture = remember { Picture() }
    var showLoader by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val view = LocalView.current

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        Utils.PROVIDER, file
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { result : Uri? ->
        imageUri = result
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
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
            onDismissRequest = onClose
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(id = R.string.new_group)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            .clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
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

                                    drawIntoCanvas { canvas ->
                                        canvas.nativeCanvas.drawPicture(
                                            picture
                                        )
                                    }
                                }
                            },
                        model = imageUri,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    ) {
                        it.load(imageUri)
                            .placeholder(R.drawable.default_user_display)
                            .error(R.drawable.default_user_display)
                    }
                    Image(
                        modifier = Modifier
                            .padding(start = 68.dp, top = 36.dp)
                            .size(24.dp)
                            .background(
                                color = colorResource(id = R.color.loginText),
                                shape = CircleShape
                            )
                            .size(18.dp)
                            .align(Alignment.Center)
                            .clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = ""
                    )
                }
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
                        Text(stringResource(id = R.string.name))
                    }
                )
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 8.dp)
                        .size(48.dp),
                    textRes = R.string.new_group,
                    onClick = {
                        showLoader = true
                        val byteArray = if (imageUri != null) createBitmapFromPicture(picture) else null
                        onClick.invoke(name, byteArray, imageUri)
                    },
                    enabled = true
                )
            }
            FullScreenProgressIndicator(show = showLoader)
        }
    }
}

@Preview
@Composable
fun PreviewAddGroupScreen() {
    AddNewGroupDrawer({}, true) { name, imageArray, imageUri ->

    }
}