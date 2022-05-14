package com.yasser.applycoremanager

import android.content.Intent
import android.util.Log
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val coreManager:CoreManager):ViewModel() {

    val mainUIEvent=MainUIEvent(
        popUp = {coreManager.navigationManager.popup()},
        hideKeyBoard = {coreManager.composeManagerEvent(ComposeManager.HideKeyBoard)},
        nextFocus = {coreManager.composeManagerEvent(ComposeManager.NextFocus)},
        downFocus = {coreManager.composeManagerEvent(ComposeManager.DownFocus)},
        showToast = { coreManager.composeManagerEvent(ComposeManager.ShowToast(it))},
        navigateTo = {
                destinationManager,arg1,arg2 ->
            coreManager.navigationManager.navigate(destinationManager,arg1,arg2)
        },
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
                    showRequestPermissionRationale = {coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings)},
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
                    showRequestPermissionRationale = {coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings)},
                    taskToDoWhenPermissionDeclined = {Log.d("TestPermission","taskToDoWhenPermissionDeclined")}
                )
            )
        },
        requestCallPhonePermission = {
            coreManager.permissionManagerEvent(PermissionManager.CallPhone(
                taskToDoWhenPermissionGranted = { coreManager.startActivityManagerEvent(StartActivityManager.StartCallPhone("+963966994266"))},
                showRequestPermissionRationale = { coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings) },
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(R.string.test_toast_resource.asTextManager()))}
             )
         )
        },

        goToSettings ={coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings)},
        restartApp = {coreManager.startActivityManagerEvent(StartActivityManager.RestartApp)},
        getStringFromRes = {Log.d("TestCoreManager", coreManager.stringByRes(it))},
        goToSendEmail = {address,subject,body->coreManager.startActivityManagerEvent(StartActivityManager.GoToSendEmail(address,subject,body))},
        startCustomIntent = {coreManager.startActivityManagerEvent(StartActivityManager.CustomIntent(it))},

        startPhoneCall = {phoneNumber->
            coreManager.permissionManagerEvent(PermissionManager.CallPhone(
                taskToDoWhenPermissionGranted = {coreManager.startActivityManagerEvent(StartActivityManager.StartCallPhone(phoneNumber))},
                showRequestPermissionRationale = {coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings)},
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))},
                )
            )
        },
        pickImageFromGallery ={
            coreManager.permissionManagerEvent(PermissionManager.ReadExternalStorage(
                taskToDoWhenPermissionGranted = { coreManager.activityForResultManagerEvent(ActivityForResultManager.PickImageFromGallery { val imageFile=it()}) },
                showRequestPermissionRationale = { coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings) },
                taskToDoWhenPermissionDeclined = {coreManager.composeManagerEvent(ComposeManager.ShowToast(TextManager.ResourceText(R.string.test_toast_resource)))}
            ))
        } ,
        imageCaptureAndShare = {
          coreManager.permissionManagerEvent(PermissionManager.Camera(
              showRequestPermissionRationale ={} ,
              taskToDoWhenPermissionDeclined = {},
              taskToDoWhenPermissionGranted = {
                  coreManager.activityForResultManagerEvent(ActivityForResultManager.CaptureImageByCamera(BuildConfig.APPLICATION_ID){
                      coreManager.startActivityManagerEvent(StartActivityManager.ShareFile(it(),BuildConfig.APPLICATION_ID))
                  })
              }
          ))
        },
        imageCaptureAndOpen = {
            coreManager.permissionManagerEvent(PermissionManager.Camera(
                showRequestPermissionRationale ={} ,
                taskToDoWhenPermissionDeclined = {},
                taskToDoWhenPermissionGranted = {
                    coreManager.activityForResultManagerEvent(ActivityForResultManager.CaptureImageByCamera(BuildConfig.APPLICATION_ID){
                        coreManager.startActivityManagerEvent(StartActivityManager.OpenFile(it(),BuildConfig.APPLICATION_ID ))
                    })
                }
            ))
        },
        startActivityForResult ={intent->
            coreManager.permissionManagerEvent(PermissionManager.ReadExternalStorage(
                taskToDoWhenPermissionGranted = { coreManager.activityForResultManagerEvent(ActivityForResultManager.PickImageFromGallery { val imageFile=it()}) },
                showRequestPermissionRationale = { coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings) },
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
                        is ResultManagerWithProgress.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithProgress.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithProgress.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
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
                        is ResultManagerWithProgress.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithProgress.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithProgress.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
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
                        is ResultManagerWithProgress.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithProgress.Success -> Log.d("CoreManager",it.result)
                        is ResultManagerWithProgress.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
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
                        is ResultManagerWithProgress.Loading -> Log.d("CoreManager","Loading")
                        is ResultManagerWithProgress.Success -> Log.d("CoreManager",it.result.orEmpty())
                        is ResultManagerWithProgress.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
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
                when(result4){
                    is ResultManager.Success -> Log.d("CoreManager",result4.result)
                    is ResultManager.Failed -> Log.d("CoreManager",result4.throwable.message.orEmpty())
                }
            }
        },

        setText1 = {newText->_mainUIState.update { it.copy(textField1 =newText ) }},
        setText2 = {newText->_mainUIState.update { it.copy(textField2 =newText ) }},
        setText3 = {newText->_mainUIState.update { it.copy(textField3 =newText ) }},
        pickDate = {coreManager.dateTimeManagerEvent(DateTimeManager.PickDate { dateData ->
                _mainUIState.update { it.copy(selectedDate = dateData().getDate()) }
            })
        },
        pickTime = {coreManager.dateTimeManagerEvent(DateTimeManager.PickTime{timeData->
                _mainUIState.update { it.copy(selectedTime = timeData().getTime()) }
            })
        },
        showDialog = {coreManager.dialogManagerEvent(DialogManager.Show {
            { Button(onClick = { coreManager.dialogManagerEvent(DialogManager.Hide) }) { Text(text = "Hide Dialog") } }
        })},
        hideDialog = {coreManager.dialogManagerEvent(DialogManager.Hide)}
    )
    private val _mainUIState:MutableStateFlow<MainUIState> = MutableStateFlow(MainUIState())
    val mainUIState:StateFlow<MainUIState> =_mainUIState
}

data class MainUIState(
    val textField1:String="",val textField2:String="",val textField3:String="",
    val selectedDate:String="Pick Date",val selectedTime:String="Pick Time"
)
data class MainUIEvent(
    val setText1:(String)->Unit, val setText2:(String)->Unit, val setText3:(String)->Unit,

    val hideKeyBoard:()->Unit, val nextFocus:()->Unit, val downFocus:()->Unit, val popUp:()->Unit, val showToast:(TextManager)->Unit,
    val goToSettings:()->Unit, val restartApp:()->Unit, val navigateTo:(DestinationManager,arg1:String?,arg2:String?)->Unit,

    val getStringFromRes:(Int)->Unit,

    val requestReadExternalStoragePermission:()->Unit, val requestRecordAudioPermission:()->Unit,
    val requestCameraPermission:()->Unit, val requestCustomPermission:(String)->Unit,
    val requestCallPhonePermission:()->Unit, val requestLocationPermission:()->Unit,

    val pickImageFromGallery:()->Unit,
    val imageCaptureAndShare:()->Unit,val imageCaptureAndOpen:()->Unit,
    val startActivityForResult:(Intent)->Unit,
    val startPhoneCall:(phone:String)->Unit,
    val goToSendEmail:(email:String,subject:String,body:String)->Unit, val startCustomIntent:(intent: Intent)->Unit,

    val requestManagerWithState:()->Unit, val requestManagerWithResult:()->Unit,

    val pickDate:()->Unit, val pickTime:()->Unit,

    val showDialog:()->Unit, val hideDialog:()->Unit
)