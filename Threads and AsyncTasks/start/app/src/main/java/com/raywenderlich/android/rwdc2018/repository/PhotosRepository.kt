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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.raywenderlich.android.rwdc2018.app.PhotosUtils


class PhotosRepository : Repository {
  private val photosLiveData = MutableLiveData<List<String>>()
  private val bannerLiveData = MutableLiveData<String>()

  private val TAG = this.javaClass.simpleName
  private val PHOTOS_KEY = "PHOTOS_KEY"
  override fun getPhotos(): LiveData<List<String>> {

    FetchJsonData()
    return photosLiveData
  }

  private fun FetchJsonData() {

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

  override fun getBanner(): LiveData<String> {
    return bannerLiveData
  }
}