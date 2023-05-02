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
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
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
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.module.uploadPhoto
import com.lovestory.lovestory.ui.components.CheckableDisplayImageFromUri
import com.lovestory.lovestory.view.PhotoForSyncView
import kotlinx.coroutines.*

@Composable
fun PhotoSyncScreen(navHostController: NavHostController, photoForSyncView: PhotoForSyncView){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val paddingWidth = ((screenWidth-(screenWidth / 3 - 10.dp)*3) - 11.dp)/2.dp

    val notSyncedPhotos by photoForSyncView.listOfPhotoForSync.observeAsState(initial = listOf())
//    var isUploadPhotos by remember {
//        mutableStateOfgalleryView.isUploadPhotos)
//    }

    var isUploadPhotos = photoForSyncView.isUploadPhotos
    var currentUploadPhotos = photoForSyncView.currentUploadPhotos
    var totalUploadPhotos = photoForSyncView.totalUploadPhotos

//    Log.d("SCREEN-PhotoSyncScreen", "$isUploadPhotos, $currentUploadPhotos, $totalUploadPhotos")

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
                newCheckPhotoList.add(true)
            }

            checkPhotoList.value = newCheckPhotoList
        }
    }

    LaunchedEffect(key1 = photoForSyncView.isUploadPhotos){
        isUploadPhotos = photoForSyncView.isUploadPhotos
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

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, top = 54.dp, bottom = 10.dp)
                .fillMaxSize(),
        ) {
            items(notSyncedPhotos.size) { index ->
                if (index < notSyncedPhotos.size && index < checkPhotoList.value.size){
                    CheckableDisplayImageFromUri(
                        index = index,
                        checked = checkPhotoList.value[index],
                        imageUri = notSyncedPhotos[index].imageUrl.toString(),
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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF3F3F3))
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 20.dp)
            ){
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
                Box(
                    modifier = Modifier
                        .background(Color.Transparent)
//                        .indication(interactionSource, rememberRipple(bounded = false, radius = 10.dp))
                        .clickable( onClick = {
                            val sendPhotos = getListOfCheckedPhoto(notSyncedPhotos, checkPhotoList)
                            if (sendPhotos.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    uploadPhoto(context, sendPhotos, photoForSyncView)
                                    //                checkExistNeedPhotoForSync(context)
                                }
                            } else {
                                Toast
                                    .makeText(context, "선택된 사진이 없습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                        .padding(10.dp)
                ) {
                    Text(
                        text="선택 사진 업로드",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }




    }
//    Column(
//        modifier = Modifier
//            .background(Color.White)
//            .fillMaxSize()
//            .padding(horizontal = 10.dp, vertical = 10.dp) ,
//        verticalArrangement = Arrangement.SpaceBetween
//    ){}



//        AnimatedVisibility(visible = isUploadPhotos, enter = scaleIn(animationSpec = tween(durationMillis = 300), initialScale = 0.1f)){
//
//
//        }
//        if(isUploadPhotos){
//            Dialog(
//                onDismissRequest = onDismissRequest,
//                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
//            ){
//                Column(
//                    modifier = Modifier
//                        .width(360.dp)
//                        .wrapContentHeight()
//                        .clip(RoundedCornerShape(25.dp))
//                        .background(color = Color.White),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ){
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text(text = "사진 업로드 중 $currentUploadPhotos of $totalUploadPhotos")
//                    LinearProgressIndicator(progress = currentUploadPhotos.toFloat()/totalUploadPhotos.toFloat())
//                    Spacer(modifier = Modifier.height(20.dp))
//                }
//            }
//        }

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