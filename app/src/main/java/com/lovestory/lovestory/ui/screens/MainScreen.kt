package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.model.UserForLoginPayload
import com.lovestory.lovestory.services.*
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar
import com.lovestory.lovestory.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Screen Navigation
 *
 * @param navHostController 네비게이션 컨트롤러
 * @param userData 사용자 데이터
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navHostController: NavHostController = rememberNavController(),
    userData : UserForLoginPayload = UserForLoginPayload(
        id = "unknown", name = "unknown", birthday = "1900-01-01", gender="M", code = "unknown")
) {
    val context = LocalContext.current

    val owner = LocalViewModelStoreOwner.current
    lateinit var photoForSyncView: PhotoForSyncView
    lateinit var syncedPhotoView: SyncedPhotoView
    lateinit var nearbyView: NearbyView

    owner?.let {
        photoForSyncView = viewModel(
            it,
            "PhotoForSyncViewModel",
            PhotoForSyncViewFactory(LocalContext.current.applicationContext as Application)
        )

        syncedPhotoView = viewModel(
            it,
            "SyncedPhotoViewModel",
            SyncedPhotoViewFactory(LocalContext.current.applicationContext as Application)
        )

        nearbyView = viewModel(
            it,
            "NearbyViewModel",
            NearbyViewFactory(LocalContext.current.applicationContext as Application)
        )
    }

    LaunchedEffect(key1 = null){
        CoroutineScope(Dispatchers.IO).launch {
            val database = PhotoDatabase.getDatabase(context)
            val additionalPhotoDao = database.additionalPhotoDao()
            val additionalPhotoRepository = AdditionalPhotoRepository(additionalPhotoDao)
            additionalPhotoRepository.deleteAllAdditionalPhoto()
        }
    }

    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
        MainNavGraph(
            navHostController = navHostController,
            photoForSyncView = photoForSyncView,
            syncedPhotoView =  syncedPhotoView,
            nearbyView = nearbyView,
            userData = userData
        )
    }
}