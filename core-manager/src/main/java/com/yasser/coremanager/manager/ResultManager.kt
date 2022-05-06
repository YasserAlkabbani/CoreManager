package com.yasser.coremanager.manager

sealed class ResultManagerWithProgress<out T>{
    class Loading(val progress:Int=0):ResultManagerWithProgress<Nothing>()
    class Success<R>(val result:R):ResultManagerWithProgress<R>()
    class Failed(val throwable: Throwable):ResultManagerWithProgress<Nothing>()
}
sealed class ResultManager<out T>{
    class Success<R>(val result:R):ResultManager<R>()
    class Failed(val throwable: Throwable):ResultManager<Nothing>()
}

