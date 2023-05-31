package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.maxkeppeker.sheets.core.models.base.SheetState
import java.time.LocalDate

@Composable
fun textFieldForAuth(
    name : String,
    onNameChanged : (String) -> Unit,
    label : String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions {  }
){
    TextField(
        value = "$name",
        onValueChange = onNameChanged,
        label = { Text(label) },
        modifier = Modifier
            .height(52.dp)
            .width(280.dp),
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF8F8F8),
            textColor = Color(0xFF000000),
            focusedIndicatorColor = Color(0xFFFFFFFF),
            unfocusedIndicatorColor = Color(0xFFF8F8F8)
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Preview(showBackground = true, name="이름")
@Composable
fun InputPreview() {
    var name by remember { mutableStateOf("nickname", ) }
    val onNameChanged: (String) -> Unit = { name = it }
    LoveStoryTheme() {
        textFieldForAuth(name = name, onNameChanged=onNameChanged, label = name)
    }
}

@Composable
fun TextFieldForCalendar(selectedDates :  MutableState<LocalDate>, calendarState : SheetState, labelText : String){
    TextField(
        enabled = false,
        readOnly = true,
        value = selectedDates.value.toString(),
        onValueChange = {},
        label = { Text(labelText) },
        modifier = Modifier
            .height(50.dp)
            .width(280.dp),
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF8F8F8),
            textColor = Color(0xFF000000),
            focusedIndicatorColor = Color(0xFFF8F8F8),
            unfocusedIndicatorColor = Color(0xFFF8F8F8),
            placeholderColor = Color.Black,
            disabledPlaceholderColor = Color.Black,
            disabledLabelColor = Color.Black
        ),
        trailingIcon = { CalendarButton(calendarState) }
    )
}

@Composable
fun EditableTextField(initialValue: String, onValueChanged: (String) -> Unit) {
    var value by remember { mutableStateOf(initialValue) }

    TextField(
        value = value,
        onValueChange = {
            value = it
            onValueChanged(it)
        },
        placeholder = {Text("코멘트를 입력해보세요!")},
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        shape = RoundedCornerShape(10f.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF8F8F8),
            textColor = Color(0xFF000000),
            focusedIndicatorColor = Color.Transparent,//Color(0xFFFFFFFF),
            unfocusedIndicatorColor = Color.Transparent// Color(0xFFF8F8F8)
        )
    )
}