package com.yasser.coremanager.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

sealed class ResultManagerWithState<out T> {
    class Loading(val progress:Int):ResultManagerWithState<Nothing>()
    class Success<R>(val result:R):ResultManagerWithState<R>()
    class Failed(val throwable: Throwable):ResultManagerWithState<Nothing>()
}
sealed class ResultManager<out T>(){
    class Success<R>(val result:R):ResultManager<R>()
    class Failed(val throwable: Throwable):ResultManager<Nothing>()
}

