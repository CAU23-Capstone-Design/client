package com.lovestory.lovestory.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.database.entities.SyncedPhoto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun PhotoForCalendar(
    syncedPhotosByDate: Map<String, List<SyncedPhoto>>,
    token: String?,
    navHostController: NavHostController,
    allPhotoListState: LazyListState,
){
    LazyColumn(
        modifier = Modifier.padding(bottom = 70.dp).fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 100.dp),
        state = allPhotoListState
    ){
        syncedPhotosByDate.forEach{(date, photos)->
            Log.d("싱크된 포토 날짜","$date")
            items(photos.chunked(3).size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    photos.chunked(3)[index].forEach { photo ->
                        if (token != null) {
                            Box(
                                modifier = Modifier.fillMaxHeight().background(color = Color.Transparent, RoundedCornerShape(12.dp))
                            ) {
                                ThumbnailOfPhotoFromServer(
                                    index = photos.indexOf(photo),
                                    token = token,
                                    photoId = photo.id,
                                    navHostController = navHostController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}