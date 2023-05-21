package com.lovestory.lovestory.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.graphs.RootNavigationGraph
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.lovestory.lovestory.R
import com.lovestory.lovestory.event.kakaoLogoutEvent
import com.lovestory.lovestory.graphs.AuthScreen
import com.lovestory.lovestory.graphs.Graph
import com.lovestory.lovestory.graphs.loginNavGraph
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.module.checkLoginToken
import com.lovestory.lovestory.module.deleteToken
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.deleteCouple
import com.lovestory.lovestory.network.getCoupleInfo
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ProfileScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    var token by remember {mutableStateOf(getToken(context))}
    val appKey = context.getString(R.string.app_kakao_key)
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    if (token == null) {
        token = getToken(context)
        if (token == null){
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSyncoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(null){
        val coupleInfo = getCoupleInfo(token!!)
        if(coupleInfo.isSuccessful){
            name = coupleInfo.body()!!.user1.name
            gender = coupleInfo.body()!!.user1.gender
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val bitmap = if (gender == "male"){
                val drawable = ContextCompat.getDrawable(context, R.drawable.img_male)
                (drawable as BitmapDrawable).bitmap
            } else if(gender == "W"){
                val drawable = ContextCompat.getDrawable(context, R.drawable.img_female)
                (drawable as BitmapDrawable).bitmap
            } else{
                val drawable = ContextCompat.getDrawable(context, R.drawable.img_human)
                (drawable as BitmapDrawable).bitmap
            }

            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(text = name,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 25.sp,
                    //fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = {
                        //
                    }
                ){
                    Text(
                        text = "사용 가이드",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                TextButton(
                    onClick = {
                        //
                    }
                ){
                    Text(
                        text = "개인정보 수집",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                TextButton(
                    onClick = {
                        showLogoutDialog = true
                    }
                ){
                    Text(
                        text = "로그아웃",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                TextButton(
                    onClick = {
                        showSyncoutDialog = true
                    }
                ){
                    Text(text = "상대방과 연결 끊기",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(200.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            modifier = Modifier
                .wrapContentHeight()
                .width(360.dp),
            shape = RoundedCornerShape(12.dp),
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("카카오톡 로그아웃") },
            text = { Text("정말로 로그아웃 하시겠습니까?") },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    TextButton(
                        onClick = {
                            //kakaoLogoutEvent(appKey = appKey, context = context)
                            deleteToken(context = context)
                            token = null
                            showLogoutDialog = false
                        }
                    ){
                        Text("확인", color = Color.Red)
                    }
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                        }
                    ){
                        Text("취소")
                    }
                }
            }
        )
    }

    if (showSyncoutDialog) {
        AlertDialog(
            modifier = Modifier
                .wrapContentHeight()
                .width(360.dp),
            shape = RoundedCornerShape(12.dp),
            onDismissRequest = { showSyncoutDialog = false },
            title = { Text("상대방과 연결 끊기") },
            text = {
                Column(){
                    Text("경고!",
                        style = TextStyle(
                            //color = Color.Black,
                            //fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                    Text("상대방과 연결을 끊을 경우 데이터 복구가 불가능합니다.")
                    Text("정말로 연결을 끊으시겠습니까?")
                }
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    TextButton(
                        onClick = {
//                            coroutineScope.launch{
//                                deleteCouple(token)
//                            }
                            showSyncoutDialog = false
                        }
                    ){
                        Text("확인", color = Color.Red)
                    }
                    TextButton(
                        onClick = {
                            showSyncoutDialog = false
                        }
                    ){
                        Text("취소")
                    }
                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun DefaultPreview3() {
    val navController = rememberNavController()
    LoveStoryTheme {
        ProfileScreen(navHostController = navController)
    }
}