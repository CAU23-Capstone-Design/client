package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovestory.lovestory.database.entities.SyncedPhoto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun RepresentPeriodGallery(
    periodGallery : List<SyncedPhoto>,
    token : String?,
    currentDate : LocalDate,

    ){
    LazyColumn(
        modifier = Modifier.padding(bottom = 70.dp),
        contentPadding = PaddingValues(top=65.dp, bottom = 75.dp),
    ){
        periodGallery.onEachIndexed { index, syncedPhoto ->
            item {
                val photoDate = LocalDate.parse(syncedPhoto.date.substring(0,10))
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    if (token != null) {
                        BigThumbnailFromServerForTest(
                            index = index,
                            token = token,
                            photoId = syncedPhoto.id,
                            location = syncedPhoto.area1+" "+syncedPhoto.area2,
                        )

                    }
                }
            }
        }
    }
}