package com.lovestory.lovestory.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.maxkeppeker.sheets.core.models.base.SheetState
import java.time.LocalDate

@Composable
fun InputMeetDayDialog(
    onDismissRequest : ()-> Unit,
    properties: DialogProperties = DialogProperties(),
    content : @Composable () -> Unit
){
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        content()
    }
}

@Composable
fun CoupleSyncDialog(
    navHostController: NavHostController,
    onDismissRequest : ()->Unit,
    selectedMeetDates : MutableState<LocalDate>,
    calendarForMeetState : SheetState,
    code :String?
    ){
    InputMeetDayDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ){
        Column(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(25.dp))
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
            ButtonForCreateCouple(
                "시작하기",
                navHostController = navHostController,
                code = code,
                meetDay = selectedMeetDates.value.toString(),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}

@Composable
fun DialogForPermission(
){
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val isDialogOpen = remember{ mutableStateOf(true) }
    val onDismissRequest : () -> Unit = {isDialogOpen.value = false}

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
    )
    val permissionResult = ContextCompat.checkSelfPermission(context, permissions[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permissions[1]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permissions[2]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permissions[3]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permissions[4]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permissions[5]) == PackageManager.PERMISSION_GRANTED


    val requestBackgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
//                Toast.makeText(context, " 백그라운드 권한 승인", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, " 백그라운드 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions->
            if(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
//                Toast.makeText(context, "정확한 위치 권한 승인", Toast.LENGTH_SHORT).show()
                requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            else if(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)){
                Toast.makeText(context, "대략적인 위치 권한 승인", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
//                Toast.makeText(context, " 알람 권한 승인", Toast.LENGTH_SHORT).show()
                requestLocationPermissionLauncher.launch(arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }else{
                Toast.makeText(context, " 알람 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestPhotoLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
//                Toast.makeText(context, " 사진 위치 권한 승인", Toast.LENGTH_SHORT).show()
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }else{
                Toast.makeText(context, " 사진 위치 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestPhotoPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
//                Toast.makeText(context, " 사진 권한 승인", Toast.LENGTH_SHORT).show()
                requestPhotoLocationPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION)
            }else{
                Toast.makeText(context, " 사진 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    if(!permissionResult){
        AnimatedVisibility(visible = isDialogOpen.value, enter= fadeIn(), exit= fadeOut()) {

            Dialog(
                onDismissRequest = onDismissRequest,
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Column(
                    modifier = Modifier
                        .width(screenWidth - 20.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(25.dp))
                        .background(color = Color.White)
                        .padding(5.dp)
                    ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Text(
                            text = "LoveStory 이용을 위해\n다음 권한 설정이 필요 합니다.\n다음 권한이 허용되지 않으면 앱 기능이\n제대로 동작하지 않을 수 있습니다.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 20.dp, start = 5.dp, bottom = 10.dp, end = 5.dp)
                    )
                    PermissionDialogItem(
                        id = com.lovestory.lovestory.R.drawable.ic_permission_media_foreground,
                        title = "사진 (필수)",
                        description = "사진 업로드 및 저장"
                    )
                    PermissionDialogItem(
                        id = com.lovestory.lovestory.R.drawable.ic_permission_notification_foreground,
                        title = "알람 (필수)",
                        description = "커플 만남 상태 알람 전송"
                    )
                    PermissionDialogItem(
                        id = com.lovestory.lovestory.R.drawable.ic_permission_location_foreground,
                        title = "위치 (필수)",
                        description = "현재 위치 자동 수신"
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .border(2.dp, Color.Black, CircleShape)
                            .clickable {
                                isDialogOpen.value = false
//                                requestPermissionLauncher.launch(permissions)
                                requestPhotoPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                            }
                            .clip(CircleShape)
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ){
                        Text(text = "설정하기", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun PermissionDialogItem(
    id : Int,
    title : String,
    description: String
){
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
        ){
            Icon(
                painter = painterResource(id),
                contentDescription = "icon",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(20.dp))
        Column() {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = description, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProgressBarDialog(
    onDismissRequest : ()-> Unit,
    properties: DialogProperties = DialogProperties(),
    content : @Composable () -> Unit
){
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        content()
    }
}