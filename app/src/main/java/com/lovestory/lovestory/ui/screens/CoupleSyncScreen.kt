package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.lovestory.lovestory.module.loveStoryCheckCouple
import com.lovestory.lovestory.ui.components.*
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import java.time.LocalDate

@Composable
fun CoupleSyncScreen(
    navHostController: NavHostController,
    id : String?,
    myCode : String?,
) {
    val context = LocalContext.current as ComponentActivity
    val focusManager = LocalFocusManager.current
    var code by remember { mutableStateOf("", ) }
    val onCodeChanged: (String) -> Unit = {
        if(it.length <= 6){
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

    Log.d("CoupleSync-Screen", "커플 동기화 스크린 호출")
    Log.d("CoupleSync-Screen", "$id / $code ")

//    loveStoryCheckCouple(navHostController = navHostController, id = id)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){

        Avatar(imgUrl = "캐릭터 넣자...!", sizeAvatar = 120)
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "사용자 닉네임의 코드\n$myCode")
        Spacer(modifier = Modifier.height(36.dp))
        textFieldForAuth(
            name=code,
            onNameChanged = onCodeChanged,
            label= "코드 입력",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {focusManager.clearFocus()}
            )
        )
        Spacer(modifier = Modifier.height(100.dp))
        ButtonForSyncCouple(
            buttonText = "시작하기",
            onOpenDialogRequest = onOpenDialogRequest,
            myCode = myCode,
            otherCode = code,
            context = context
        )

    }

    if(isVisible){
        CoupleSyncDialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        ){
            Column(
                modifier = Modifier
                    .width(360.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Spacer(modifier = Modifier.height(20.dp))
                TextFieldForCalendar(
                    selectedDates = selectedMeetDates,
                    calendarState = calendarForMeetState,
                    labelText = "처음 만난 날")
                Spacer(modifier = Modifier.height(20.dp))
            }

        }
    }
}