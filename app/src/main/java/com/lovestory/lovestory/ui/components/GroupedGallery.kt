package com.lovestory.lovestory.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.view.SyncedPhotoView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun GroupedGallery(
    syncedPhotosByDate: Map<String, List<SyncedPhoto>>,
    token: String?,
    navHostController: NavHostController,
    currentDate: LocalDate,
    allPhotoListState: LazyListState,
    syncedPhotoView : SyncedPhotoView,
    isPressedPhotoMode : MutableState<Boolean>,
    listOfSelectedPhoto :  MutableState<MutableSet<String>>

){

    LazyColumn(
        modifier = Modifier.padding(bottom = 70.dp),
        contentPadding = PaddingValues(top=65.dp, bottom = 75.dp),
        state = allPhotoListState
    ){
        syncedPhotosByDate.forEach{(date, photos)->
            item {
                val photoDate = LocalDate.parse(date)
                val dateFormatter = if (currentDate.year == photoDate.year) {
                    DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.getDefault())
                } else {
                    DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.getDefault())
                }

                val formattedDate = photoDate.format(dateFormatter)

                Text(
                    text = formattedDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 16.dp)
                )
            }

            items(photos.chunked(3).size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    photos.chunked(3)[index].forEach { photo ->
                        if (token != null) {
                            Box(
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                ThumbnailOfPhotoFromServer(
                                    index = photos.indexOf(photo),
                                    token = token,
                                    photo = photo,
                                    navHostController = navHostController,
                                    syncedPhotoView = syncedPhotoView,
                                    isPressedPhotoMode = isPressedPhotoMode,
                                    listOfSelectedPhoto = listOfSelectedPhoto
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}