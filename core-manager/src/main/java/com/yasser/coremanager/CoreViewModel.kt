package com.yasser.coremanager

import android.util.Log
import androidx.lifecycle.ViewModel
import com.yasser.coremanager.manager.ComposeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CoreViewModel @Inject constructor():ViewModel() {

    private val _composeManager:MutableStateFlow<ComposeManager> = MutableStateFlow(ComposeManager.Idle)
    internal val composeManager:StateFlow<ComposeManager> =_composeManager
    internal fun setComposeManager(newComposeManager: ComposeManager){_composeManager.update { newComposeManager }}

}