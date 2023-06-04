package com.lovestory.lovestory.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
public fun Dp.toInt(): Int {
    return (this.value * LocalDensity.current.density).toInt()
}
