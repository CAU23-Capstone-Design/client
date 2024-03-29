package com.lovestory.lovestory.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.lovestory.lovestory.database.entities.AdditionalPhoto
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.PhotoForSyncDao
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.module.photo.addPhotoFromGallery
import com.lovestory.lovestory.module.photo.uploadPhoto
import com.lovestory.lovestory.module.photo.uploadPhotoFromGallery
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.view.PhotoForSyncView
import kotlinx.coroutines.*

@Composable
fun PhotoSyncScreen(navHostController: NavHostController, photoForSyncView: PhotoForSyncView){
    val notSyncedPhotos by photoForSyncView.listOfPhotoForSync.observeAsState(initial = listOf())
    val additionalNotSync by photoForSyncView.listOfAdditionPhotosForSync.observeAsState(initial = listOf())

    var isDropMenuForRemovePhoto = remember {mutableStateOf(false)}
    val showDeletePhotoDialog = remember { mutableStateOf(false) }

    val numOfCurrentUploadedPhoto = remember { mutableStateOf(0) }
    val numOfTotalUploadPhoto = remember { mutableStateOf(0) }
    val showUploadPhotoDialog = remember { mutableStateOf(false) }
    val onDismissRequest : () -> Unit = {showUploadPhotoDialog.value = false}

    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()

    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)
    val photoForSyncDao : PhotoForSyncDao = photoDatabase.photoForSyncDao()
    val photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)

    val additionalPhotoDao = photoDatabase.additionalPhotoDao()
    val additionalPhotoRepository = AdditionalPhotoRepository(additionalPhotoDao)

    LaunchedEffect(key1 = notSyncedPhotos.size) {
        photoForSyncView.checkPhotoList.value = MutableList<Boolean>(notSyncedPhotos.size) { true }

        if (notSyncedPhotos.size > photoForSyncView.checkPhotoList.value.size) {
            val newSize = notSyncedPhotos.size
            val oldSize = photoForSyncView.checkPhotoList.value.size
            val newCheckPhotoList = photoForSyncView.checkPhotoList.value.toMutableList()

            for (i in oldSize until newSize) {
                newCheckPhotoList.add(true)
            }

            photoForSyncView.checkPhotoList.value = newCheckPhotoList
        }
    }

    LaunchedEffect(key1 = additionalNotSync.size){
        photoForSyncView.checkPhotoFromGalleryList.value = MutableList<Boolean>(additionalNotSync.size){true}

        if(additionalNotSync.size > photoForSyncView.checkPhotoFromGalleryList.value.size){
            val newSize = additionalNotSync.size
            val oldSize = photoForSyncView.checkPhotoFromGalleryList.value.size
            val newCheckPhotoList = photoForSyncView.checkPhotoFromGalleryList.value.toMutableList()

            for(i in oldSize until newSize){
                newCheckPhotoList.add(true)
            }

            photoForSyncView.checkPhotoFromGalleryList.value = newCheckPhotoList
        }
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    val onChangeChecked: (Int) -> Unit = { index ->
        photoForSyncView.checkPhotoList.value = photoForSyncView.checkPhotoList.value.toMutableList().also {
            it[index] = !it[index]
        }
    }

    val onChangeCheckedGalley : (Int) -> Unit = {index ->
        photoForSyncView.checkPhotoFromGalleryList.value = photoForSyncView.checkPhotoFromGalleryList.value.toMutableList().also{
            it[index] = !it[index]
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(30)){uri ->
        CoroutineScope(Dispatchers.IO).launch{
            addPhotoFromGallery(uri, context)
        }
    }


    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ){
        AnimatedVisibility (visible = showDeletePhotoDialog.value, enter = fadeIn(), exit = fadeOut()) {
            DialogWithConfirmAndCancelButton(
                showDialog = showDeletePhotoDialog,
                title = "사진을 삭제하시겠습니까?",
                text = "사진 업로드 리스트에서 제외됩니다. 기기에 저장된 사진은 삭제되지 않습니다.",
                confirmText = "삭제",
            ) {
                showDeletePhotoDialog.value = false
                val deleteFromLoveStory =  getListOfNotCheckedPhoto(notSyncedPhotos, photoForSyncView.checkPhotoList.value)
                val deleteFromGallery = getListOfNotCheckedPhotoFromGallery(additionalNotSync, photoForSyncView.checkPhotoFromGalleryList.value,)

                var countDeleteItem = 0
                if(deleteFromLoveStory.isNotEmpty()){
                    for(item in deleteFromLoveStory){
                        photoForSyncRepository.deletePhotoForSync(item)
                    }
                    countDeleteItem += deleteFromLoveStory.size
                }
                if(deleteFromGallery.isNotEmpty()){
                    for(item in deleteFromGallery){
                        additionalPhotoRepository.deleteAdditionalPhoto(item)
                    }
                    countDeleteItem += deleteFromGallery.size
                }

                Toast.makeText(context, "${countDeleteItem}개의 사진을 삭제했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        AnimatedVisibility(visible = showUploadPhotoDialog.value, enter = fadeIn(), exit = fadeOut()){
            ProgressbarInDialog(
                onDismissRequest = onDismissRequest,
                numOfCurrentUploadedPhoto = numOfCurrentUploadedPhoto,
                numOfTotalUploadPhoto = numOfTotalUploadPhoto,
                titleForWork = "사진 업로드 중"
            )
        }

        SectionOfPhotoListLayout(
            notSyncedPhotos = notSyncedPhotos,
            additionalNotSync = additionalNotSync,
            photoLauncher = photoLauncher,
            photoForSyncView = photoForSyncView,
            onChangeChecked = onChangeChecked,
            onChangeCheckedGalley = onChangeCheckedGalley,
            navHostController = navHostController,
        )

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ){
            ScreenHeaderWithDropDown(
                navHostController = navHostController,
                headerTitle = "사진 업로드",
            ){
                DropDownIcon(
                    isDropMenuForRemovePhoto = isDropMenuForRemovePhoto,
                    showDeletePhotoDialog = showDeletePhotoDialog,
                    notSyncedPhotos = notSyncedPhotos,
                    checkPhotoList = photoForSyncView.checkPhotoList.value,
                    additionalPhotos = additionalNotSync,
                    checkPhotoFromGalleryList = photoForSyncView.checkPhotoFromGalleryList.value,
                    context = context,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            SectionOfUploadButton(
                context = context,
                notSyncedPhotos = notSyncedPhotos,
                additionalNotSync = additionalNotSync,
                photoForSyncView = photoForSyncView,
                navHostController = navHostController,
                numOfTotalUploadPhoto = numOfTotalUploadPhoto,
                showUploadPhotoDialog = showUploadPhotoDialog,
                numOfCurrentUploadedPhoto = numOfCurrentUploadedPhoto,
            )
        }
    }
}

@Composable
fun SectionOfPhotoListLayout(
    notSyncedPhotos :List<PhotoForSync>,
    additionalNotSync : List<AdditionalPhoto>,
    photoLauncher : ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    photoForSyncView : PhotoForSyncView,
    onChangeChecked : (Int) -> Unit,
    onChangeCheckedGalley : (Int) -> Unit,
    navHostController : NavHostController,
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(top = 65.dp, bottom = 65.dp)
    ) {
        mapOf<String, List<Any>>( "camera" to listOf(notSyncedPhotos)  , "gallery" to listOf(additionalNotSync)).forEach {
            item (
                span = {
                    GridItemSpan(
                        maxLineSpan
                    )
                }

            ){
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 15.dp),
//                        verticalArrangement = Arrangement.Center
                ){
                    if(it.key =="camera"){
                        Text(text = "카메라로 찍은 사진", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 5.dp))
                        AnimatedVisibility(visible = notSyncedPhotos.isEmpty(),enter = fadeIn(), exit = fadeOut()){
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "연인과 만났을 때 찍은 사진이 표시 됩니다.",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }else if(it.key == "gallery"){
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        Text(text = "갤러리에서 선택한 사진", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 5.dp))
                        AnimatedVisibility(visible = additionalNotSync.isEmpty(),enter = fadeIn(), exit = fadeOut()){
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ){
                                Text(
                                    text = "사진이 없습니다. 갤러리에서 사진을 골라보세요!",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .border(2.dp, Color.Black, CircleShape)
                                        .clip(shape = CircleShape)
                                        .clickable {
                                            photoLauncher.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        }
                                        .clip(CircleShape),

                                    ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ){
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_gallery),
                                            contentDescription = "icon",
                                            tint = Color.Black
                                        )
                                        Text(
                                            text="갤러리 열기",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }

            if(it.key =="camera"){
                items(notSyncedPhotos.size) { index ->
                    if (index < notSyncedPhotos.size && index < photoForSyncView.checkPhotoList.value.size){
                        CheckableDisplayImageFromUri(
                            index = index,
                            checked = photoForSyncView.checkPhotoList.value[index],
                            imageInfo = notSyncedPhotos[index],
                            onChangeChecked = onChangeChecked,
                            navHostController = navHostController
                        )
                    }
                }
            }

            else if(it.key == "gallery"){
                items(additionalNotSync.size){ index ->
                    if(index < additionalNotSync.size && index < photoForSyncView.checkPhotoFromGalleryList.value.size){
                        CheckableDisplayImageFromUriWithPicker(
                            navHostController = navHostController,
                            index = index,
                            checked = photoForSyncView.checkPhotoFromGalleryList.value[index],
                            imageInfo = additionalNotSync[index],
                            onChangeChecked = onChangeCheckedGalley
                        )
                    }


                }
            }
        }
    }
}

@Composable
fun SectionOfUploadButton(
    context : Context,
    notSyncedPhotos : List<PhotoForSync>,
    additionalNotSync : List<AdditionalPhoto>,
    photoForSyncView : PhotoForSyncView,
    navHostController : NavHostController,
    numOfTotalUploadPhoto : MutableState<Int>,
    showUploadPhotoDialog : MutableState<Boolean>,
    numOfCurrentUploadedPhoto : MutableState<Int>,

){
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
                val sendPhotosFromCamera = getListOfCheckedPhoto(
                    allPhotos = notSyncedPhotos,
                    checkPhotoList =  photoForSyncView.checkPhotoList.value,
                )
                val sendPhotosFromGallery = getListOfCheckedPhotoFromGallery(
                    allPhotosFromGallery = additionalNotSync,
                    checkPhotoListFromGallery = photoForSyncView.checkPhotoFromGalleryList.value
                )
                if (sendPhotosFromCamera.isNotEmpty() || sendPhotosFromGallery.isNotEmpty()) {
                    if(sendPhotosFromCamera.isNotEmpty() && sendPhotosFromGallery.isNotEmpty()){
                        numOfTotalUploadPhoto.value = sendPhotosFromCamera.size + sendPhotosFromGallery.size
                        showUploadPhotoDialog.value = true
                        CoroutineScope(Dispatchers.IO).launch {
                            uploadPhoto(
                                context = context,
                                sendPhotos = sendPhotosFromCamera,
                                numOfCurrentUploadedPhoto =  numOfCurrentUploadedPhoto,
                            )

                            uploadPhotoFromGallery(
                                context = context,
                                sendPhotosFromGallery = sendPhotosFromGallery,
                                numOfCurrentUploadedPhoto = numOfCurrentUploadedPhoto,
                            )

                            withContext(Dispatchers.Main){
                                showUploadPhotoDialog.value = false
                                Toast.makeText(context, "${numOfTotalUploadPhoto.value}개의 사진을 업로드 했습니다.", Toast.LENGTH_SHORT).show()


                                Log.d("PhotoSyncScreen", "notSyncedPhotos.value) :  ${notSyncedPhotos.size}")
                                Log.d("PhotoSyncScreen", "notSyncedPhotos.value) :  ${additionalNotSync.size}")
                                numOfCurrentUploadedPhoto.value = 0
                                if(notSyncedPhotos.size == 1 && additionalNotSync.size == 1){
                                    navHostController.popBackStack()
                                }
                            }
                        }
                    }
                    else if(sendPhotosFromCamera.isNotEmpty()){
                        numOfTotalUploadPhoto.value = sendPhotosFromCamera.size
                        showUploadPhotoDialog.value = true

                        CoroutineScope(Dispatchers.IO).launch {
                            uploadPhoto(
                                context = context,
                                sendPhotos = sendPhotosFromCamera,
                                numOfCurrentUploadedPhoto =  numOfCurrentUploadedPhoto,
                            )

                            withContext(Dispatchers.Main){
                                showUploadPhotoDialog.value = false
                                Toast.makeText(context, "${numOfTotalUploadPhoto.value}개의 사진을 업로드 했습니다.", Toast.LENGTH_SHORT).show()

                                numOfCurrentUploadedPhoto.value = 0
                                if(notSyncedPhotos.size == 1){
                                    navHostController.popBackStack()
                                }
                            }
                        }
                    }
                    else if(sendPhotosFromGallery.isNotEmpty()){
                        numOfTotalUploadPhoto.value = sendPhotosFromGallery.size
                        showUploadPhotoDialog.value = true
                        CoroutineScope(Dispatchers.IO).launch {
                            uploadPhotoFromGallery(
                                context = context,
                                sendPhotosFromGallery = sendPhotosFromGallery,
                                numOfCurrentUploadedPhoto = numOfCurrentUploadedPhoto,
                            )

                            withContext(Dispatchers.Main){
                                showUploadPhotoDialog.value = false
                                Toast.makeText(context, "${numOfTotalUploadPhoto.value}개의 사진을 업로드 했습니다.", Toast.LENGTH_SHORT).show()

                                numOfCurrentUploadedPhoto.value = 0
                                if(additionalNotSync.size == 1){
                                    navHostController.popBackStack()
                                }
                            }
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

fun getListOfCheckedPhoto (
    allPhotos : List<PhotoForSync>,
    checkPhotoList : List<Boolean>,
) : List<PhotoForSync>{
    var listOfCheck =  mutableListOf<PhotoForSync>()

    for(current in allPhotos.indices){
        if(checkPhotoList[current]){
            listOfCheck.add(allPhotos[current])
        }
    }

    return listOfCheck
}

fun getListOfCheckedPhotoFromGallery(
    allPhotosFromGallery : List<AdditionalPhoto>,
    checkPhotoListFromGallery : List<Boolean>
):List<AdditionalPhoto>{
    var listOfCheck =  mutableListOf<AdditionalPhoto>()

    for(current in allPhotosFromGallery.indices){
        if(checkPhotoListFromGallery[current]){
            listOfCheck.add(allPhotosFromGallery[current])
        }
    }

    return listOfCheck
}

fun getListOfNotCheckedPhoto (allPhotos : List<PhotoForSync>, checkPhotoList : List<Boolean>):List<PhotoForSync>{
    var listOfCheck =  mutableListOf<PhotoForSync>()

    for(current in allPhotos.indices){
        if(!checkPhotoList[current]){
            listOfCheck.add(allPhotos[current])
        }
    }
    return listOfCheck
}

fun getListOfNotCheckedPhotoFromGallery(allPhotoFromGallery : List<AdditionalPhoto>, checkPhotoFromGalleryList : List<Boolean>):List<AdditionalPhoto>{
    var listOfCheck = mutableListOf<AdditionalPhoto>()

    for(current in allPhotoFromGallery.indices){
        if(!checkPhotoFromGalleryList[current]){
            listOfCheck.add(allPhotoFromGallery[current])
        }
    }

    return listOfCheck
}