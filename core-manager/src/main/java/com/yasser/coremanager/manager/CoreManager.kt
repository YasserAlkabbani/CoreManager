package com.yasser.coremanager.manager

import android.util.Log
import com.yasser.coremanager.CoreActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreManager @Inject constructor(val navigationManager: NavigationManager){

    private var currentActivity:String?=null
    internal fun setCurrentActivity(newActivity:String){currentActivity=newActivity}

    lateinit var getCurrentActivity:()->CoreActivity?
    private set
    internal fun setGetCurrentActivity(newGetCurrentActivity:()->CoreActivity?){getCurrentActivity=newGetCurrentActivity}

    var composeManagerEvent:(ComposeManager)->Unit ={}
    private set
    internal fun setComposeManagerEvent(selectedActivity:String,newComposeManagerEvent:(ComposeManager)->Unit) {
        if (selectedActivity==currentActivity)composeManagerEvent=newComposeManagerEvent
    }

    var permissionManagerEvent:(PermissionManager)->Unit={}
    private set
    internal fun setPermissionManagerEvent(selectedActivity:String,newPermissionManagerEvent: (PermissionManager)->Unit) {
        if (selectedActivity==currentActivity)permissionManagerEvent=newPermissionManagerEvent
    }

    var activityForResultManagerEvent:(ActivityForResultManager)->Unit={}
    private set
    internal fun setActivityForResultManagerEvent(selectedActivity:String,newActivityForResultManagerEvent:(ActivityForResultManager)->Unit) {
        if (selectedActivity==currentActivity)activityForResultManagerEvent=newActivityForResultManagerEvent
    }

    var startActivityManagerEvent:(StartActivityManager)->Unit={}
    private set
    internal fun setStartActivityEvent(selectedActivity:String, newStartActivityManagerEvent:(newStartActivityManager:StartActivityManager)->Unit){
        if (selectedActivity==currentActivity)startActivityManagerEvent=newStartActivityManagerEvent
    }

    var dateTimeManagerEvent:(DateTimeManager)->Unit={}
    private set
    internal fun setDateTimePickerEvent(selectedActivity: String,newDateTimeManagerEvent:(DateTimeManager)->Unit){
        if (currentActivity==selectedActivity) dateTimeManagerEvent=newDateTimeManagerEvent
    }


    var dialogManagerEvent:(DialogManager)->Unit={}
    private set
    internal fun setDialogManager(selectedActivity: String,newDialogManagerEvent:(DialogManager)->Unit){
        if (currentActivity==selectedActivity)dialogManagerEvent=newDialogManagerEvent
    }

    var stringByRes:(stringRes:Int)->String ={""}
    internal fun setStringFromRes(selectedActivity:String, newStringByRes:(stringRes:Int)->String){
        if (selectedActivity==currentActivity) stringByRes=newStringByRes
    }

    suspend fun <T>requestProcessWithState(
        taskForRefreshData:suspend ()->Unit={}, forceRefreshData:Boolean=false, taskForReturnData:suspend ()->T?,
    ): Flow<ResultManagerWithProgress<T>> {
        return flow<ResultManagerWithProgress<T>> {
            val oldData=taskForReturnData()
            val freshData=if (forceRefreshData||oldData==null){
                taskForRefreshData()
                taskForReturnData()!!
            }else oldData
            ResultManager.Success(freshData)
            emit(ResultManagerWithProgress.Success(freshData))
        }.onStart { emit(ResultManagerWithProgress.Loading()) }
            .catch { emit(ResultManagerWithProgress.Failed(it)) }
            .flowOn(Dispatchers.IO)
    }
    suspend fun <T>requestProcessWithResult(
        taskForRefreshData:suspend ()->Unit={}, forceRefreshData:Boolean=false, taskForReturnData:suspend ()->T?,
    ):ResultManager<T>{
        return withContext(Dispatchers.IO){
            try {
                val oldData=taskForReturnData()
                val freshData=if (forceRefreshData||oldData==null){
                    taskForRefreshData()
                    taskForReturnData()!!
                }else oldData
                ResultManager.Success(freshData)
            }catch (t:Throwable){
                ResultManager.Failed(t)
            }
        }
    }
}