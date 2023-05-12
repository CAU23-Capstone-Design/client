package com.lovestory.lovestory.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
    prevPhotoState : LazyListState,
    currentPhotoState : LazyListState,
    setSelectedButton: (String)->Unit,
    cumPrevPhotosList : List<List<Int>>,
    type : String
    ){
    Log.d("[COMPOSABLE] RepresentPreiodGallery", "cum : $cumPrevPhotosList")
    LazyColumn(
        modifier = Modifier.padding(bottom = 70.dp),
        contentPadding = PaddingValues(top=65.dp, bottom = 75.dp),
        state = currentPhotoState
    ){
        periodGallery.onEachIndexed { index, syncedPhoto ->
            item {
                val photoDate = LocalDate.parse(syncedPhoto.date.substring(0,10))
                val dateFormatter = if (type == "월") {
                    DateTimeFormatter.ofPattern("yyyy년 M월", Locale.getDefault())
                } else {
                    DateTimeFormatter.ofPattern("yyyy년", Locale.getDefault())
                }

                val formattedDate = photoDate.format(dateFormatter)

                val curIndex = if(cumPrevPhotosList[index][cumPrevPhotosList[index].size-1] != null) cumPrevPhotosList[index][cumPrevPhotosList[index].size-1] else null

                Text(
                    text = formattedDate,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    if (token != null) {
                        if (curIndex !=null){
                            BigThumbnailFromServerForTest(
                                index = index,
                                token = token,
                                photoId = syncedPhoto.id,
                                location = syncedPhoto.area1+" "+syncedPhoto.area2,
                                prevPhotoState = prevPhotoState,
                                setSelectedButton = setSelectedButton,
                                curIndex = curIndex,
                                type = type
                            )
                        }


                    }
                }
            }
        }
    }
}