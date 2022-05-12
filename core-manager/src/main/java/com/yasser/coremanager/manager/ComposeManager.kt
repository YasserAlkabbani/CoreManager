package com.yasser.coremanager.manager

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

sealed class ComposeManager {
    internal object Idle:ComposeManager()
    object HideKeyBoard: ComposeManager()
    object NextFocus: ComposeManager()
    object DownFocus: ComposeManager()
    object Popup: ComposeManager()
    class ShowToast(val textManager: TextManager):ComposeManager()
    class Navigate(
        val destinationManager: DestinationManager, val routeValue:String="",
        val launchSingleTop:Boolean=false, val restoreState:Boolean=false,
        val popUpToDestination:DestinationManager?=null,val saveState:Boolean=false,val inclusive:Boolean=false
    ):ComposeManager()
}

