package com.yasser.applycoremanager

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.yasser.coremanager.manager.*
import com.yasser.coremanager.manager.ComposeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val coreManager:CoreManager):ViewModel() {

    val mainUIEvent=MainUIEvent(
        popUp = {coreManager.composeManagerEvent(ComposeManager.Popup)},
        hideKeyBoard = {coreManager.composeManagerEvent(ComposeManager.HideKeyBoard)},
        nextFocus = {coreManager.composeManagerEvent(ComposeManager.NextFocus)},
        showToast = { coreManager.composeManagerEvent(ComposeManager.ShowToast(it))},
        navigateTo = { route ->  coreManager.composeManagerEvent(ComposeManager.Navigate { navigate(route) })},

        requestCameraPermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.Camera(
                    taskToDoWhenPermissionGranted = {},
                    showRequestPermissionRationale = {},
                    taskToDoWhenPermissionDeclined = {}
                )
            )
        },
        requestReadExternalStoragePermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.ReadExternalStorage(
                    taskToDoWhenPermissionGranted = {},
                    showRequestPermissionRationale = {coreManager.activityManagerEvent(StartActivityManager.GoToSettings)},
                    taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))}
                )
            )
        },
        requestCustomPermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.CustomPermission(
                    permission = it,
                    taskToDoWhenPermissionGranted = {}, showRequestPermissionRationale = {}, taskToDoWhenPermissionDeclined = {}
                )
            )
        },
        requestRecordAudioPermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.RecordAudio(
                    taskToDoWhenPermissionGranted = {Log.d("TestPermission","taskToDoWhenPermissionGranted")},
                    showRequestPermissionRationale = {coreManager.activityManagerEvent(StartActivityManager.GoToSettings)},
                    taskToDoWhenPermissionDeclined = {Log.d("TestPermission","taskToDoWhenPermissionDeclined")}
                )
            )
        },
        requestCallPhonePermission = {
            coreManager.permissionManagerEvent(PermissionManager.CallPhone(
                taskToDoWhenPermissionGranted = { coreManager.activityManagerEvent(StartActivityManager.StartCallPhone("+963966994266"))},
                showRequestPermissionRationale = { coreManager.activityManagerEvent(StartActivityManager.GoToSettings) },
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(R.string.test_toast_resource.asTextManager()))}
             )
         )
        },

        goToSettings ={coreManager.activityManagerEvent(StartActivityManager.GoToSettings)},
        goToSendEmail = {address,subject,body->coreManager.activityManagerEvent(StartActivityManager.GoToSendEmail(address,subject,body))},
        startCustomIntent = {coreManager.activityManagerEvent(StartActivityManager.CustomIntent(it))},

        startPhoneCall = {phoneNumber->
            coreManager.permissionManagerEvent(PermissionManager.CallPhone(
                taskToDoWhenPermissionGranted = {coreManager.activityManagerEvent(StartActivityManager.StartCallPhone(phoneNumber))},
                showRequestPermissionRationale = {coreManager.activityManagerEvent(StartActivityManager.GoToSettings)},
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))},
            )
            )
        },
        pickImageFromGallery ={
            coreManager.permissionManagerEvent(PermissionManager.ReadExternalStorage(
                taskToDoWhenPermissionGranted = { coreManager.activityForResultManagerEvent(ActivityForResultManager.PickImageFromGallery { val imageFile=it()}) },
                showRequestPermissionRationale = { coreManager.activityManagerEvent(StartActivityManager.GoToSettings) },
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))}
            ))
        } ,
        startActivityForResult ={intent->
            coreManager.permissionManagerEvent(PermissionManager.ReadExternalStorage(
                taskToDoWhenPermissionGranted = { coreManager.activityForResultManagerEvent(ActivityForResultManager.PickImageFromGallery { val imageFile=it()}) },
                showRequestPermissionRationale = { coreManager.activityManagerEvent(StartActivityManager.GoToSettings) },
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))}
            ))
        },


        setText1 = {newText->_mainUIState.update { it.copy(textField1 =newText ) }},
        setText2 = {newText->_mainUIState.update { it.copy(textField2 =newText ) }},
        setText3 = {newText->_mainUIState.update { it.copy(textField3 =newText ) }},
    )
    private val _mainUIState:MutableStateFlow<MainUIState> = MutableStateFlow(MainUIState())
    val mainUIState:StateFlow<MainUIState> =_mainUIState
}

data class MainUIState(
    val textField1:String="",val textField2:String="",val textField3:String=""
)
data class MainUIEvent(
        val setText1:(String)->Unit, val setText2:(String)->Unit, val setText3:(String)->Unit,

        val hideKeyBoard:()->Unit, val nextFocus:()->Unit, val popUp:()->Unit, val showToast:(TextManager)->Unit,
        val goToSettings:()->Unit, val navigateTo:(route:String)->Unit,

        val requestReadExternalStoragePermission:()->Unit, val requestRecordAudioPermission:()->Unit,
        val requestCameraPermission:()->Unit, val requestCustomPermission:(String)->Unit,
        val requestCallPhonePermission:()->Unit,

        val pickImageFromGallery:()->Unit, val startActivityForResult:(Intent)->Unit,
        val startPhoneCall:(phone:String)->Unit,
        val goToSendEmail:(email:String,subject:String,body:String)->Unit, val startCustomIntent:(intent: Intent)->Unit,

    )