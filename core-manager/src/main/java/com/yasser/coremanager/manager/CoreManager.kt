package com.yasser.coremanager.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreManager @Inject constructor(){

    var currentActivity:String?=null
    internal fun setCurrentActivity(newActivity:String){currentActivity=newActivity}

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

    var activityManagerEvent:(StartActivityManager)->Unit={}
    private set
    internal fun setStartActivity(selectedActivity:String,newStartActivity:(newStartActivityManager:StartActivityManager)->Unit){
        if (selectedActivity==currentActivity)activityManagerEvent=newStartActivity
    }

    var stringByRes:(stringRes:Int)->String ={""}
    internal fun setStringFromRes(selectedActivity:String, newStringByRes:(stringRes:Int)->String){
        if (selectedActivity==currentActivity) stringByRes=newStringByRes
    }

    suspend fun <T>requestProcessWithState(
        taskForRefreshData:suspend ()->Unit={}, forceRefreshData:Boolean=false, taskForReturnData:suspend ()->T?,
    ): Flow<ResultManagerWithState<T>> {
        return flow<ResultManagerWithState<T>> {
            val oldData=taskForReturnData()
            val freshData=if (forceRefreshData||oldData==null){
                taskForRefreshData()
                taskForReturnData()!!
            }else oldData
            ResultManager.Success(freshData)
            emit(ResultManagerWithState.Success(freshData))
        }.onStart { emit(ResultManagerWithState.Loading()) }
            .catch { emit(ResultManagerWithState.Failed(it)) }
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