package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.ui.components.*
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SignUpScreen(navHostController: NavHostController, id:String?, nickname:String?, gender:String?, birthday:String?) {
    Log.d("user Info", "$id, $nickname, $gender, $birthday")
    Log.d("SignUp-Screen", "회원가입 스크린 호출")

    val activity = LocalContext.current as ComponentActivity
    val focusManager = LocalFocusManager.current

//    focusManager.moveFocus(focusDirection = FocusDirection)

    var thisYear = LocalDate.now().year

    var birthdayParse = birthday?.chunked(2)
    val birthdayString = "${thisYear-1}-${
        if(birthdayParse != null){
            birthdayParse[0]    
        }else{
            val monthValue= LocalDate.now().month.value
            if(LocalDate.now().month.value<10){
                "0$monthValue"
            }else{
                "$monthValue"
            }
        }
    }-${
        if(birthdayParse !=null){
            birthdayParse[1]
        }else{
            LocalDate.now().dayOfMonth.toString()
        }
    }"

    var name by remember { mutableStateOf("$nickname", ) }
    val onNameChanged: (String) -> Unit = { name = it }

    var userGender by remember { mutableStateOf(
        if(gender == "MALE"){
            "M"
        }else{
            "F"
        }
    ) }
    val onUserGenderChange : (String) -> Unit = {userGender = it}

    val calendarForBirthState = rememberSheetState(visible = false)
//    val calendarForMeetState = rememberSheetState(visible = false)
    val selectedBirthDates = remember { mutableStateOf<LocalDate>(LocalDate.parse(birthdayString, DateTimeFormatter.ISO_DATE)) }
//    val selectedMeetDates = remember { mutableStateOf<LocalDate>(LocalDate.now()) }

    CalendarDialogForSignUp(calendarState = calendarForBirthState, selectedDates = selectedBirthDates)
//    CalendarDialogForSignUp(calendarState = calendarForMeetState, selectedDates = selectedMeetDates)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { /* 생략 */ },
        content = { paddingValue->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValue.calculateTopPadding(),
                        bottom = paddingValue.calculateBottomPadding()
                    )
            ){
                SelectAvatar("default Img Url", sizeAvatar = 120)
                Spacer(modifier = Modifier.height(50.dp))

                textFieldForAuth(
                    name = name,
                    onNameChanged=onNameChanged,
                    label="이름",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            calendarForBirthState.show()
                        }
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextFieldForCalendar(
                    selectedDates = selectedBirthDates,
                    calendarState = calendarForBirthState,
                    labelText = "생년월일")
                Spacer(modifier = Modifier.height(20.dp))
//                TextFieldForCalendar(
//                    selectedDates = selectedMeetDates,
//                    calendarState = calendarForMeetState,
//                    labelText = "처음 만난 날")
//                Spacer(modifier = Modifier.height(16.dp))
                ToggleForTwoButton(
                    "남자",
                    "여자",
                    0xFFFFB6B6,
                    0xFFF8F8F8,
                    selectedGender = userGender,
                    onChangeGender = onUserGenderChange
                )
                Spacer(modifier = Modifier.height(90.dp))
                ButtonForSignUp(
                    navHostController = navHostController,
                    buttonText="커플 등록하기",
                    id = id,
                    nickname = name,
                    gender =  userGender,
                    birthday = selectedBirthDates.value.toString(),
                    context = activity
                )

                /**manage kakao login user <ONLY FOR TEST>*/
                //ManageKakaoUser(activity)
            }
        }
    )

    activity.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity.finish()
        }
    })
}