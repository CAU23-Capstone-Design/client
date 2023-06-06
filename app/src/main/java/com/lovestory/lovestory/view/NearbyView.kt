package com.lovestory.lovestory.view

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_CHANGE_VALUE_NEARBY
import com.lovestory.lovestory.module.shared.getNearBy
import com.lovestory.lovestory.module.shared.saveNearBy

class NearbyView(application : Application) : ViewModel() {
    var isNearby : MutableLiveData<Boolean> = MutableLiveData(false)


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_CHANGE_VALUE_NEARBY) {

                val isNearby = intent.getBooleanExtra("isNearby", false)
                Log.d("NearbyView", "onReceive: $isNearby")
                saveNearBy(application, isNearby)
                this@NearbyView.isNearby.value = isNearby
            }
        }
    }

    init {
//        getNearBy(application)
        isNearby.value = getNearBy(application)
        val filter = IntentFilter().apply {
            addAction(ACTION_CHANGE_VALUE_NEARBY)
        }

        LocalBroadcastManager.getInstance(application).registerReceiver(broadcastReceiver, filter)



//        val intentFilter = IntentFilter()

//        LocalBroadcastManager.getInstance(application).registerReceiver(
//            broadcastReceiver, IntentFilter(ACTION_CHANGE_VALUE_NEARBY)
//        )
//        intentFilter.addAction(ACTION_CHANGE_VALUE_NEARBY)
//        application.registerReceiver(broadcastReceiver, intentFilter)
        Log.d("NearbyView", "init: ")

    }

    fun registerDataUpdateReceiver(context: Context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            broadcastReceiver, IntentFilter(ACTION_CHANGE_VALUE_NEARBY)
        )
    }

    fun unregisterDataUpdateReceiver(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
    }



}