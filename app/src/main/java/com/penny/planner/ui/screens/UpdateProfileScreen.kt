package com.penny.planner.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.R
import com.penny.planner.helpers.Utils
import com.penny.planner.data.repositories.OnboardingRepositoryImpl
import com.penny.planner.helpers.Utils.Navigation.createBitmapFromPicture
import com.penny.planner.helpers.createImageFile
import com.penny.planner.ui.components.BottomDrawer
import com.penny.planner.ui.components.FullScreenProgressIndicator
import com.penny.planner.ui.components.PrimaryButton
import com.penny.planner.viewmodels.OnboardingViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DateFormat.getDateTimeInstance
import java.util.Objects

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UpdateProfileScreen (
    viewModel: OnboardingViewModel,
    buttonClicked : () -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val picture = remember { Picture() }
    var showLoader by remember {
        mutableStateOf(false)
    }
    val headerBoxHeight = LocalConfiguration.current.screenHeightDp / 3
    val context = LocalContext.current
    val view = LocalView.current

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        Utils.PROVIDER, file
    )

    val state = viewModel.profileUpdateResult.observeAsState().value
    if (state != null) {
        showLoader = false
        if (state.isSuccess)
            buttonClicked.invoke()
        else if (state.isFailure)
            Toast.makeText(
                context,
                state.exceptionOrNull()?.message ?: stringResource(id = R.string.operation_failed),
                Toast.LENGTH_LONG
            ).show()
    }

    SideEffect {
        val window = (context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { result : Uri? ->
        imageUri = result
        showBottomSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        imageUri = uri
        showBottomSheet = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, context.resources.getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, context.resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(headerBoxHeight.dp)
                .clip(CustomShape())
                .background(
                    colorResource(id = R.color.loginText)
                )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp),
                text = stringResource(id = R.string.profile_update_header),
                fontSize = 42.sp,
                color = Color.White,
                lineHeight = 42.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
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
                        showBottomSheet = true
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
                    .placeholder(R.drawable.default_user_display)
                    .error(R.drawable.default_user_display)
            }
            Image(
                modifier = Modifier
                    .padding(start = 68.dp, top = 36.dp)
                    .size(24.dp)
                    .background(color = colorResource(id = R.color.loginText), shape = CircleShape)
                    .size(18.dp)
                    .align(Alignment.Center)
                    .clickable {
                        showBottomSheet = true
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
            textRes = R.string.get_started,
            onClick = {
                showLoader = true
                val byteArray = if (imageUri != null) createBitmapFromPicture(picture) else null
                viewModel.updateProfile(name, byteArray)
            },
            enabled = true
        )
    }
    FullScreenProgressIndicator(show = showLoader)
    val texts = if (imageUri != null)
        listOf(
            stringResource(id = R.string.gallery),
            stringResource(id = R.string.camera),
            stringResource(id = R.string.delete)
        )
    else
        listOf(stringResource(id = R.string.gallery), stringResource(id = R.string.camera))
    val icons = if (imageUri != null)
        listOf(R.drawable.gallery_icon, R.drawable.camera_icon, R.drawable.delete_icon)
    else
        listOf(R.drawable.gallery_icon, R.drawable.camera_icon)

    BottomDrawer(modifier = Modifier, texts = texts, icons = icons, showSheet = showBottomSheet, onCLose = { showBottomSheet = false }) {
        when(it) {
            0 -> galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            1 -> {
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
            2 -> imageUri = null
        }
    }
}

class CustomShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            lineTo(0f, size.height)
            lineTo(size.width, (size.height * 0.75).toFloat())
            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Preview
@Composable
fun UpdateProfile() {
    UpdateProfileScreen(OnboardingViewModel(OnboardingRepositoryImpl(FirebaseAuth.getInstance()))) {}
}