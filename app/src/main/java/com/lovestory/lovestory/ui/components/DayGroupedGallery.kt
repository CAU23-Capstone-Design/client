package com.lovestory.lovestory.ui.components

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
fun DayGroupedGallery(
    daySyncedPhotoByDate: Map<String, List<SyncedPhoto>>,
    token: String?,
    currentDate: LocalDate,
    allPhotoListState: LazyListState,
    dayPhotoListState: LazyListState,
    setSelectedButton: (String)->Unit,
    cumOfSizeOfInnerElements: List<List<Int>>
){

    LazyColumn(
        modifier = Modifier.padding(bottom = 70.dp),
        contentPadding = PaddingValues(top=65.dp, bottom = 75.dp),
        state = dayPhotoListState
        ){
        daySyncedPhotoByDate.onEachIndexed { index, (date, daySyncedPhoto) ->

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
            items(daySyncedPhoto.size) { innerIndex ->
//                Log.d("GalleryScreen", "id : ${daySyncedPhoto[index].id} | date : ${daySyncedPhoto[index].date} | area1 : ${daySyncedPhoto[index].area1} | area2 : ${daySyncedPhoto[index].area2} | area3 : ${daySyncedPhoto[index].area3}")
//                Log.d("GalleryScreen", "$index, $innerIndex")
                val curIndex = if(cumOfSizeOfInnerElements[index][innerIndex]!=null){cumOfSizeOfInnerElements[index][innerIndex]}else{null}
//                Log.d("GalleryScreen", "$curIndex")

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    if (token != null) {
                        if(curIndex != null){
                            BigThumbnailFromServer(
                                index = innerIndex,
                                token = token,
                                photoId = daySyncedPhoto[innerIndex].id,
                                location = daySyncedPhoto[innerIndex].area1+" "+daySyncedPhoto[innerIndex].area2,
                                allPhotoListState = allPhotoListState,
                                setSelectedButton = setSelectedButton,
                                curIndex = curIndex
                            )
                        }

                    }
                }
            }
        }
    }
}

