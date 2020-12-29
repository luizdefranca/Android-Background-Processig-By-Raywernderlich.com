package com.raywenderlich.android.kotlincoroutinesfundamentals

import android.content.Context
import android.graphics.BitmapFactory
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class SepiaFilterWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        val imagePath =  inputData.getString("image_path") ?: return Result.failure()
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val newBitmap = ImageUtils


        return Result.success()
    }
}