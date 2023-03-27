package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.loveStoryCheckCouple
import com.lovestory.lovestory.resource.ManageKakaoUser
import com.lovestory.lovestory.resource.apple_bold
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.*
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import java.time.LocalDate

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CoupleSyncScreen(
    navHostController: NavHostController,
    myCode : String?,
    nickname : String?
) {
    val context = LocalContext.current as ComponentActivity
    val focusManager = LocalFocusManager.current
    var code by remember { mutableStateOf("", ) }
    val onCodeChanged: (String) -> Unit = {
        if(it.length <= 8){
            code = it
        }
    }

    var isVisible by remember {
        mutableStateOf(false)
    }
    val onOpenDialogRequest : ()->Unit = {isVisible = true}
    val onDismissRequest : () -> Unit = {isVisible = false}

    val selectedMeetDates = remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    val calendarForMeetState = rememberSheetState(visible = false)
    CalendarDialogForSignUp(calendarState = calendarForMeetState, selectedDates = selectedMeetDates)

    val token = getToken(context)

    DisposableEffect(key1 = token) {
        Log.d("Couple-Sync-Screen", "코루틴 시작")
        val checkCoupleJob = loveStoryCheckCouple(navHostController = navHostController, token = token, context = context)

        onDispose {
            Log.d("Couple-Sync-Screen", "코루틴 종료 및 스크린 제거")
            checkCoupleJob.cancel()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){

        Avatar(imgUrl = "캐릭터 넣자...!", sizeAvatar = 120)
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "${nickname}님의 코드",
            fontFamily = apple_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$myCode",
            fontFamily = apple_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(36.dp))
        textFieldForAuth(
            name=code,
            onNameChanged = onCodeChanged,
            label= "코드 입력",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {focusManager.clearFocus()}
            )
        )
        Spacer(modifier = Modifier.height(100.dp))
        ButtonForSyncCouple(
            buttonText = "코드 입력 완료",
            onOpenDialogRequest = onOpenDialogRequest,
            myCode = myCode,
            otherCode = code,
            context = context
        )
//        ManageKakaoUser(context = context)
    }

    AnimatedVisibility(visible = isVisible, enter = scaleIn(animationSpec = tween(durationMillis = 300), initialScale = 0.1f)){
        CoupleSyncDialog(
            navHostController = navHostController,
            onDismissRequest = onDismissRequest,
            selectedMeetDates = selectedMeetDates,
            calendarForMeetState = calendarForMeetState,
            code =code,
        )
    }
}