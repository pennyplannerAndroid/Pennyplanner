package com.penny.planner.data.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File

@HiltWorker
class ImageDownloadWorker(
    @ApplicationContext private val appContext: Context,
    params : WorkerParameters
) : CoroutineWorker(appContext, params){

    override suspend fun doWork(): Result {
        val firebaseImagePath = inputData.getString("firebaseImagePath") ?: return Result.failure()
        val imageId = inputData.getString("imageId") ?: return Result.failure()

        return try {
            val storageRef = FirebaseStorage.getInstance().reference.child(firebaseImagePath).child(imageId)
            val localFile = File(applicationContext.filesDir, "${imageId}.jpeg")

            // Synchronous download with a blocking method
            val task = storageRef.getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) {
                    println("Image downloaded to: ${localFile.absolutePath}")
                } else {
                    it.exception?.printStackTrace()
                }
            }

            // Block until the task completes
            task.await()

            if (task.isSuccessful) {
                val output = Data.Builder()
                    .putString("resultKey", localFile.absolutePath)
                    .build()
                Result.success(output)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

}