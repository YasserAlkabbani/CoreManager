package com.yasser.applycoremanager

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.yasser.core_manager.manager.*
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
        navigateTo = { route ->  coreManager.composeManagerEvent(ComposeManager.Navigate(route))},
        goToSettings ={coreManager.activityManagerEvent(StartActivityManager.GoToSettings)},
        goToSendEmail = {coreManager.activityManagerEvent(StartActivityManager.GoToSendEmail(it))},
        pickImageFromGallery ={
            coreManager.permissionManagerEvent(PermissionManager.ReadExternalStorage(
                taskToDoWhenPermissionGranted = { coreManager.activityForResultManagerEvent(ActivityForResultManager.PickImageFromGallery { val imageFile=it()}) },
                showRequestPermissionRationale = { coreManager.activityManagerEvent(StartActivityManager.GoToSettings) },
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))}
            ))
        } ,
        startActivityForResult ={intent->
            coreManager.activityForResultManagerEvent(ActivityForResultManager.CustomActivityForResult(intent){ val activityResult=it() })
        },
        requestCustomPermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.CustomPermission(
                    permission = Manifest.permission.CALL_PHONE,
                    taskToDoWhenPermissionGranted = {}, showRequestPermissionRationale = {}, taskToDoWhenPermissionDeclined = {}
                )
            )
        },




        requestRecordAudioPermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.RecordAudio(
                    taskToDoWhenPermissionGranted = {Log.d("TestPermission","taskToDoWhenPermissionGranted")},
                    showRequestPermissionRationale = {Log.d("TestPermission","showRequestPermissionRationale")},
                    taskToDoWhenPermissionDeclined = {Log.d("TestPermission","taskToDoWhenPermissionDeclined")}
                )
            )
        },


        setText1 = {newText->_mainUIState.update { it.copy(textField1 =newText ) }},
        setText2 = {newText->_mainUIState.update { it.copy(textField2 =newText ) }},
        setText3 = {newText->_mainUIState.update { it.copy(textField3 =newText ) }},

        requestPermissionCamera = {
            coreManager.permissionManagerEvent(
                PermissionManager.Camera(
                    taskToDoWhenPermissionGranted = {},
                    showRequestPermissionRationale = {},
                    taskToDoWhenPermissionDeclined = {}
                )
            )
        },
        requestPermissionWriteExternalStorage = {
            coreManager.permissionManagerEvent(
                PermissionManager.WriteExternalStorage(
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
                    showRequestPermissionRationale = {},
                    taskToDoWhenPermissionDeclined = {}
                )
            )
        },
        startPhoneCall = {phoneNumber->
            coreManager.permissionManagerEvent(PermissionManager.CallPhone(
                taskToDoWhenPermissionGranted = {coreManager.activityManagerEvent(StartActivityManager.StartCallPhone(phoneNumber))},
                showRequestPermissionRationale = {coreManager.activityManagerEvent(StartActivityManager.GoToSettings)},
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))},
               )
            )
        },

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
    val requestReadExternalStoragePermission:()->Unit, val requestPermissionWriteExternalStorage:()->Unit,
    val requestRecordAudioPermission:()->Unit, val requestPermissionCamera:()->Unit,
    val requestCustomPermission:(String)->Unit,
    val pickImageFromGallery:()->Unit, val startActivityForResult:(Intent)->Unit,
    val goToSettings:()->Unit, val startPhoneCall:(phone:String)->Unit, val goToSendEmail:(email:String)->Unit,
    val navigateTo:(route:String)->Unit
)