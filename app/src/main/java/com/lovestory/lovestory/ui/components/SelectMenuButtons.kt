package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectMenuButtons(items : List<String>, selectedButton : String, onClick : (String)->Unit){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xDAD5D2D2), shape = RoundedCornerShape(25.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ){
        items.forEach { item->
            SelectMenuButton(onClick = {
                onClick(item as String)}, buttonText = item, isSelected = item == selectedButton)
        }
    }
}

@Composable
fun SelectMenuButton(
    onClick: () -> Unit,
    buttonText: String,
    isSelected: Boolean,
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor =  if (isSelected) Color(0xFFFCC5C5) else Color.Transparent,
            contentColor = Color.Black,
            disabledContentColor = Color.Black.copy(alpha = 0f)
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
        modifier = Modifier
            .width(70.dp)
            .height(40.dp),
        content = {
            Text(text = buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
    )
}

@Composable
fun SelectHelpButtons(items : List<String>, selectedButton : String, onClick : (String)->Unit){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xDAD5D2D2), shape = RoundedCornerShape(25.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ){
        items.forEach { item->
            SelectHelpButton(onClick = {
                onClick(item as String)}, buttonText = item, isSelected = item == selectedButton)
        }
    }
}

@Composable
fun SelectHelpButton(
    onClick: () -> Unit,
    buttonText: String,
    isSelected: Boolean,
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor =  if (isSelected) Color(0xFFFCC5C5) else Color.Transparent,
            contentColor = Color.Black,
            disabledContentColor = Color.Black.copy(alpha = 0f)
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        content = {
            Text(text = buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
    )
}