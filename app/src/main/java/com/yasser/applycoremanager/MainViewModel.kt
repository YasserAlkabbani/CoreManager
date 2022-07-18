package com.yasser.applycoremanager

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasser.coremanager.manager.*
import com.yasser.coremanager.manager.ComposeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val coreManager:CoreManager):ViewModel() {

    val mainUIStateReadOnly:MainUIStateReadOnly=MainUIState().returnMainUIStateReadOnly(
        mainViewModelEvent = MainViewModelEvent(
            readExternalStoragePermission=::readExternalStoragePermission, recordAudioPermission=::recordAudioPermission,
            cameraPermission=::cameraPermission, callPhonePermission=::callPhonePermission,
            sendSMSPermission=::sendSMSPermission, readCallLogPermission=::readCallLogPermission,
            writeCallLogPermission=::writeCallLogPermission, readPhoneStatePermission=::readPhoneStatePermission,
            locationPermission=::locationPermission, customPermission=::customPermission,
            pickImageFromGalleryAndShare=::pickImageFromGalleryAndShare, captureImageByCameraAndOpen=::captureImageByCameraAndOpen,
            pickFilesAndShare=::pickFilesAndShare, customActivityForResult=::customActivityForResult,
            goToSettings=::goToSettings, restartApp=::restartApp, goToSendEmail=::goToSendEmail, startCallPhone=::startCallPhone,
            shareText=::shareText,openWebUrl=::openWebUrl, customIntent=::customIntent,
            pickDate=::pickDate, pickTime=::pickTime, showDialog=::showDialog, hideKeyBoard=::hideKeyBoard,
            nextFocus=::nextFocus, downFocus=::downFocus, popup=::popup, showToast=::showToast, navigation=::navigation,
            requestManagerWithResult=::requestManagerWithResult, requestManagerWithResultWithProgress=::requestManagerWithResultWithState
        )
    )

    private val showRequestPermissionRationale:()->Unit=
        {coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings)}
    private val taskToDoWhenPermissionDeclined:()->Unit=
        {coreManager.composeManagerEvent(ComposeManager.ShowToast("PERMISSION_REQUIRED".asTextManager()))}
    private val taskToDoWhenPermissionGranted:()->Unit=
        {coreManager.composeManagerEvent(ComposeManager.ShowToast("PERMISSION_GRANTED".asTextManager()))}

    ///// PERMISSION_MANAGER
    private fun readExternalStoragePermission(){
        coreManager.permissionManagerEvent(PermissionManager.ReadExternalStorage(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun recordAudioPermission(){
        coreManager.permissionManagerEvent(PermissionManager.RecordAudio(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun cameraPermission(){
        coreManager.permissionManagerEvent(PermissionManager.Camera(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun callPhonePermission(){
        coreManager.permissionManagerEvent(PermissionManager.CallPhone(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun sendSMSPermission(){
        coreManager.permissionManagerEvent(PermissionManager.SendSMSPermission(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun readCallLogPermission(){
        coreManager.permissionManagerEvent(PermissionManager.ReadCallLogPermission(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun writeCallLogPermission(){
        coreManager.permissionManagerEvent(PermissionManager.WriteCallLogPermission(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun readPhoneStatePermission(){
        coreManager.permissionManagerEvent(PermissionManager.ReadPhoneStatePermission(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun locationPermission(){
        coreManager.permissionManagerEvent(PermissionManager.LocationPermission(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }
    private fun customPermission(customPermission:String){
        coreManager.permissionManagerEvent(PermissionManager.CustomPermission(customPermission,
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined,
            showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = taskToDoWhenPermissionGranted
        ))
    }

    ///// START_ACTIVITY_FOR_RESULT_MANAGER
    private fun pickImageFromGalleryAndShare(){
        coreManager.startActivityForResultManagerEvent(
            StartActivityForResultManager.PickImageFromGallery{
                coreManager.startActivityManagerEvent(StartActivityManager.ShareFile(listOf(it()),BuildConfig.APPLICATION_ID))
            }
        )
    }
    private fun captureImageByCameraAndOpen(){
        coreManager.permissionManagerEvent(PermissionManager.Camera(
            taskToDoWhenPermissionDeclined=taskToDoWhenPermissionDeclined, showRequestPermissionRationale=showRequestPermissionRationale,
            taskToDoWhenPermissionGranted = {
                coreManager.startActivityForResultManagerEvent(
                    StartActivityForResultManager.CaptureImageByCamera(BuildConfig.APPLICATION_ID){
                        coreManager.startActivityManagerEvent(StartActivityManager.OpenFile(it(),BuildConfig.APPLICATION_ID))
                    }
                )
            }
        ))
    }
    private fun pickFilesAndShare(){
        coreManager.startActivityForResultManagerEvent(
            StartActivityForResultManager.PickFiles(true,true,true,true,true,true){
                coreManager.startActivityManagerEvent(StartActivityManager.ShareFile(it(),BuildConfig.APPLICATION_ID))
            }
        )
    }
    private fun customActivityForResult(intent: Intent){
        coreManager.startActivityForResultManagerEvent(StartActivityForResultManager.CustomIntent(intent) {

        })
    }

    ///// START_ACTIVITY_MANAGER
    private fun goToSettings(){coreManager.startActivityManagerEvent(StartActivityManager.GoToSettings)}
    private fun restartApp(){coreManager.startActivityManagerEvent(StartActivityManager.RestartApp)}
    private fun goToSendEmail(email:String,subject:String,body:String){
        coreManager.startActivityManagerEvent(StartActivityManager.GoToSendEmail(email,subject,body))
    }
    private fun startCallPhone(phoneNumber:String){
        coreManager.startActivityManagerEvent(StartActivityManager.StartCallPhone(phoneNumber))
    }
    private fun shareText(){coreManager.startActivityManagerEvent(StartActivityManager.ShareText("TEST_SHARE_TEXT___CORE_MANAGER"))}
    private fun openWebUrl(){coreManager.startActivityManagerEvent(StartActivityManager.OpenWebUrl("https://www.google.com"))}
    private fun customIntent(intent: Intent){coreManager.startActivityManagerEvent(StartActivityManager.CustomIntent(intent))}

    ///// DATE_TIME_MANAGER
    private fun pickDate(){
        coreManager.dateTimeManagerEvent(DateTimeManager.PickDate{mainUIStateReadOnly.mainUIEvent.updateSelectedData(it().getDate())})
    }
    private fun pickTime(){
        coreManager.dateTimeManagerEvent(DateTimeManager.PickTime{mainUIStateReadOnly.mainUIEvent.updateSelectedTime(it().getTime())})
    }

    ///// DIALOG_MANAGER
    private fun showDialog(){coreManager.dialogManagerEvent(DialogManager.Show {
        {
            Card() {
                Box(modifier = Modifier.padding(20.dp)) {
                    MainButton(text ="HIDE DIALOG"){
                        coreManager.dialogManagerEvent(DialogManager.Hide)
                    }
                }
            }
        }
    })}

    ///// COMPOSE_MANAGER
    private fun hideKeyBoard(){coreManager.composeManagerEvent(ComposeManager.HideKeyBoard)}
    private fun nextFocus(){coreManager.composeManagerEvent(ComposeManager.NextFocus)}
    private fun downFocus(){coreManager.composeManagerEvent(ComposeManager.DownFocus)}
    private fun popup(){coreManager.composeManagerEvent(ComposeManager.Popup)}
    private fun showToast(textManager: TextManager){coreManager.composeManagerEvent(ComposeManager.ShowToast(textManager))}
    private fun navigation(destinationManager: DestinationManager){coreManager.composeManagerEvent(ComposeManager.Navigation(destinationManager))}

    ///// REQUEST_MANAGER
    private suspend fun createFakeRequest(isSuccess:Boolean, delayAmount:Long): String {
        delay(delayAmount)
        if (!isSuccess) throw IllegalStateException("ERROR REQUEST")
        delay(delayAmount)
        return "SUCCESS REQUEST"
    }
    private fun requestManagerWithResult(){
        viewModelScope.launch {
            val successRequest=coreManager.requestProcessWithResult {
                createFakeRequest(true,2500)
            }
            when(successRequest){
                is ResultManager.Success -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_RESULT ${successRequest.result}")
                is ResultManager.Failed -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_RESULT ${successRequest.throwable.message}")
            }
            val errorRequest=coreManager.requestProcessWithResult {
                createFakeRequest(true,2500)
            }
            when(errorRequest){
                is ResultManager.Success -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_RESULT ${errorRequest.result}")
                is ResultManager.Failed -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_RESULT ${errorRequest.throwable.message}")
            }
        }
    }
    private fun requestManagerWithResultWithState(){
        viewModelScope.launch {
            coreManager.requestProcessWithState { createFakeRequest(true,2500) }.collect{
                when(it){
                    is ResultManagerWithProgress.Success -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_STATE ${it.result}")
                    is ResultManagerWithProgress.Loading -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_STATE $it")
                    is ResultManagerWithProgress.Failed -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_STATE ${it.throwable.message}")
                }
            }
            coreManager.requestProcessWithState { createFakeRequest(false,2500) }.collect{
                when(it){
                    is ResultManagerWithProgress.Success -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_STATE ${it.result}")
                    is ResultManagerWithProgress.Loading -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_STATE LOADING.. ")
                    is ResultManagerWithProgress.Failed -> Log.d("CoreManager","TEST_REQUEST_MANAGER_WITH_STATE ${it.throwable.message}")
                }
            }
        }
    }

}

class MainUIStateReadOnly(
    val textField1:StateFlow<String>, val textField2:StateFlow<String>,
    val selectedDate:StateFlow<String>, val selectedTime:StateFlow<String>,
    val mainUIEvent: MainUIEvent,
    val mainViewModelEvent:MainViewModelEvent
){

}
class MainUIState(
    private val textField1:MutableStateFlow<String> = MutableStateFlow(""),
    private val textField2:MutableStateFlow<String> = MutableStateFlow(""),
    private val selectedDate:MutableStateFlow<String> = MutableStateFlow("Pick Date"),
    private val selectedTime:MutableStateFlow<String> = MutableStateFlow("Pick Time"),
){
    fun returnMainUIStateReadOnly(mainViewModelEvent:MainViewModelEvent):MainUIStateReadOnly=
        MainUIStateReadOnly(
            textField1,textField2,selectedDate,selectedTime,
            MainUIEvent(
                updateText1 = ::updateText1,updateText2=::updateText2,
                updateSelectedTime = ::updateSelectedTime, updateSelectedData = ::updateSelectedData
            ),
            mainViewModelEvent=mainViewModelEvent
        )

    fun updateText1(newValue:String){textField1.update { newValue }}
    fun updateText2(newValue:String){textField2.update { newValue }}
    fun updateSelectedTime(newValue:String){selectedTime.update { newValue }}
    fun updateSelectedData(newValue:String){selectedDate.update { newValue }}
}
class MainUIEvent(
    val updateText1:(String)->Unit, val updateText2:(String)->Unit,
    val updateSelectedTime: (String)->Unit ,val updateSelectedData: (String)->Unit,
)
class MainViewModelEvent(
    ///// PERMISSION_MANAGER
    val readExternalStoragePermission:()->Unit, val recordAudioPermission:()->Unit, val cameraPermission:()->Unit,
    val callPhonePermission:()->Unit, val sendSMSPermission:()->Unit, val readCallLogPermission:()->Unit,
    val writeCallLogPermission:()->Unit, val readPhoneStatePermission:()->Unit, val locationPermission:()->Unit,
    val customPermission:(permission:String)->Unit,

    ///// START_ACTIVITY_FOR_RESULT_MANAGER
    val pickImageFromGalleryAndShare:()->Unit, val captureImageByCameraAndOpen:()->Unit,
    val pickFilesAndShare:()->Unit, val customActivityForResult:(Intent)->Unit,

    ///// START_ACTIVITY_MANAGER
    val goToSettings:()->Unit, val restartApp:()->Unit, val goToSendEmail:(email:String,subject:String,body:String)->Unit,
    val startCallPhone:(String)->Unit, val shareText:()->Unit,
    val openWebUrl:()->Unit, val customIntent:(Intent)->Unit,

    ///// DATE_TIME_MANAGER
    val pickDate:()->Unit, val pickTime:()->Unit,

    ///// DIALOG_MANAGER
    val showDialog:()->Unit,

    ///// COMPOSE_MANAGER
    val hideKeyBoard:()->Unit, val nextFocus:()->Unit, val downFocus:()->Unit,
    val popup:()->Unit, val showToast:(TextManager)->Unit, val navigation:(DestinationManager)->Unit,

    ///// REQUEST_MANAGER
    val requestManagerWithResult:()->Unit, val requestManagerWithResultWithProgress:()->Unit
)
