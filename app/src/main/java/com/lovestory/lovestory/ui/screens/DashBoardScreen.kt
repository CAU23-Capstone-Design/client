package com.lovestory.lovestory.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.model.UsersOfCoupleInfo
import com.lovestory.lovestory.module.dashboard.getCoupleInfo
import com.lovestory.lovestory.module.dashboard.requestUsersOfCoupleInfo
import com.lovestory.lovestory.module.shared.getDistanceInfo
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.AnimateCharacter
import com.lovestory.lovestory.ui.components.AnimateFlyHeart
import com.lovestory.lovestory.ui.components.AnimateHeart
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DashBoardScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val currentDate = LocalDate.now()
    val coroutineScope = rememberCoroutineScope()

    val doubleBackToExitPressedOnce = remember { mutableStateOf(false) }
    val isVisibleFlyingAnimation = remember { mutableStateOf(false) }
    val countDay = remember { mutableStateOf(0L) }

    val coupleInfo :MutableState<UsersOfCoupleInfo?> = remember {
        mutableStateOf(null)
    }
    val coupleDistance = remember {
        mutableStateOf(99999999)
    }

    LaunchedEffect(null){
        val result = getCoupleInfo(context)
        if(result == null){
            coupleInfo.value = requestUsersOfCoupleInfo(context)
            Log.d("DashBoard Screen", "${coupleInfo.value}")
        }else{
            coupleInfo.value = result
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val localDate = LocalDate.parse(coupleInfo!!.value!!.firstDate!!, formatter)
            countDay.value = currentDate.toEpochDay() - localDate.toEpochDay()
        }
    }

    LaunchedEffect(null){
        val result = getDistanceInfo(context)

        if(result == null){
            coupleDistance.value = 99999999
        }
    }

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
        Text(text = "${coupleDistance.value}", modifier = Modifier
            .padding(top = 70.dp)
            .clickable {
                val result = getDistanceInfo(context)
                if (result == null) {
                    coupleDistance.value = 99999999
                } else {
                    coupleDistance.value = result
                }
            })
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color(0xBBF3F3F3))
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
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
        ) {
            AnimateCharacter()
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
                    Text(
                        text = "D + ${countDay.value}",
                        fontFamily = vitro,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${coupleInfo!!.value!!.firstDate?.substring(0,10)}",
                        fontFamily = vitro,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            Column() {
                Text(text = "여기에 뭘 넣으면 좋을까?!!!!!!!")
            }
        }

        AnimatedVisibility(isVisibleFlyingAnimation.value , enter = fadeIn(), exit = fadeOut()){
            AnimateFlyHeart()
        }
    }
}