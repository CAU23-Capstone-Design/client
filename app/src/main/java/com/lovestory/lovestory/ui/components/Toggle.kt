package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.ui.theme.LoveStoryTheme

@Composable
fun ToggleForTwoButton(
    leftValue : String,
    rightValue : String,
    selectedColor : Long,
    unSelectedColor : Long,
    selectedGender : String,
    onChangeGender : (String)->Unit
){
    Row(
        modifier = Modifier
            .height(50.dp)
            .width(280.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonForToggleGender(
            color = if(selectedGender == "M") {
                selectedColor
            }else{
                unSelectedColor },
            text = leftValue,
            onChangeGender = onChangeGender)

        ButtonForToggleGender(
            color = if(selectedGender != "M") {
                selectedColor
            }
            else{
                unSelectedColor },
            text = rightValue,
            onChangeGender = onChangeGender)
    }
}



@Preview(showBackground = true, name="이름")
@Composable
fun TogglePreview() {
    LoveStoryTheme() {
//        ToggleForTwoButton("남자", "여자", 0xFFFFB6B6, 0xFFF8F8F8)
    }
}