package com.yasser.coremanager.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

sealed class ResultManagerWithState<out T> {
    class Loading(val progress:Int):ResultManagerWithState<Nothing>()
    class Success<R>(val result:R):ResultManagerWithState<R>()
    class Failed(val throwable: Throwable):ResultManagerWithState<Nothing>()
}
suspend fun <T>requestProcessWithState(
    taskForRefreshData:suspend ()->T?={null},
    forceRefreshData:Boolean=false,
    taskForReturnData:suspend ()->T?,
):Flow<ResultManagerWithState<T?>>{
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

sealed class ResultManager<out T>(){
    class Success<R>(val result:R):ResultManager<R>()
    class Failed(val throwable: Throwable):ResultManager<Nothing>()
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

