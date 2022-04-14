package com.yasser.coremanager.manager

import android.content.Context
import androidx.annotation.StringRes

sealed class TextManager {
    class StringText(val text:String):TextManager()
    class ResourceText(@StringRes val resource:Int):TextManager()

    fun getText(context: Context):String{
        return when(this){
            is StringText -> text
            is ResourceText -> context.getString(resource)
        }
    }
}