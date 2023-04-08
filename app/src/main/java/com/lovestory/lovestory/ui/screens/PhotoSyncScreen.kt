package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.module.uploadPhoto
import com.lovestory.lovestory.ui.components.CheckableDisplayImageFromUri
import com.lovestory.lovestory.ui.components.DisplayImageFromUri
import com.lovestory.lovestory.view.PhotoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PhotoSyncScreen(navHostController: NavHostController, viewModel: PhotoViewModel, notSyncedPhotos : List<Photo>){
    val context = LocalContext.current

    var checkPhotoList = remember {
        mutableStateOf(MutableList<Boolean>(notSyncedPhotos.size) { true })
    }

    Log.d("PhtoSyncScreen", "${notSyncedPhotos.size}")

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
                uploadPhoto(context, sendPhotos)
                checkExistNeedPhotoForSync(context)
            }
        }) {
            Text("업로드 하기")
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