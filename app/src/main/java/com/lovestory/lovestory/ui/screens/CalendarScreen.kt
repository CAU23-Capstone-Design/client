package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.*
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.graphs.CalendarStack
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.*
import com.lovestory.lovestory.module.photo.getThumbnailForPhoto
import com.lovestory.lovestory.network.*
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.*
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(MapsComposeExperimentalApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")

@Composable
fun CalendarScreen(navHostController: NavHostController, syncedPhotoView : SyncedPhotoView) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeek() }

    var selectionSave by rememberSaveable { mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.MonthDate))}
    var isPopupVisibleSave by rememberSaveable { mutableStateOf(false) }
    var commentSave by rememberSaveable{ mutableStateOf("") }

    var selection by remember { mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.MonthDate))}
    var isPopupVisible by remember { mutableStateOf(false) }
    var dialogContent by remember { mutableStateOf(false) }

    LaunchedEffect(null) {
        if (isPopupVisibleSave) {
            selection = selectionSave
            isPopupVisible = true
        }
    }

    val onOpenDialogRequest : ()->Unit = {
        isPopupVisible = true
    }
    val onDismissRequest : () -> Unit = {isPopupVisible = false}

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth =  endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val coroutineScope = rememberCoroutineScope()
    val coroutineScopeMap = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)

    var coupleMemoryList by remember { mutableStateOf(emptyList<CoupleMemory>()) }
    val stringMemoryList = mutableListOf<StringMemory>()

    var latLng by remember { mutableStateOf(emptyList<LatLng>()) }
    var photoPosition by remember { mutableStateOf(emptyList<LatLng>()) }
    val dataLoaded = remember { mutableStateOf(false) }
    val meetDataLoaded = remember { mutableStateOf(false) }
    val meetDate by remember { mutableStateOf(mutableSetOf<String>()) }
    val meetDateAfterLoad = remember { mutableStateListOf<String>() }

    val context = LocalContext.current
    val token = getToken(context)

    val photoDatabase = PhotoDatabase.getDatabase(context)
    val photoDao = photoDatabase.syncedPhotoDao()
    val repository = SyncedPhotoRepository(photoDao)

    var photoDate by remember { mutableStateOf(emptyList<String>()) }
    val items = remember{ mutableStateListOf<MyItem>() }

    var latLngMarker by remember { mutableStateOf(emptyList<LatLng>()) }
    var latLngExist by remember { mutableStateOf(false) }
    var photoExist by remember { mutableStateOf(false) }

    val syncedPhotosByDate by syncedPhotoView.groupedSyncedPhotosByDate.observeAsState(initial = mapOf())
    val allPhotoListState = rememberLazyGridState()

    //var syncedPhotos by remember { mutableStateOf(emptyList<String>()) }
//    val allPhotoListState = rememberLazyListState()
    var syncedPhotos by remember { mutableStateOf(emptyList<SyncedPhoto>()) }

    var syncedPhoto by remember { mutableStateOf(emptyList<SyncedPhoto>()) }
    var uniqueDate by remember { mutableStateOf(mutableSetOf<String>()) }

    val systemUiController = rememberSystemUiController()

    //해야 되는 게 코루틴 정리. 룸 db
    LaunchedEffect(null) {
        //getDayListByTargetMonth
        val listOfDays = repository.getDayListByTargetMonth(monthToString(visibleMonth.yearMonth))
        listOfDays?.forEach {
            uniqueDate.add(it)
            meetDate.add(it)
        }
        Log.d("유니크","$uniqueDate")

        //get Comment
        val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        if(getMemoryList.isSuccessful){
            getMemoryList.body()!!.forEach {getMemory ->
                coupleMemoryList += CoupleMemory(LocalDate.parse(getMemory.date, formatter), getMemory.comment)
            }
        }
        coupleMemoryList.forEach {
            meetDate.add(dateToString(it.date))
        }

        //get meetday by month
        val meetDay = getDay(token!!, monthToString(visibleMonth.yearMonth))
        meetDay.body()?.forEach{
            meetDate.add(intmonthToString(visibleMonth.yearMonth, it))
        }
        meetDateAfterLoad.addAll(meetDate)
    }

    LaunchedEffect(visibleMonth.yearMonth){
        if(visibleMonth.yearMonth != currentMonth) {
            val meetDay = getDay(token!!, monthToString(visibleMonth.yearMonth))
            meetDay.body()?.forEach {
                meetDateAfterLoad.add(intmonthToString(visibleMonth.yearMonth, it))
            }

            val listOfDays = repository.getDayListByTargetMonth(monthToString(visibleMonth.yearMonth))
            listOfDays?.forEach{
                meetDateAfterLoad.add(it)
                uniqueDate.add(it)
            }
        }
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    Box(){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
//                .padding(top = 100.dp),
        ) {
            CalendarHeader(
                visibleMonth = visibleMonth,
                coroutineScope = coroutineScope,
                state = state,
            )
            Spacer(modifier = Modifier.height(5.dp))
            SimpleCalendarTitle(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                currentMonth = visibleMonth.yearMonth,
                goToPrevious = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                    }
                },
                goToNext = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                    }
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
            DaysOfWeekTitle(daysOfWeek = daysOfWeek)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalCalendar(
                modifier = Modifier.wrapContentWidth(),
                state = state,
                dayContent = { day ->
                    Day(
                        day = day,
                        isPopupVisible = isPopupVisible,
                        isSelected = selection == day,
                        onOpenDialogRequest = onOpenDialogRequest,
                        meetDate = meetDateAfterLoad,
                    ) { clicked ->
                        selection = clicked
                    }
                }
            )
        }

    }



    if (isPopupVisible) {
        dialogContent = meetDateAfterLoad.contains(dateToString(selection.date))

        var editedcomment by remember { mutableStateOf("") }
        if(commentSave != ""){
            editedcomment = commentSave
        }

        val existingMemory = coupleMemoryList.firstOrNull { it.date == selection.date }
        if (existingMemory != null) {
            editedcomment = existingMemory.comment
        }

        LoveStoryDialog(
//            selection = selection,
            onDismissRequest = {
                if(existingMemory != null) {
                    coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment
                    coroutineScope.launch{
                        val put : Response<Any> = putComment(token!!, dateToString(selection.date), editedcomment)
                    }
                } else {
                    if ( editedcomment != ""){
                        val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                        coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}
                        coroutineScope.launch{
                            val put : Response<Any> = putComment(token!!, dateToString(selection.date), editedcomment)
                            meetDateAfterLoad.add(dateToString(selection.date))
                        }
                    }
                }
                isPopupVisible = false// Update coupleMemoryList when dialog is dismissed
                isPopupVisibleSave = false
                items.clear()
                latLng = emptyList()
                latLngExist = false
                photoExist = false
                photoPosition = emptyList()
                dataLoaded.value = false
                commentSave = ""
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            var photoPosition by remember { mutableStateOf(emptyList<LatLng>()) }
            LaunchedEffect(null){
                isPopupVisibleSave = true
                //get GPS
                val gps = getGps(token!!, dateToString(selection.date))
                if (gps.body() != null) {
                    latLng = getLatLng(gps.body()!!)
                }
                latLngMarker = latLng

                syncedPhoto = repository.getSyncedPhotosByDate(dateToString(selection.date))

                photoPosition = syncedPhoto.map { it ->
                    LatLng(it.latitude, it.longitude)
                }

                photoPosition.forEach {
                    latLng += it
                }

                dataLoaded.value = true

                //get Comment
                val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                if (getMemoryList.isSuccessful) {
                    getMemoryList.body()!!.forEach { getMemory ->
                        val date = LocalDate.parse(getMemory.date, formatter)
                        val comment = getMemory.comment
                        val stringMemory = StringMemory(date.toString(), comment)
                        stringMemoryList.add(stringMemory)
                    }
                }
                coupleMemoryList = convertToCoupleMemoryList(stringMemoryList)
                saveComment(context, coupleMemoryList)
                coupleMemoryList.forEach {
                    if (!meetDateAfterLoad.contains(dateToString(it.date))) {
                        meetDateAfterLoad.add(dateToString(it.date))
                    }
                }
            }

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            Column(
                modifier = Modifier
                    .width(screenWidth - 40.dp)
//                    .height(500.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = Color.Transparent)
                        .padding(start = 25.dp, end = 25.dp, top = 15.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = selection.date.dayOfMonth.toString(),
                        fontSize = 26.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .weight(1f),
                        text = selection.date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ) + "요일",
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                    ButtonForCalendarDialog(
                        onClick = {
                            coroutineScope.launch {
                                val date = selection.date
                                coupleMemoryList =
                                    coupleMemoryList.filterNot { it.date == date }
                                val delete: Any =
                                    deleteComment(token!!, dateToString(selection.date))
                                if (!uniqueDate.contains(dateToString(date))) {
                                    meetDateAfterLoad.remove(dateToString(date))
                                }
                            }
                            saveComment(context, coupleMemoryList)
                            isPopupVisible = false
                            isPopupVisibleSave = false
                            commentSave = ""
                        },
                        description = "delete",
                        icon = Icons.Outlined.Delete,
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    ButtonForCalendarDialog(
                        onClick = {
                            if (existingMemory != null) {
                                coupleMemoryList.find { it.date == selection.date }?.comment =
                                    editedcomment
                                //sendComment
                                coroutineScope.launch {
                                    val put: Response<Any> = putComment(
                                        token!!,
                                        dateToString(selection.date),
                                        editedcomment
                                    )
                                }
                            } else {
                                if (editedcomment != "") {
                                    val newMemory = CoupleMemory(
                                        date = selection.date,
                                        comment = editedcomment
                                    )
                                    coupleMemoryList = coupleMemoryList
                                        .toMutableList()
                                        .apply { add(newMemory) }
                                    coroutineScope.launch {
                                        val put: Response<Any> = putComment(
                                            token!!,
                                            dateToString(selection.date),
                                            editedcomment
                                        )
                                        saveComment(context, coupleMemoryList)
                                        meetDateAfterLoad.add(dateToString(selection.date))
                                    }
                                }
                            }
                            isPopupVisible = false
                            isPopupVisibleSave = false
                            items.clear()
                            latLng = emptyList()
                            latLngExist = false
                            photoExist = false
                            photoPosition = emptyList()
                            dataLoaded.value = false
                            commentSave = ""
                        },
                        description = "close dialog",
                        icon = Icons.Outlined.Close,
                    )
                }
                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, end = 20.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().height((screenWidth - 80.dp) + 100.dp),
                    contentPadding = PaddingValues(bottom = 20.dp, top = 10.dp, start = 20.dp, end = 20.dp),
                    state = allPhotoListState
                ){
                    item(
                        span = {
                            GridItemSpan(
                                maxLineSpan
                            )
                        }
                    ) {
                        Column() {
                            EditableTextField(
                                initialValue = editedcomment,
                                onValueChanged = { editedcomment = it }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if ((dialogContent && uniqueDate.contains(dateToString(selection.date)) || (dialogContent && latLng.isNotEmpty()))) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f)
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(color = Color(0xFFF8F8F8))
                                ) {
                                    selectionSave = selection
                                    Column(){
                                        AnimatedVisibility(
                                            visible = !dataLoaded.value,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(2f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(color = Color(0xFFF8F8F8))
                                            ) {
                                                Icon(
                                                    Icons.Outlined.LocationOn,
                                                    contentDescription = "Load Google Map",
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .size(50.dp),
                                                    tint = Color(0xFFE47676)
                                                )
                                            }
                                        }
                                    }
                                    Column() {
                                        AnimatedVisibility(
                                            visible = dataLoaded.value,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            if (latLng.isNotEmpty()) {
                                                LaunchedEffect(null) {
                                                    coroutineScopeMap.launch {
                                                        syncedPhoto.forEach {
                                                            val cacheKey = "thumbnail_${it.id}"
                                                            val cachedBitmap =
                                                                loadBitmapFromDiskCache(
                                                                    context,
                                                                    cacheKey
                                                                )
                                                            if (cachedBitmap != null) {
                                                                items.add(
                                                                    MyItem(
                                                                        LatLng(
                                                                            it.latitude,
                                                                            it.longitude
                                                                        ),
                                                                        "PHOTO",
                                                                        "사진",
                                                                        cachedBitmap!!,
                                                                        "PHOTO",
                                                                        it.id
                                                                    )
                                                                )
                                                            } else {
                                                                val getResult =
                                                                    getThumbnailForPhoto(
                                                                        token!!,
                                                                        it.id
                                                                    )
                                                                items.add(
                                                                    MyItem(
                                                                        LatLng(
                                                                            it.latitude,
                                                                            it.longitude
                                                                        ),
                                                                        "PHOTO",
                                                                        "사진",
                                                                        getResult!!,
                                                                        "PHOTO",
                                                                        it.id
                                                                    )
                                                                )
                                                                saveBitmapToDiskCache(
                                                                    context,
                                                                    getResult!!,
                                                                    cacheKey
                                                                )
                                                            }
                                                        }
                                                        //사진 좌표와 비트맵
                                                        latLngMarker.forEach {
                                                            items.add(
                                                                MyItem(
                                                                    it,
                                                                    "LOCATION",
                                                                    "위치",
                                                                    null,
                                                                    "POSITION",
                                                                    "HI"
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                val viewposition = averageLatLng(latLng)
                                                val zoomLevel = getZoomLevelForDistance(
                                                    getMaxDistanceBetweenLatLng(
                                                        viewposition,
                                                        latLng
                                                    )
                                                ) - 1

                                                val cameraPositionState = remember {
                                                    CameraPositionState(
                                                        position = CameraPosition.fromLatLngZoom(
                                                            viewposition,
                                                            zoomLevel
                                                        )
                                                    )
                                                }

                                                GoogleMap(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(2f)
                                                        .clip(RoundedCornerShape(10.dp)),
                                                    cameraPositionState = cameraPositionState,
                                                    onMapClick = {
                                                        coroutineScopeMap.cancel()
                                                        isPopupVisible = false
//                                    isPopupVisibleSave = true
                                                        commentSave = editedcomment
                                                        items.clear()

                                                        navHostController.navigate(
                                                            CalendarStack.Map.route + "/${
                                                                dateToString(
                                                                    selection.date
                                                                )
                                                            }"
                                                        ) {
                                                            launchSingleTop = true
                                                        }
                                                    },
                                                    uiSettings = uiSettings
                                                ) {
                                                    var clusterManager by remember {
                                                        mutableStateOf<ClusterManager<MyItem>?>(
                                                            null
                                                        )
                                                    }
                                                    MapEffect(items) { map ->
                                                        if (clusterManager == null) {
                                                            clusterManager =
                                                                ClusterManager<MyItem>(context, map)
                                                        }
                                                        clusterManager?.addItems(items)
                                                        clusterManager?.renderer =
                                                            MarkerClusterRender(
                                                                context,
                                                                map,
                                                                clusterManager!!
                                                            ) {
                                                            }
                                                        clusterManager?.setOnClusterClickListener {
                                                            false
                                                        }
                                                        clusterManager?.setOnClusterItemClickListener {
                                                            false
                                                        }
                                                    }
                                                    LaunchedEffect(key1 = cameraPositionState.isMoving) {
                                                        if (!cameraPositionState.isMoving) {
                                                            clusterManager?.onCameraIdle()
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (uniqueDate.contains(dateToString(selection.date))) {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .aspectRatio(2f)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(color = Color(0xFFF8F8F8)),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally

                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.ic_not_exist_location_foreground),
                                                            contentDescription = "위치 정보가 없음",
                                                            modifier = Modifier.size(50.dp),
                                                            tint = Color.LightGray
                                                        )
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Text(
                                                            text = "위치 기록이 없어요.",
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.LightGray
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            else {
                                val dialogWidth =
                                    LocalConfiguration.current.screenWidthDp.dp - 80.dp
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dialogWidth),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "기록된 추억이 없어요.",
                                        modifier = Modifier.align(Alignment.Center),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                    Log.d("통과","${uniqueDate.contains(dateToString(selection.date))}")
                    if ( uniqueDate.contains(dateToString(selection.date)) && dialogContent ) {
                        val filteredSyncedPhotosByDate =
                            syncedPhotosByDate.filterKeys { key ->
                                key == dateToString(selection.date)
                            }
                        filteredSyncedPhotosByDate.forEach{(date, photos)->
                            items(photos.size){index ->
                                val boxWidth = remember { mutableStateOf(Dp.Unspecified) }
                                val dens = LocalDensity.current
                                if (token != null) {
                                    ThumbnailOfPhotoFromServerPopup(
                                        index = photos.indexOf(photos[index]),
                                        token = token,
                                        photo = photos[index],
                                        syncedPhotoView = syncedPhotoView,
                                        photoId = photos[index].id,
                                        navHostController = navHostController,
                                        widthDp = boxWidth.value,
                                        date = dateToString(selection.date),
                                        onImageClick = {
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else if(!uniqueDate.contains(dateToString(selection.date)) && latLng.isEmpty() && dialogContent){
                    item(
                        span = {
                            GridItemSpan(
                                maxLineSpan
                            )
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f)
                                .background(
                                    color = Color.White,
                                    RoundedCornerShape(12.dp)
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_not_exist_image_foreground),
                                contentDescription = "이미지가 존재하지 않음" ,
                                modifier = Modifier.size(50.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "사진이 없어요.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                        }
                    }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CalendarHeader(
    visibleMonth: CalendarMonth,
    coroutineScope: CoroutineScope,
    state: CalendarState
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
//        SimpleCalendarTitle(
//            modifier = Modifier
//                .padding(horizontal = 0.dp, vertical = 12.dp),
//            currentMonth = visibleMonth.yearMonth,
//            goToPrevious = {
//                coroutineScope.launch {
//                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
//                }
//            },
//            goToNext = {
//                coroutineScope.launch {
//                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
//                }
//            },
//        )
        Text(
            text = "캘린더",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

val uiSettings = MapUiSettings(
        compassEnabled = false,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = false,
        scrollGesturesEnabled = false,
        scrollGesturesEnabledDuringRotateOrZoom = false,
        tiltGesturesEnabled = false,
        zoomControlsEnabled = false,
        zoomGesturesEnabled = false
    )