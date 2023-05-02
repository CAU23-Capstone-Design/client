package com.lovestory.lovestory.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PhotoForSyncViewFactory(private val application: Application) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotoForSyncView(application) as T
    }
}