package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.module.uploadPhoto
import com.lovestory.lovestory.ui.components.CheckableDisplayImageFromUri
import com.lovestory.lovestory.view.PhotoViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PhotoSyncScreen(navHostController: NavHostController, viewModel: PhotoViewModel){
    val notSyncedPhotos by viewModel.notSyncedPhotos.observeAsState(initial = listOf())
//    var isUploadPhotos by remember {
//        mutableStateOf(viewModel.isUploadPhotos)
//    }

    var isUploadPhotos = viewModel.isUploadPhotos
    var currentUploadPhotos = viewModel.currentUploadPhotos
    var totalUploadPhotos = viewModel.totalUploadPhotos

    Log.d("SCREEN-PhotoSyncScreen", "$isUploadPhotos, $currentUploadPhotos, $totalUploadPhotos")

    val context = LocalContext.current

    var checkPhotoList = remember {
        mutableStateOf(MutableList<Boolean>(notSyncedPhotos.size) { true })
    }

    val onDismissRequest : () -> Unit = {isUploadPhotos = false}

    LaunchedEffect(key1 = notSyncedPhotos.size) {
        if (notSyncedPhotos.size > checkPhotoList.value.size) {
            val newSize = notSyncedPhotos.size
            val oldSize = checkPhotoList.value.size
            val newCheckPhotoList = checkPhotoList.value.toMutableList()

            for (i in oldSize until newSize) {
                newCheckPhotoList.add(false)
            }

            checkPhotoList.value = newCheckPhotoList
        }
    }

    LaunchedEffect(key1 = viewModel.isUploadPhotos){
        isUploadPhotos = viewModel.isUploadPhotos
    }

    val onChangeChecked: (Int) -> Unit = { index ->
        checkPhotoList.value = checkPhotoList.value.toMutableList().also {
            it[index] = !it[index]
        }
    }

    var itemList by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3")) }

    val onItemChanged: (Int, String) -> Unit = { index, newValue ->
        itemList = itemList.toMutableList().also {
            it[index] = newValue
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 10.dp) ,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp)
        ) {
            items(notSyncedPhotos.size) { index ->
                if (index < notSyncedPhotos.size && index < checkPhotoList.value.size){
                    CheckableDisplayImageFromUri(
                        index = index,
                        checked = checkPhotoList.value[index],
                        imageUri = notSyncedPhotos[index].imageUrl.toString(),
                        onChangeChecked = onChangeChecked
                    )
                }
            }
        }

        Button(onClick = {
            val sendPhotos = getListOfCheckedPhoto(notSyncedPhotos, checkPhotoList)
            CoroutineScope(Dispatchers.IO).launch {
                uploadPhoto(context, sendPhotos, viewModel)
                checkExistNeedPhotoForSync(context)
            }
        }) {
            Text("업로드 하기")
        }

//        AnimatedVisibility(visible = isUploadPhotos, enter = scaleIn(animationSpec = tween(durationMillis = 300), initialScale = 0.1f)){
//
//
//        }
        if(isUploadPhotos){
            Dialog(
                onDismissRequest = onDismissRequest,
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            ){
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(25.dp))
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "사진 업로드 중 $currentUploadPhotos of $totalUploadPhotos")
                    LinearProgressIndicator(progress = currentUploadPhotos.toFloat()/totalUploadPhotos.toFloat())
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

fun getListOfCheckedPhoto (allPhotos : List<Photo>, checkPhotoList : MutableState<MutableList<Boolean>>) : List<Photo>{
    var listOfCheck =  mutableListOf<Photo>()

    for(current in allPhotos.indices){
        if(checkPhotoList.value[current]){
            listOfCheck.add(allPhotos[current])
        }
    }
    return listOfCheck
}