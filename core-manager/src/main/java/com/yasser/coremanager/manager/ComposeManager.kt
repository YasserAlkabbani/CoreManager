package com.yasser.coremanager.manager

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

sealed class ComposeManager {
    internal object Idle:ComposeManager()
    object HideKeyBoard: ComposeManager()
    object NextFocus: ComposeManager()
    object DownFocus: ComposeManager()
    class ShowToast(val textManager: TextManager):ComposeManager()
}

