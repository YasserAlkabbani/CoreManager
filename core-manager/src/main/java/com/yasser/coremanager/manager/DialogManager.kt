package com.yasser.coremanager.manager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class DialogManager(val content:@Composable ()->Unit) {
    object Hide:DialogManager({Surface(modifier = Modifier.fillMaxWidth().height(100.dp)){}})
    class Show(dialogContent: @Composable () -> Unit) :DialogManager(dialogContent)
}