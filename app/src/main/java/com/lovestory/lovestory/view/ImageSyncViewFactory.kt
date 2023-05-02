package com.lovestory.lovestory.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageSyncViewFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageSyncView() as T
    }
}