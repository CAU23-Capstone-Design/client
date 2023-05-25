package com.lovestory.lovestory.model

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

fun Float.toDp(density: Density): Dp {
    return Dp(this / density.density)
}

fun Int.toDp(density: Density): Dp {
    return toFloat().toDp(density)
}

fun Double.toDp(density: Density): Dp {
    return toFloat().toDp(density)
}