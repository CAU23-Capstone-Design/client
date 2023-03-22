package com.lovestory.lovestory.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CoupleSyncDialog(
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