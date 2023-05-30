package com.lovestory.lovestory.ui.screens

//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.GoogleMap
//import com.google.maps.android.compose.Marker
//import com.google.maps.android.compose.MarkerState
//import com.google.maps.android.compose.rememberCameraPositionState
import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
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
    val meetDate = remember { mutableStateListOf<String>() }
    val meetDateAfterLoad = remember { mutableStateListOf<String>() }

    val context = LocalContext.current
    val token = getToken(context)
    val dialogWidthDp = remember { mutableStateOf(0.dp) }

    lateinit var repository : SyncedPhotoRepository
    lateinit var repositoryDummy : SyncedPhotoRepository //나중에 월별로 받아오면 삭제할 부분
    val photoDate = remember { mutableStateListOf<String>() }
    val items = remember{ mutableStateListOf<MyItem>() }

    var latLngMarker by remember { mutableStateOf(emptyList<LatLng>()) }
//    val drawable = ContextCompat.getDrawable(context, R.drawable.img) //마커 이미지로 변경
//    val bitmap = (drawable as BitmapDrawable).bitmap

    var latLngExist by remember { mutableStateOf(false) }
    var photoExist by remember { mutableStateOf(false) }

    val syncedPhotosByDate by syncedPhotoView.groupedSyncedPhotosByDate.observeAsState(initial = mapOf())
    val allPhotoListState = rememberLazyGridState()

    var syncedPhotos by remember { mutableStateOf(emptyList<SyncedPhoto>()) }

    var syncedPhoto by remember { mutableStateOf(emptyList<SyncedPhoto>()) }
    var uniqueDate = remember { mutableStateListOf<String>() }

    val systemUiController = rememberSystemUiController()

    //해야 되는 게 코루틴 정리. 룸 db
    LaunchedEffect(key1 = true) {
        val meetDay = getDay(token!!, monthToString(visibleMonth.yearMonth))
        meetDay.body()?.forEach{
            meetDate.add(intmonthToString(visibleMonth.yearMonth, it))
        }

        //shared Preference 에서 get Comment
        val data = getSavedComment(context)
        coupleMemoryList = data
//        coupleMemoryList.forEach { CoupleMemory -> Log.d("쉐어드1", "$CoupleMemory") }

        //get Comment
        val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        if(getMemoryList.isSuccessful){
            getMemoryList.body()!!.forEach {getMemory ->
                val date = LocalDate.parse(getMemory.date, formatter)
                val comment = getMemory.comment
                val stringMemory = StringMemory(date.toString(), comment)
                stringMemoryList.add(stringMemory)
            }
        }
        coupleMemoryList = convertToCoupleMemoryList(stringMemoryList)
        saveComment(context, coupleMemoryList)
        coupleMemoryList.forEach {
            if (!meetDate.contains(dateToString(it.date))) {
                meetDate.add(dateToString(it.date))
            }
        }

        val photoDatabase = PhotoDatabase.getDatabase(context)
        val photoDao = photoDatabase.syncedPhotoDao()
        repository = SyncedPhotoRepository(photoDao)
        syncedPhotos = repository.listOfGetAllSyncedPhoto()

        val uniqueDatesSet = HashSet<String>()

        for (synced in syncedPhotos) {
            if (synced.date.substring(0, 10) !in uniqueDatesSet) {
                uniqueDatesSet.add(synced.date.substring(0, 10))
                meetDate.add(synced.date.substring(0, 10))
                uniqueDate.add(synced.date.substring(0, 10))
            }
        }

        meetDateAfterLoad.addAll(meetDate)
    }

    LaunchedEffect(visibleMonth.yearMonth){
        val meetDay = getDay(token!!, monthToString(visibleMonth.yearMonth))
        meetDay.body()?.forEach{
            meetDateAfterLoad.add(intmonthToString(visibleMonth.yearMonth, it))
        }
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
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
            modifier = Modifier.wrapContentWidth(),//.background(color = Color.White, RoundedCornerShape(30.dp)),
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

    if (isPopupVisible) {
//        coroutineScope.launch{
//            if(meetDate.contains(dateToString(selection.date))){
//                dialogContent = true
//            }
//        }
        dialogContent = meetDateAfterLoad.contains(dateToString(selection.date))

        var editedcomment by remember { mutableStateOf("") }
        if(commentSave != ""){
            editedcomment = commentSave
        }

        val existingMemory = coupleMemoryList.firstOrNull { it.date == selection.date }
        if (existingMemory != null) {
            editedcomment = existingMemory.comment
        }

        CalendarDialog(
//            selection = selection,
            onDismissRequest = {
                if(existingMemory != null) {
                    coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment
                    coroutineScope.launch{
                        val put : Response<Any> = putComment(token!!, dateToString(selection.date), editedcomment)
                        saveComment(context, coupleMemoryList)
                    }
                } else {
                    if ( editedcomment != ""){
                        val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                        coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}
                        coroutineScope.launch{
                            val put : Response<Any> = putComment(token!!, dateToString(selection.date), editedcomment)
                            saveComment(context, coupleMemoryList)
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
            }, //onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            var photoPosition by remember { mutableStateOf(emptyList<LatLng>()) }

            LaunchedEffect(null){
                isPopupVisibleSave = true
                Log.d("실행","2, $isPopupVisible, $isPopupVisibleSave, ${items.isNotEmpty()}")
                //get GPS
                val gps = getGps(token!!, dateToString(selection.date))
                if (gps.body() != null) {
                    latLng = getLatLng(gps.body()!!)
                }
                latLngMarker = latLng

                val photoDatabase = PhotoDatabase.getDatabase(context)
                val photoDao = photoDatabase.syncedPhotoDao()
                repository = SyncedPhotoRepository(photoDao)

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
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = Color.Transparent)
                        .padding(start = 25.dp, end = 25.dp, top = 15.dp, bottom = 10.dp),//vertical = 15.dp, horizontal = 25.dp),
                    verticalAlignment = Alignment.Bottom
                ){
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
                        text = selection.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())+"요일",
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {
                            coroutineScope.launch {
                                val date = selection.date
                                coupleMemoryList = coupleMemoryList.filterNot { it.date == date }
                                val delete: Any = deleteComment(token!!, dateToString(selection.date))
                                if(!photoDate.contains(dateToString(date))){
                                    meetDateAfterLoad.remove(dateToString(date))
                                }
                            }
                            saveComment(context, coupleMemoryList)
                            isPopupVisible = false
                            isPopupVisibleSave = false
                            commentSave = ""
                        },
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        //.padding(bottom = 5.dp),//wrapContentSize(),
                        shape = CircleShape,
                    ){
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete"
                        )
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {
                            if(existingMemory != null) {
                                coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment
                                //sendComment
                                coroutineScope.launch{
                                    val put : Response<Any> = putComment(token!!, dateToString(selection.date), editedcomment)
                                    saveComment(context, coupleMemoryList)
                                }
                            } else {
                                if ( editedcomment != ""){
                                    val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                                    coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}
                                    coroutineScope.launch{
                                        val put : Response<Any> = putComment(token!!, dateToString(selection.date), editedcomment)
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
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .padding(bottom = 5.dp),//wrapContentSize(),
                        shape = CircleShape,
                    ){
                        Text(
                            text = "X",
                            fontSize = 22.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, end = 20.dp))

                Spacer(modifier = Modifier.height(15.dp))

                EditableTextField(
                    initialValue = editedcomment,
                    onValueChanged = {editedcomment = it}
                )

                Spacer(modifier = Modifier.height(20.dp))

                if(dialogContent) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp)
                            .wrapContentHeight()
                            .background(color = Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        selectionSave = selection
                        if (!dataLoaded.value) {
                            //스켈레톤 추가
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(2f)
                                    .background(color = Color.Transparent)
                                    .clip(RoundedCornerShape(12.dp))
                            ){
                                Text(
                                    text = "지도 로드 중...",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else if (dataLoaded.value) {
                            if (latLng.isNotEmpty()) {
                                LaunchedEffect(null) {
                                    coroutineScopeMap.launch {
                                        syncedPhoto.forEach {
                                            val cacheKey = "thumbnail_${it.id}"
                                            val cachedBitmap =
                                                loadBitmapFromDiskCache(context, cacheKey)
                                            if (cachedBitmap != null) {
                                                items.add(
                                                    MyItem(
                                                        LatLng(it.latitude, it.longitude),
                                                        "PHOTO",
                                                        "사진",
                                                        cachedBitmap!!,
                                                        "PHOTO",
                                                        it.id
                                                    )
                                                )
                                            } else {
                                                val getResult = getThumbnailForPhoto(token!!, it.id)
                                                items.add(
                                                    MyItem(
                                                        LatLng(it.latitude, it.longitude),
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
                                        .clip(RoundedCornerShape(12.dp)),
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
                                    var clusterManager by remember { mutableStateOf<ClusterManager<MyItem>?>(null) }
                                    MapEffect(items) { map ->
                                        if (clusterManager == null) {
                                            clusterManager = ClusterManager<MyItem>(context, map)
                                        }
                                        clusterManager?.addItems(items)
                                        clusterManager?.renderer = MarkerClusterRender(context,map,clusterManager!!) {
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
                            }else{
                                //스켈레톤 추가
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f)
                                        .background(color = Color.Transparent)
                                        .clip(RoundedCornerShape(12.dp))
                                ){
                                    Text(
                                        text = "위치 데이터가 없어요...",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    if(!uniqueDate.contains(dateToString(selection.date))) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.7f)
                                .padding(start = 20.dp, end = 20.dp)
                                .background(color = Color.Transparent, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                        ){
                            Text(
                                text = "사진이 없어요...",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }else {
                        val boxWidth = remember { mutableStateOf(Dp.Unspecified) }
                        val dens = LocalDensity.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.7f)
                                .padding(start = 20.dp, end = 20.dp)
                                .background(color = Color.Transparent, RoundedCornerShape(12.dp))
                                .onSizeChanged {
                                    boxWidth.value = it.width.toDp(dens)
                                },
                        ) {
                            val filteredSyncedPhotosByDate = syncedPhotosByDate.filterKeys { key ->
                                key == dateToString(selection.date)
                            }
                            //isPopupVisibleSave = true
                            PhotoForCalendar(
                                syncedPhotosByDate = filteredSyncedPhotosByDate,
                                token = token,
                                syncedPhotoView = syncedPhotoView,
                                navHostController = navHostController,
                                allPhotoListState = allPhotoListState,
                                widthDp = boxWidth.value,
                                selectDate = dateToString(selection.date),
                                isPopupVisibleSave = isPopupVisibleSave,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }else{
                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - 80.dp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenWidth + 60.dp)
                            .padding(start = 20.dp, end = 20.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "만난 기록이 없어요...",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
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