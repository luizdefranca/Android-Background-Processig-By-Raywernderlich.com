package com.raywenderlich.android.rwdc2018.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import com.raywenderlich.android.rwdc2018.app.PhotosUtils

class PhotoJobService : JobService() {

    companion object {
        private const val TAG = "PhotoJobService"
    }


    override fun onStartJob(params: JobParameters?): Boolean {
        val runnable = Runnable {
            val needsReschedule : Boolean = try {
                PhotosUtils.fetchJsonString() == null
            } catch (e: InterruptedException) {
                Log.e(TAG, "error running job: ${e.message}")
                true
            }
            Log.i(TAG, "Job finished: ${params?.jobId}, needsReschedule = $needsReschedule")

            jobFinished(params, needsReschedule)
        }
        Thread(runnable).start()
       return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(TAG, "Jog stopped: ${params?.jobId}")
        return false
    }


}