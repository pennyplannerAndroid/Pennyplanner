package com.penny.planner.helpers

import android.content.Context
import java.io.File
import java.text.DateFormat.getDateTimeInstance

fun Context.createImageFile(): File {
    val timeStamp = getDateTimeInstance()
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
    return image
}
