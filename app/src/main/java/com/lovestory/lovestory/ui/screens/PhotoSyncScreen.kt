package com.lovestory.lovestory.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.PhotoForSyncDao
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.module.uploadPhoto
import com.lovestory.lovestory.ui.components.CheckableDisplayImageFromUri
import com.lovestory.lovestory.ui.components.DeletPhotoDialog
import com.lovestory.lovestory.ui.components.DropDownIcon
import com.lovestory.lovestory.view.PhotoForSyncView
import kotlinx.coroutines.*

@Composable
fun PhotoSyncScreen(navHostController: NavHostController, photoForSyncView: PhotoForSyncView){
    var isDropMenuForRemovePhoto = remember {mutableStateOf(false)}
    val showDeletePhotoDialog = remember { mutableStateOf(false) }
    val showUploadPhotoDialog = remember { mutableStateOf(false) }

    val notSyncedPhotos by photoForSyncView.listOfPhotoForSync.observeAsState(initial = listOf())
//    var isUploadPhotos by remember {
//        mutableStateOfgalleryView.isUploadPhotos)
//    }

    var isUploadPhotos = photoForSyncView.isUploadPhotos
    var currentUploadPhotos = photoForSyncView.currentUploadPhotos
    var totalUploadPhotos = photoForSyncView.totalUploadPhotos

    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()

    var checkPhotoList = remember {
        mutableStateOf(MutableList<Boolean>(notSyncedPhotos.size) { true })
    }

    val onDismissRequest : () -> Unit = {isUploadPhotos = false}

    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)
    val photoForSyncDao : PhotoForSyncDao = photoDatabase.photoForSyncDao()
    val photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)

    LaunchedEffect(key1 = notSyncedPhotos.size) {
        if (notSyncedPhotos.size > checkPhotoList.value.size) {
            val newSize = notSyncedPhotos.size
            val oldSize = checkPhotoList.value.size
            val newCheckPhotoList = checkPhotoList.value.toMutableList()

            for (i in oldSize until newSize) {
                newCheckPhotoList.add(true)
            }

            checkPhotoList.value = newCheckPhotoList
        }
    }

    LaunchedEffect(key1 = photoForSyncView.isUploadPhotos){
        isUploadPhotos = photoForSyncView.isUploadPhotos
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    val onChangeChecked: (Int) -> Unit = { index ->
        checkPhotoList.value = checkPhotoList.value.toMutableList().also {
            it[index] = !it[index]
        }
    }


    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ){
        if (showDeletePhotoDialog.value) {
            DeletPhotoDialog(
                showDeletePhotoDialog = showDeletePhotoDialog,
                notSyncedPhotos = notSyncedPhotos,
                checkPhotoList = checkPhotoList,
                photoForSyncRepository = photoForSyncRepository,
                context = context,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 65.dp, bottom = 10.dp)
                .fillMaxSize(),
        ) {
            items(notSyncedPhotos.size) { index ->
                if (index < notSyncedPhotos.size && index < checkPhotoList.value.size){
                    CheckableDisplayImageFromUri(
                        index = index,
                        checked = checkPhotoList.value[index],
                        imageInfo = notSyncedPhotos[index],
                        onChangeChecked = onChangeChecked,
                        navHostController = navHostController
                    )
                }
            }
        }


        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF3F3F3))
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 20.dp)
            ){
                Row() {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier.clickable {navHostController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "사진 업로드",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropDownIcon(
                    isDropMenuForRemovePhoto = isDropMenuForRemovePhoto,
                    showDeletePhotoDialog = showDeletePhotoDialog,
                    notSyncedPhotos = notSyncedPhotos,
                    checkPhotoList = checkPhotoList,
                    context = context
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF3F3F3))
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 20.dp)
            ){
                TextButton(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(10.dp),
                    onClick = {
                        val sendPhotos = getListOfCheckedPhoto(notSyncedPhotos, checkPhotoList)
                        if (sendPhotos.isNotEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                uploadPhoto(context, sendPhotos, photoForSyncView)
                                withContext(Dispatchers.Main){
                                    Toast.makeText(context, "${sendPhotos.size}개의 사진을 업로드 했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast
                                .makeText(context, "선택된 사진이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ) {
                    Text(
                        text="선택 사진 업로드",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

fun getListOfCheckedPhoto (allPhotos : List<PhotoForSync>, checkPhotoList : MutableState<MutableList<Boolean>>) : List<PhotoForSync>{
    var listOfCheck =  mutableListOf<PhotoForSync>()

    for(current in allPhotos.indices){
        if(checkPhotoList.value[current]){
            listOfCheck.add(allPhotos[current])
        }
    }
    return listOfCheck
}

fun getListOfNotCheckedPhoto (allPhotos : List<PhotoForSync>, checkPhotoList : MutableState<MutableList<Boolean>>):List<PhotoForSync>{
    var listOfCheck =  mutableListOf<PhotoForSync>()

    for(current in allPhotos.indices){
        if(!checkPhotoList.value[current]){
            listOfCheck.add(allPhotos[current])
        }
    }
    return listOfCheck
}