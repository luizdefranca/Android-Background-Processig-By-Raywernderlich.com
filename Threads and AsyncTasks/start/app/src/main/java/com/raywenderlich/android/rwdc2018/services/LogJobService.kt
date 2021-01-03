package com.raywenderlich.android.rwdc2018.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
import android.util.Log

class LogJobService : JobService() {

    companion object {
        private const val TAG = "LogJobService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "**********************************\nthe job  has created")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "**********************************\nthe job  has destroyed")
    }
    override fun onStartJob(params: JobParameters?): Boolean {
        Thread(Runnable {
            Thread.sleep(5000)
            jobFinished(params, false)
            Log.i(TAG, "**********************************\nthe job id is: ${params?.jobId.toString()} and has finished")
        }).start()
        Log.i(TAG, "**********************************\nthe job id is: ${params?.jobId.toString()} and has started")
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(TAG, "**********************************\nthe job id is: ${params?.jobId.toString()} and has stoped")
        return false
    }


}