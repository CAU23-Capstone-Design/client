package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.R

@Composable
fun ScreenHeaderWithBackButton(
    navHostController : NavHostController,
    headerTitle : String
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xBBF3F3F3))
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = null,
                modifier = Modifier.clip(shape = CircleShape).clickable {navHostController.popBackStack() }.padding(10.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = headerTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ScreenHeaderWithDropDown(
    navHostController : NavHostController,
    headerTitle: String,
    dropDownIconComposable: @Composable () -> Unit,
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF3F3F3))
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 10.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = null,
                modifier = Modifier.clip(shape = CircleShape).clickable {navHostController.popBackStack() }.padding(10.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = headerTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        dropDownIconComposable()
    }
}