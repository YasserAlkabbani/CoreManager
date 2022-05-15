package com.yasser.coremanager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.yasser.coremanager.manager.ComposeManager
import com.yasser.coremanager.manager.CoreManager
import com.yasser.coremanager.manager.DialogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CoreViewModel @Inject constructor(
    private val coreManager: CoreManager
):ViewModel() {

    private val _composeManager:MutableStateFlow<ComposeManager> = MutableStateFlow(ComposeManager.Idle)
    internal val composeManager:StateFlow<ComposeManager> =_composeManager
    internal fun setComposeManager(newComposeManager: ComposeManager){_composeManager.update { newComposeManager }}

    private val _dialogManager:MutableStateFlow<DialogManager> = MutableStateFlow(DialogManager.Hide)
    val dialogManager:StateFlow<DialogManager> =_dialogManager
    internal fun setDialogManager(newDialogManager: DialogManager){ _dialogManager.update { newDialogManager } }
    internal fun setHideDialog(){_dialogManager.update {DialogManager.Hide}}

    fun setNavHostController(newNavHostController: NavHostController){
        coreManager.navigationManager.setNavHostController(newNavHostController)
    }

}