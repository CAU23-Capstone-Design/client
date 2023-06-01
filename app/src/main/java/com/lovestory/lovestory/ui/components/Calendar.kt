package com.lovestory.lovestory.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.lovestory.lovestory.R
import com.lovestory.lovestory.model.dateToString
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    isPopupVisible: Boolean = false,
    meetDate: List<String>,
    onOpenDialogRequest : () -> Unit,
    onClick: (CalendarDay) -> Unit = {},
){
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.LightGray),
                enabled = day.position == DayPosition.MonthDate,
                onClick = {
                }
            )
            .pointerInput(Unit){
                detectTapGestures(
                    onTap = {
                        onClick(day)
                        onOpenDialogRequest()
                    },
                    onDoubleTap = {
                        onClick(day)
                        onOpenDialogRequest()
                    }
                )
            }
            .background(color = if (day.date == LocalDate.now() && day.position == DayPosition.MonthDate) colorResource(com.lovestory.lovestory.R.color.ls_pink) else Color.White),
        contentAlignment = Alignment.TopCenter //텍스트 상단 중앙 배치
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val textColor = when (day.position) {
                DayPosition.MonthDate -> when (day.date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
                }
                DayPosition.InDate, DayPosition.OutDate -> Color.Transparent // 해당 월에 속하지 않은 날들의 숫자 색
            }
            val circleColor = when (day.position){
                DayPosition.MonthDate -> colorResource(R.color.ls_pink)
                DayPosition.InDate, DayPosition.OutDate -> Color.Transparent
            }
            Text(
                modifier = Modifier
                    .padding(top = 5.dp), // 위에서 5dp 만큼 간격
                text = day.date.dayOfMonth.toString(), //텍스트 가운데 정렬
                color = textColor, //해당 월에 있는 날들은 검은 색으로 표시, 아닌데 나오는 날들은 회색으로 표시
                fontWeight = FontWeight.Bold,//굵기 조절
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(2.dp))

            AnimatedVisibility(
                visible = (meetDate.contains(dateToString(day.date)) && (day.date != LocalDate.now())) && (day.position == DayPosition.MonthDate),
                enter = fadeIn(),
                exit = fadeOut()
            ){
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(color = circleColor, CircleShape), //colorResource(R.color.ls_pink)
                    contentAlignment = Alignment.Center){
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold, //굵기 조절
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontSize = 18.sp,
                color = when (dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
                },
            )
        }
    }
}


@Composable
fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.wrapContentHeight(),//.fillMaxWidth(),//height(60.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column( modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = currentMonth.year.toString(),//currentMonth.displayText(),
                fontSize = 20.sp,
                color = Color.Black, // 툴바에 있는 월, 연도 글자의 색 분홍색으로 할 거면 color = colorResource(color.ls_pink)
            )
            Text(
                text = currentMonth.month.displayText(),//currentMonth.displayText(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black, // 툴바에 있는 월, 연도 글자의 색
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ){
            Box(
                modifier = Modifier.size(30.dp)
            ){
                CalendarNavigationIcon(
                    icon = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Previous",
                    onClick = goToPrevious,
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Box(
                modifier = Modifier.size(30.dp)
            ){
                CalendarNavigationIcon(
                    icon = painterResource(id = R.drawable.arrow_right) ,
                    contentDescription = "Next",
                    onClick = goToNext,
                )
            }
        }
    }
}

@Composable
private fun CalendarNavigationIcon(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onClick),
) {
    Icon(
        icon,
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .align(Alignment.Center),
        tint = Color.Black,
        contentDescription = contentDescription,
    )
}

@Composable
fun rememberFirstCompletelyVisibleMonth(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.completelyVisibleMonths.firstOrNull() }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

private val CalendarLayoutInfo.completelyVisibleMonths: List<CalendarMonth>
    get() {
        val visibleItemsInfo = this.visibleMonthsInfo.toMutableList()
        return if (visibleItemsInfo.isEmpty()) {
            emptyList()
        } else {
            val lastItem = visibleItemsInfo.last()
            val viewportSize = this.viewportEndOffset + this.viewportStartOffset
            if (lastItem.offset + lastItem.size > viewportSize) {
                visibleItemsInfo.removeLast()
            }
            val firstItem = visibleItemsInfo.firstOrNull()
            if (firstItem != null && firstItem.offset < this.viewportStartOffset) {
                visibleItemsInfo.removeFirst()
            }
            visibleItemsInfo.map { it.month }
        }
    }

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.getDefault())
}