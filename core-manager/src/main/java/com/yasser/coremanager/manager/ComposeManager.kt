package com.yasser.coremanager.manager

import androidx.navigation.NavHostController

sealed class ComposeManager {
    internal object Idle:ComposeManager()
    object HideKeyBoard: ComposeManager()
    object NextFocus: ComposeManager()
    object DownFocus: ComposeManager()
    object Popup: ComposeManager()
    class ShowToast(val textManager: TextManager):ComposeManager()
    class Navigate(val navigate: NavHostController.()->Unit):ComposeManager()
}

