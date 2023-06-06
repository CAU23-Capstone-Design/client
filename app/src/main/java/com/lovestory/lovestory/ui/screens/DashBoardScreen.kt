package com.lovestory.lovestory.ui.screens

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.lovestory.lovestory.model.UsersOfCoupleInfo
import com.lovestory.lovestory.module.dashboard.getCoupleInfo
import com.lovestory.lovestory.module.dashboard.requestUsersOfCoupleInfo
import com.lovestory.lovestory.module.shared.getDistanceInfo
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.AnimateCharacter
import com.lovestory.lovestory.ui.components.AnimateFlyHeart
import com.lovestory.lovestory.ui.components.AnimateHeart
import com.lovestory.lovestory.ui.components.copyAssetToTempFile
import com.lovestory.lovestory.view.NearbyView
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashBoardScreen(
    navHostController: NavHostController,
    nearbyView: NearbyView
) {

    val context = LocalContext.current
    val currentDate = LocalDate.now()
    val coroutineScope = rememberCoroutineScope()

    val doubleBackToExitPressedOnce = remember { mutableStateOf(false) }
    val isVisibleFlyingAnimation = remember { mutableStateOf(false) }
    val countDay = remember { mutableStateOf(0L) }

    val coupleInfo :MutableState<UsersOfCoupleInfo?> = remember {
        mutableStateOf(null)
    }
//    val coupleDistance = remember {
//        mutableStateOf(99999999)
//    }

    val isNearby by nearbyView.isNearby.observeAsState(initial = false)

    LaunchedEffect(null){
        val result = getCoupleInfo(context)
        if(result == null){
            Log.d("DashBoard Screen", "getCoupleInfo() is null")
            coupleInfo.value = requestUsersOfCoupleInfo(context)
            Log.d("DashBoard Screen", "coupleInfo.value : ${coupleInfo.value}")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val localDate = LocalDate.parse(coupleInfo.value!!.firstDate!!, formatter)
            countDay.value = currentDate.toEpochDay() - localDate.toEpochDay()
            Log.d("DashBoard Screen", "${coupleInfo.value}")
        }else{
            Log.d("DashBoard Screen", "getCoupleInfo() is not null")
            coupleInfo.value = result
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val localDate = LocalDate.parse(coupleInfo!!.value!!.firstDate!!, formatter)
            countDay.value = currentDate.toEpochDay() - localDate.toEpochDay()
        }
    }

//    LaunchedEffect(null){
//        val result = getDistanceInfo(context)
//
//        if(result == null){
//            coupleDistance.value = 99999999
//        }
//    }

    BackHandler(enabled = true) {
        if(doubleBackToExitPressedOnce.value){
            (context as Activity).finish()
        }else{
            doubleBackToExitPressedOnce.value = true
            Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()

            coroutineScope.launch {
                delay(2000)
                doubleBackToExitPressedOnce.value = false
            }
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ){
//        Text(text = "${coupleDistance.value}", modifier = Modifier
//            .padding(top = 70.dp)
//            .clickable {
//                val result = getDistanceInfo(context)
//                if (result == null) {
//                    coupleDistance.value = 99999999
//                } else {
//                    coupleDistance.value = result
//                }
//            })

        HeaderOfDashBoard()

        ContentOfDashBoard(
            coupleInfo = coupleInfo,
            isVisibleFlyingAnimation = isVisibleFlyingAnimation,
//            countDay = countDay
            isNearby = isNearby
        )

        AnimatedVisibility(isVisibleFlyingAnimation.value , enter = fadeIn(), exit = fadeOut()){
            AnimateFlyHeart()
        }



//        DisplayGif()
    }
}

//@Composable
//fun DisplayGif() {
//    val context = LocalContext.current
//    val tempFile = copyAssetToTempFile(context, "alone.gif")
//    val painter = rememberImagePainter(data = tempFile)
//    Image(painter = painter, contentDescription = "My gif")
//}


@Composable
fun HeaderOfDashBoard(){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color(0xFFF3F3F3))
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp)
    ){
        Text(
            text = "Lovestory",
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = vitro,
        )
    }
}

@Composable
fun ContentOfDashBoard(
    coupleInfo : MutableState<UsersOfCoupleInfo?>,
    isVisibleFlyingAnimation : MutableState<Boolean>,
    isNearby : Boolean,
){
    val currentDate = LocalDate.now()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp)
    ) {

        AnimatedVisibility(visible = isNearby, enter = fadeIn(), exit = fadeOut())
        {
            AnimateCharacter()
        }
        AnimatedVisibility(visible = !isNearby , enter = fadeIn(), exit = fadeOut()){
            GifImage()
        }
        Spacer(modifier = Modifier.height(20.dp))
        if(coupleInfo.value != null){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xB5FFDBDB))
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                ){
                    Text(
                        text = coupleInfo!!.value!!.user1.name,
                        fontFamily = vitro,
                        fontSize = 18.sp
                    )
                    Box(modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .clickable {
                            if (!isVisibleFlyingAnimation.value) {
                                isVisibleFlyingAnimation.value = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    delay(3000)
                                    isVisibleFlyingAnimation.value = false
                                }
                            }
                        }) {
                        AnimateHeart()
                    }
                    Text(
                        text = coupleInfo!!.value!!.user2.name,
                        fontFamily = vitro,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(visible = coupleInfo.value != null, enter = fadeIn(), exit = fadeOut()){
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    val localDate = LocalDate.parse(coupleInfo.value!!.firstDate!!, formatter)
                    Text(
//                    text = "D + ${countDay.value}",
                        text = "D+ ${currentDate.toEpochDay() - localDate.toEpochDay()}",
                        fontFamily = vitro,
                        fontSize = 20.sp
                    )
                }
                AnimatedVisibility(visible = coupleInfo.value == null, enter = fadeIn(), exit = fadeOut()){
                    Text(
                        text = " ",
                        fontFamily = vitro,
                        fontSize = 20.sp
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "From. ${coupleInfo!!.value!!.firstDate?.substring(0,10)}",
                    fontFamily = vitro,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        Column() {
//            Text(text = "여기에 뭘 넣으면 좋을까?!!!!!!!")
        }
    }
}

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val tempFile = copyAssetToTempFile(context, "alone2.gif")
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = tempFile).apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}