/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.raywenderlich.android.rwdc2018.repository

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.AsyncTask
import android.util.Log
import com.raywenderlich.android.rwdc2018.app.PhotosUtils
import com.raywenderlich.android.rwdc2018.app.RWDC2018Application
import com.raywenderlich.android.rwdc2018.services.LogJobService
import com.raywenderlich.android.rwdc2018.services.PhotoJobService
import java.security.AccessController.getContext


class PhotosRepository : Repository {
  private val photosLiveData = MutableLiveData<List<String>>()
  private val bannerLiveData = MutableLiveData<String>()

  private val TAG = this.javaClass.simpleName
  private val PHOTOS_KEY = "PHOTOS_KEY"

    init {
        scheduleLogJobService()
        scheduleFetchJob()
    }
  override fun getPhotos(): LiveData<List<String>> {
    // cause we are going to use the FetchPhotoAsyncTask we are no loger using the FetchPhoto Method
    // Instead we are going to call using the FetchPhotoAsyncTask
    FetchPhotosAsyncTask({photos ->
      photosLiveData.value = photos

    }).execute()
   // FetchPhotos()
    return photosLiveData
  }

  private fun FetchPhotos() {

    //Instead to use a handler and a looper to update  values on main thread
    //we can use photosLiveData.postValue() method
    /*
    val handler = object: Handler(Looper.getMainLooper()) {
      override fun handleMessage(message: Message?){
        val bundle = message?.data
        val photos = bundle?.getStringArrayList(PHOTOS_KEY)
        photosLiveData.value = photos
      }
    }
     */
    val runnable = Runnable {
      val photoString = PhotosUtils.photoJsonString()
      Log.i(TAG, TAG + "  -  " + photoString)
  //      Log.i("PhotosRepository", photoString)
      val photos =  PhotosUtils.photoUrlsFromJsonString(photoString ?: "")

      if (photos != null) {
     /*   val message = Message()
        val bundle = Bundle()
        bundle.putStringArrayList(PHOTOS_KEY, photos)
        message.data = bundle
        handler.sendMessage(message)
      */

       photosLiveData.postValue(photos)
      }
    }


    val thread = Thread(runnable)
    thread.start()
  }

    //JobService Methods

    private fun scheduleFetchJob(){
        val jobScheduler = RWDC2018Application.getAppContext()
                .getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//        val jobInfo = JobInfo.Builder(1000,
//        ComponentName(Application().applicationContext, PhotoJobService::class.java))
        val componentName = ComponentName(RWDC2018Application.getAppContext(), PhotoJobService::class.java)


        val jobInfo = JobInfo.Builder(1000, componentName)
                .setPeriodic(900000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()

        jobScheduler.schedule(jobInfo)

    }

    private fun scheduleLogJobService (){
        val jobScheduler = RWDC2018Application.getAppContext()
                .getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(RWDC2018Application.getAppContext(), LogJobService::class.java)
        val jobInfo = JobInfo.Builder(1001, componentName)
                .setPeriodic(900000)
                .build()
        jobScheduler.schedule(jobInfo)
    }

  /*
  this function do the same thing that FetchPhotos function. however using the AsyncTask objects instead.

   */

 fun fetchBanner(){
    Thread(Runnable {
        val bannerString = PhotosUtils.photoJsonString()
        val banners = PhotosUtils.bannerFromJsonString(bannerString?: "")

      if (banners != null) {
        bannerLiveData.postValue(banners)
      }

      }).start()
  }


  override fun getBanner(): LiveData<String> {
      // Using the FetchBannerAsyncTask instead
//    fetchBanner()

      FetchBannerAsyncTask({banner ->
          bannerLiveData.value = banner
      }).execute()
    return bannerLiveData
  }


    // *******   AsyncTask classes *******

  private class FetchPhotosAsyncTask(val callback: (List<String>) -> Unit)
    : AsyncTask<Void, Void, List<String>>() {

    override fun doInBackground(vararg params: Void?): List<String> ?{
      val photoString = PhotosUtils.photoJsonString()
      val photos = PhotosUtils.photoUrlsFromJsonString(photoString?: "")
      return photos
    }

    override fun onPostExecute(result: List<String>?) {
//      super.onPostExecute(result)
      if (result != null){
        callback(result)
      }
    }
  }

  private class FetchBannerAsyncTask(val callback: (String) -> Unit)
      : AsyncTask<Void, Void, String>(){
      override fun doInBackground(vararg params: Void?): String? {
          val bannerString = PhotosUtils.photoJsonString()
          return PhotosUtils.bannerFromJsonString(bannerString?: "")
      }

      override fun onPostExecute(result: String?) {
          if (result != null) {
              callback(result)
          }
      }

  }

}