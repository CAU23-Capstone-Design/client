package com.lovestory.lovestory.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.module.kakaoLogin
import com.lovestory.lovestory.module.loveStoryCheckCode
import com.lovestory.lovestory.module.loveStorySignUp
import com.lovestory.lovestory.resource.apple_bold
import com.maxkeppeker.sheets.core.models.base.SheetState

@Composable
fun ButtonForAuth(navHostController: NavHostController){
    val context = LocalContext.current
    Button(
        onClick = { kakaoLogin(context, navHostController) },
        modifier = Modifier
            .height(50.dp)
            .width(240.dp),
        colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFFFCEB57)
            ,contentColor = Color(0xFF131313)
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.elevation(disabledElevation = 0.dp, defaultElevation = 0.dp, pressedElevation = 0.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Image(
                painter = painterResource(id = com.lovestory.lovestory.R.drawable.kakao_logo),
                contentDescription = "kakao login",
                modifier = Modifier
                    .size(24.dp),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = "카카오로 시작하기",
                modifier = Modifier.padding(start = 15.dp),
                fontFamily = apple_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun ButtonForToggleGender(
    color: Long,
    text : String,
    onChangeGender : (String)->Unit
){
    Button(
        onClick = { if(text=="남자"){
            onChangeGender("M")
        }else{
            onChangeGender("W")
        }
        },
        modifier = Modifier
            .height(50.dp)
            .width(110.dp),
        colors = ButtonDefaults.textButtonColors(backgroundColor = Color(color)
            ,contentColor = Color(0xFF131313)
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.elevation(disabledElevation = 0.dp, defaultElevation = 0.dp, pressedElevation = 0.dp)

    ) {
        Text(
            text = "$text",
            fontFamily = apple_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ButtonForSignUp(
    navHostController: NavHostController,
    buttonText : String,
    id:String?,
    nickname:String,
    gender:String,
    birthday:String,
    context : Context,
){
    Button(
        onClick = {
            loveStorySignUp(
            navHostController = navHostController,
            id = id, nickname = nickname,
            gender = gender,
            birthday = birthday,
            context = context ) },
        modifier = Modifier
            .height(50.dp)
            .width(280.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = Color(0xFFFFB6B6),
            contentColor = Color(0xFF131313)
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.elevation(
            disabledElevation = 0.dp,
            defaultElevation = 0.dp,
            pressedElevation = 0.dp)

    ) {
        Text(
            text = "$buttonText",
            fontFamily = apple_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

@Composable
fun ButtonForSyncCouple(buttonText : String, onOpenDialogRequest :()->Unit, context: Context, myCode : String?, otherCode : String){
    Button(
        onClick = { loveStoryCheckCode(
            context = context,
            onOpenDialogRequest = onOpenDialogRequest,
            inputCode = otherCode
        ) },
        modifier = Modifier
            .height(50.dp)
            .width(280.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = Color(0xFFFFB6B6),
            contentColor = Color(0xFF131313)
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.elevation(
            disabledElevation = 0.dp,
            defaultElevation = 0.dp,
            pressedElevation = 0.dp)

    ) {
        Text(
            text = "$buttonText",
            fontFamily = apple_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

@Composable
fun CalendarButton(openCalendar : SheetState){
    Row(
        modifier = Modifier.padding(end = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center,

            ) {
            IconButton(
                onClick = {openCalendar.show()},
                modifier = Modifier.size(24.dp),
                content = {
                    Icon(
                        painter = painterResource(id = com.lovestory.lovestory.R.drawable.ic_calendaer),
                        contentDescription = "Add",
                        tint = Color.Gray
                    )
                },
            )
        }
    }

}

//@Preview("buttonForAuth")
//@Composable
//private fun ButtonPreview(){
//    LoveStoryTheme {
//        ButtonForAuth(navHostController: NavHostController)
//    }
//}