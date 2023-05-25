package com.lovestory.lovestory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.getToken

@Composable
fun PrivacyScreen(navHostController: NavHostController){
    val context = LocalContext.current
    val token = getToken(context)
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
            Column(modifier = Modifier.padding(start = 20.dp, top = 70.dp), horizontalAlignment = Alignment.Start) {
                Text("1. 수집하는 개인정보 항목 및 수집 방법",)
                Spacer(modifier = Modifier.height(10.dp))
                Text("2. 개인정보의 수집 및 이용 목적",)
                Spacer(modifier = Modifier.height(10.dp))
                Text("3. 개인정보의 보유 및 이용 기간",)
                Spacer(modifier = Modifier.height(10.dp))
                Text("4. 개인정보 제공에 관한 사항 (제3자 제공 등)",)
                Spacer(modifier = Modifier.height(10.dp))
                Text("5. 개인정보의 파기 절차 및 방법",)
                Spacer(modifier = Modifier.height(10.dp))
                Text("6. 이용자 및 법정대리인의 권리와 그 행사방법",)
                Spacer(modifier = Modifier.height(10.dp))
                Text("7. 개인정보 자동 수집 장치의 설치/운영 및 거부에 관한 사항",)
            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xBBF3F3F3))
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier.clickable { navHostController.popBackStack() },
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "개인정보 수집",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

    }
}