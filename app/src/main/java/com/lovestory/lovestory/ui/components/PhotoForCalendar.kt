package com.lovestory.lovestory.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.core.CalendarDay
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.model.dateToString
import com.lovestory.lovestory.model.toDp
import com.lovestory.lovestory.ui.screens.MyItem
import com.lovestory.lovestory.view.SyncedPhotoView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun PhotoListForCalendar(
    syncedPhotosByDate: Map<String, List<SyncedPhoto>>,
    token: String?,
    navHostController: NavHostController,
    syncedPhotoView : SyncedPhotoView,
//    allPhotoListState: LazyGridState,
//    widthDp: Dp,
    selectDate: String,
    uniqueDate: SnapshotStateList<String>,
    selection: CalendarDay,
//    isPopupVisibleSave: Boolean,
    //onPopupVisibilityChange: (Boolean) -> Unit
){
//    val syncedPhotosByDate = syncedPhotosByDate
//    val allPhotoListState = rememberLazyGirdState()

    if (!uniqueDate.contains(dateToString(selection.date))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.7f)
                .padding(start = 20.dp, end = 20.dp)
                .background(
                    color = Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "사진이 없어요...",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        val boxWidth = remember { mutableStateOf(Dp.Unspecified) }
        val dens = LocalDensity.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.7f)
                .padding(start = 20.dp, end = 20.dp)
                .background(
                    color = Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .onSizeChanged {
                    boxWidth.value = it.width.toDp(dens)
                },
        ) {
            val filteredSyncedPhotosByDate =
                syncedPhotosByDate.filterKeys { key ->
                    key == dateToString(selection.date)
                }
            //isPopupVisibleSave = true
//                                        PhotoForCalendar(
//                                            syncedPhotosByDate = filteredSyncedPhotosByDate,
//                                            token = token,
//                                            syncedPhotoView = syncedPhotoView,
//                                            navHostController = navHostController,
//                                            allPhotoListState = allPhotoListState,
//                                            widthDp = boxWidth.value,
//                                            selectDate = dateToString(selection.date),
//                                            isPopupVisibleSave = isPopupVisibleSave,
//                                        )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 10.dp),
//                state = allPhotoListState
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
                                widthDp = boxWidth.value,
                                date = selectDate,
                                onImageClick = {
                                }
                            )
                        }
                    }
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