package com.lovestory.lovestory.event

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.lovestory.lovestory.module.kakaoLogout
import com.lovestory.lovestory.network.deleteCouple

fun kakaoLogoutEvent(appKey: String, context: Context){
    kakaoLogout(appKey, context)
}
