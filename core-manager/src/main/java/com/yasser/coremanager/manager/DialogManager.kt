package com.yasser.coremanager.manager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class DialogManager(internal val dialogManagerContent:DialogManagerContent) {
    object Hide:DialogManager(DialogManagerContentEmpty())
    class Show(dialogManagerContent:DialogManagerContent) :DialogManager(dialogManagerContent)
}

abstract class DialogManagerContent{
    @Composable abstract fun DialogContent()
}

class DialogManagerContentEmpty:DialogManagerContent(){
    @Composable override fun DialogContent() { Surface(modifier = Modifier.fillMaxWidth().height(10.dp)){} }
}