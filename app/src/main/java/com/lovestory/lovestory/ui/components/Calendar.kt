package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.lovestory.lovestory.R
import com.lovestory.lovestory.model.CoupleMemory
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
    coupleMemoryList: List<CoupleMemory>,
    onOpenDialogRequest : () -> Unit,
    onClick: (CalendarDay) -> Unit = {},
){

    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square sizing!
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = {
                    onClick(day)
                    onOpenDialogRequest()
                }
            )
            .background(color = if (day.date == LocalDate.now() && day.position == DayPosition.MonthDate) colorResource(com.lovestory.lovestory.R.color.ls_pink) else Color.Transparent),
        contentAlignment = Alignment.TopCenter //텍스트 상단 중앙 배치
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val textColor = when (day.position) {
                DayPosition.MonthDate -> when (day.date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
                }
                    // coupleMemoryList.firstOrNull { it.date == day.date }?.let {
                    //colorResource(R.color.black)//R.color.ls_pink) // 공휴일 색 넣는 로직 넣을 예정
                //} ?: Color.Black // 공휴일 색 바꾸는 로직 필요
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

            //LaunchedEffect()
            coupleMemoryList.firstOrNull { it.date == day.date }?.let{
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
                //modifier = Modifier
                //.weight(1f)
                //    .testTag("MonthTitle"),
                text = currentMonth.year.toString(),//currentMonth.displayText(),
                fontSize = 20.sp,
                //textAlign = TextAlign.Center,
                //fontWeight = FontWeight.ExtraBold,
                color = Color.Black, // 툴바에 있는 월, 연도 글자의 색 분홍색으로 할 거면 color = colorResource(color.ls_pink)
            )
            Text(
                //modifier = Modifier
                //.weight(1f)
                //    .testTag("MonthTitle"),
                text = currentMonth.month.displayText(),//currentMonth.displayText(),
                fontSize = 32.sp,
                //textAlign = TextAlign.Center,
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
                modifier = Modifier.size(50.dp)
            ){
                CalendarNavigationIcon(
                    icon = painterResource(id = R.drawable.ic_chevron_left),
                    contentDescription = "Previous",
                    onClick = goToPrevious,
                )
            }
            Box(
                modifier = Modifier.size(50.dp)
            ){
                CalendarNavigationIcon(
                    icon = painterResource(id = R.drawable.ic_chevron_right),
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
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .align(Alignment.Center),
        painter = icon,
        tint = Color.Black,
        contentDescription = contentDescription,
    )
}

@Composable
fun rememberFirstCompletelyVisibleMonth(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    // Only take non-null values as null will be produced when the
    // list is mid-scroll as no index will be completely visible.
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