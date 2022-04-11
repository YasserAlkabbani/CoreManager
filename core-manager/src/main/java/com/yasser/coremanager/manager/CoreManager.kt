package com.yasser.coremanager.manager

import android.util.Log
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreManager @Inject constructor(){

    lateinit var composeManagerEvent:(ComposeManager)->Unit
    private set
    internal fun setComposeManagerEvent(newComposeManagerEvent:(ComposeManager)->Unit)
    {composeManagerEvent=newComposeManagerEvent}

    lateinit var permissionManagerEvent:(PermissionManager)->Unit
    private set
    internal fun setPermissionManagerEvent(newPermissionManagerEvent: (PermissionManager)->Unit)
    {permissionManagerEvent=newPermissionManagerEvent}

    lateinit var activityForResultManagerEvent:(ActivityForResultManager)->Unit
    private set
    internal fun setActivityForResultManagerEvent(newActivityForResultManagerEvent:(ActivityForResultManager)->Unit)
    {activityForResultManagerEvent=newActivityForResultManagerEvent}

    lateinit var activityManagerEvent:(StartActivityManager)->Unit
    private set
    internal fun setStartActivity(newStartActivity:(newStartActivityManager:StartActivityManager)->Unit){
        activityManagerEvent=newStartActivity
    }

    suspend fun <T>requestProcessWithState(
        taskForRefreshData:suspend ()->T?={null},
        forceRefreshData:Boolean=false,
        taskForReturnData:suspend ()->T?,
    ): Flow<ResultManagerWithState<T?>> {
        return flow<ResultManagerWithState<T?>> {
            val oldData=taskForReturnData()
            val freshData=if (forceRefreshData||oldData==null){
                taskForRefreshData();taskForReturnData()
            }else oldData
            ResultManager.Success(freshData)
            emit(ResultManagerWithState.Success(freshData))
        }.onStart { emit(ResultManagerWithState.Loading(0)) }
            .catch { emit(ResultManagerWithState.Failed(it)) }
            .flowOn(Dispatchers.IO)
    }
    suspend fun <T>requestProcessWithResult(
        taskForRefreshData:suspend ()->T?={null},
        forceRefreshData:Boolean=false,
        taskForReturnData:suspend ()->T?,
    ):ResultManager<T?>{
        return withContext(Dispatchers.IO){
            try {
                val oldData=taskForReturnData()
                val freshData=if (forceRefreshData||oldData==null){
                    taskForRefreshData();taskForReturnData()
                }else oldData
                ResultManager.Success(freshData)
            }catch (t:Throwable){
                ResultManager.Failed(t)
            }
        }
    }
}