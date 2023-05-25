package com.lovestory.lovestory.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.ui.screens.MyItem
import com.lovestory.lovestory.view.SyncedPhotoView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun PhotoForCalendar(
    syncedPhotosByDate: Map<String, List<SyncedPhoto>>,
    token: String?,
    navHostController: NavHostController,
    syncedPhotoView : SyncedPhotoView,
    allPhotoListState: LazyGridState,
    widthDp: Dp,
    selectDate: String,
    isPopupVisibleSave: Boolean,
    //onPopupVisibilityChange: (Boolean) -> Unit
){
    val syncedPhotosByDate = syncedPhotosByDate

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 10.dp),
        state = allPhotoListState
    ){
        syncedPhotosByDate.forEach{(date, photos)->
            items(photos.size) { index ->
                if (token != null) {
                    ThumbnailOfPhotoFromServerPopup(
                        index = photos.indexOf(photos[index]),
                        token = token,
                        photo = photos[index],
                        syncedPhotoView = syncedPhotoView,
                        photoId = photos[index].id,
                        navHostController = navHostController,
                        widthDp = widthDp,
                        date = selectDate,
                        onImageClick = {
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoForMap(
    syncedPhotosByDate: Map<String, List<SyncedPhoto>>,
    token: String?,
    navHostController: NavHostController,
    syncedPhotoView : SyncedPhotoView,
    allPhotoListState: LazyListState,
    widthDp: Dp,
    selectDate: String
){
    val syncedPhotosByDate = syncedPhotosByDate
    Log.d("클러스터2","$syncedPhotosByDate")

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(2.dp),
        contentPadding = PaddingValues(bottom = 10.dp),
        state = allPhotoListState,
//        horizontalAlignment = Alignment.
    ){
        syncedPhotosByDate.forEach{(date, photos)->
            items(photos.chunked(3).size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
//                    verticalAlignment = Alignment.Top
                ) {
                    photos.chunked(3)[index].forEach { photo ->
                        if (token != null) {
                            Box(
                                modifier = Modifier.fillMaxHeight().background(color = Color.Transparent, RoundedCornerShape(12.dp))
                            ) {
                                ThumbnailOfPhotoFromServerPopup(
                                    index = photos.indexOf(photo),
                                    token = token,
                                    photo = photo,
                                    syncedPhotoView = syncedPhotoView,
                                    photoId = photo.id,
                                    navHostController = navHostController,
                                    widthDp = widthDp,
                                    date = selectDate,
                                    onImageClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}