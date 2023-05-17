package com.lovestory.lovestory.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.model.UsersOfCoupleInfo
import com.lovestory.lovestory.module.dashboard.getCoupleInfo
import com.lovestory.lovestory.module.dashboard.requestUsersOfCoupleInfo
import com.lovestory.lovestory.resource.vitro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess


@Composable
fun DashBoardScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val currentDate = LocalDate.now()
    val coroutineScope = rememberCoroutineScope()

    val doubleBackToExitPressedOnce = remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("love-animation.json")
    )
    val lottieAnimatable = rememberLottieAnimatable()

    val coupleInfo :MutableState<UsersOfCoupleInfo?> = remember {
        mutableStateOf(null)
    }

    LaunchedEffect(coupleInfo.value){
        val result = getCoupleInfo(context)
        if(result == null){
            coupleInfo.value = requestUsersOfCoupleInfo(context)
            Log.d("DashBoard Screen", "${coupleInfo.value}")
        }else{
            coupleInfo.value = result
        }
    }

    BackHandler(enabled = true) {
        if(doubleBackToExitPressedOnce.value){
            (context as Activity).finish()
        }else{
            doubleBackToExitPressedOnce.value = true
            Toast.makeText(context, "한 번 더 뒤로 가기 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show()

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
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    intent.putExtra(Settings.EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEEC9C9)),
                shape = RoundedCornerShape(25.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "open app setting"
                    )
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            if(coupleInfo.value != null){
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val localDate = LocalDate.parse(coupleInfo!!.value!!.firstDate!!, formatter)
//                val meetDate = LocalDate.parse()

                val diff = Period.between(localDate, currentDate)
                val day = diff.days

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time.time

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = coupleInfo!!.value!!.user1.name,
                            fontFamily = vitro,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = coupleInfo!!.value!!.user2.name,
                            fontFamily = vitro,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "D + $day",
                        fontFamily = vitro,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Box(modifier = Modifier
                .height(200.dp)
                .width(200.dp)) {

                LaunchedEffect(composition) {
                    lottieAnimatable.animate(
                        composition = composition,
//                        clipSpec = LottieClipSpec.Frame(0, 1200),
//                        initialProgress = 0f,
                        reverseOnRepeat = true,
                        iterations = LottieConstants.IterateForever,
                        iteration = 4,
                        continueFromPreviousAnimate = true
                    )
                }

//            val composition = rememberLottieComposition(R.raw.lottie_animation)
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
//                    LottieAnimation(composition = composition, progress = { /*TODO*/ })
                    LottieAnimation(
                        composition = composition,
                        progress = lottieAnimatable.progress,
                        contentScale = ContentScale.FillHeight,
                        renderMode = RenderMode.AUTOMATIC,
                    )
                }
            }
        }



    }
}