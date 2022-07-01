package com.yasser.coremanager.manager

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

sealed class ComposeManager {
    internal object Idle:ComposeManager()
    object HideKeyBoard: ComposeManager()
    object NextFocus: ComposeManager()
    object DownFocus: ComposeManager()
    object Popup:ComposeManager()
    class ShowToast(val textManager: TextManager):ComposeManager()
    class Navigation(val destinationManager: DestinationManager,val arg1Value:String?=null,val arg2Value:String?=null):ComposeManager()
}

