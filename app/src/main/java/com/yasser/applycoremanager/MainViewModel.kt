package com.yasser.applycoremanager

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasser.coremanager.manager.*
import com.yasser.coremanager.manager.ComposeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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
        requestLocationPermission = {
            coreManager.permissionManagerEvent(
                PermissionManager.LocationPermission(
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
        restartApp = {coreManager.activityManagerEvent(StartActivityManager.RestartApp)},
        getStringFromRes = {Log.d("TestCoreManager", coreManager.stringByRes(it))},
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

        requestManagerWithState = {
            viewModelScope.launch {
                coreManager.requestProcessWithState {
                    delay(1000)
                    "TASK RESULT WITH STATE"
                }.collect{
                    when(it){
                        is ResultManagerWithState.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithState.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithState.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
                    }
                }
                delay(3000)
                coreManager.requestProcessWithState(
                    forceRefreshData = true,
                    taskForRefreshData = {
                        delay(1000)
                        "TASK RESULT WITH STATE REFRESH"
                    },
                    taskForReturnData = {
                        delay(1000)
                        "TASK RESULT WITH STATE RETURN CACHING"
                    }
                ).collect{
                    when(it){
                        is ResultManagerWithState.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithState.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithState.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
                    }
                }
                delay(3000)
                coreManager.requestProcessWithState(
                    forceRefreshData = false,
                    taskForRefreshData = {
                        delay(1000)
                        null
                    },
                    taskForReturnData = {
                        delay(1000)
                        "TASK RESULT WITH STATE RETURN CACHING"
                    }
                ).collect{
                    when(it){
                        is ResultManagerWithState.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithState.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithState.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
                    }
                }

                coreManager.requestProcessWithState(
                    forceRefreshData = true,
                    taskForRefreshData = {
                        delay(1000)
                        null
                    },
                    taskForReturnData = {
                        delay(1000)
                        throw Throwable("TASK RESULT WITH STATE RETURN ERROR")
                    }
                ).collect{
                    when(it){
                        is ResultManagerWithState.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithState.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithState.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
                    }
                }
            }
        },
        requestManagerWithResult = {
            viewModelScope.launch {
                val result1=coreManager.requestProcessWithResult {
                    delay(1000)
                    "TASK RESULT WITH STATE AS STATE"
                }
                when(result1){
                    is ResultManager.Success -> Log.d("CoreManager",result1.result.orEmpty())
                    is ResultManager.Failed -> Log.d("CoreManager",result1.throwable.message.orEmpty())
                }
                delay(3000)
                val result2=coreManager.requestProcessWithResult (
                    forceRefreshData = true,
                    taskForRefreshData = {
                        delay(1000)
                        "TASK RESULT WITH STATE REFRESH AS STATE"
                    },
                    taskForReturnData = {
                        delay(1000)
                        "TASK RESULT WITH STATE RETURN CACHING AS STATE"
                    }
                )
                when(result2){
                    is ResultManager.Success -> Log.d("CoreManager",result2.result.orEmpty())
                    is ResultManager.Failed -> Log.d("CoreManager",result2.throwable.message.orEmpty())
                }
                delay(3000)
                val result3=coreManager.requestProcessWithResult (
                    forceRefreshData = false,
                    taskForRefreshData = {null},
                    taskForReturnData = {
                        delay(1000)
                        "TASK RESULT WITH STATE RETURN CACHING AS STATE"
                    }
                )
                when(result3){
                    is ResultManager.Success -> Log.d("CoreManager",result3.result.orEmpty())
                    is ResultManager.Failed -> Log.d("CoreManager",result3.throwable.message.orEmpty())
                }
                val result4=coreManager.requestProcessWithResult (
                    forceRefreshData = true,
                    taskForRefreshData = {null},
                    taskForReturnData = {
                        delay(1000)
                        throw Throwable("TASK RESULT WITH STATE RETURN ERROR AS STATE")
                    }
                )
                val file=File("")
                coreManager.activityManagerEvent(StartActivityManager.ShareFile(file))
                when(result4){
                    is ResultManager.Success -> Log.d("CoreManager",result4.result.orEmpty())
                    is ResultManager.Failed -> Log.d("CoreManager",result4.throwable.message.orEmpty())
                }
            }
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
    val goToSettings:()->Unit,val restartApp:()->Unit, val navigateTo:(route:String)->Unit,

    val getStringFromRes:(Int)->Unit,

    val requestReadExternalStoragePermission:()->Unit, val requestRecordAudioPermission:()->Unit,
    val requestCameraPermission:()->Unit, val requestCustomPermission:(String)->Unit,
    val requestCallPhonePermission:()->Unit,val requestLocationPermission:()->Unit,

    val pickImageFromGallery:()->Unit, val startActivityForResult:(Intent)->Unit,
    val startPhoneCall:(phone:String)->Unit,
    val goToSendEmail:(email:String,subject:String,body:String)->Unit, val startCustomIntent:(intent: Intent)->Unit,

    val requestManagerWithState:()->Unit, val requestManagerWithResult:()->Unit
    )