package com.yasser.core_manager.manager

import android.content.Context

sealed class TextManager {
    class StringText(val text:String):TextManager()
    class ResourceText(val resource:Int):TextManager()

    fun getText(context: Context):String{
        return when(this){
            is StringText -> text
            is ResourceText -> context.getString(resource)
        }
    }
}