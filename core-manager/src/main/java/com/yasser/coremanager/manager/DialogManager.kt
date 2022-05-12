package com.yasser.coremanager.manager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class DialogManager(internal val dialogManagerContent:()-> @Composable ()->Unit) {
    object Hide:DialogManager({{Surface(modifier = Modifier.fillMaxWidth().height(10.dp)){}}})
    class Show(dialogManagerContent:()-> @Composable ()->Unit) :DialogManager(dialogManagerContent)
}