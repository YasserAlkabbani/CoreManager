package com.yasser.coremanager.manager

import java.text.SimpleDateFormat
import java.util.*

data class DateData(val timeStamp:Long){
    fun getDate():String=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timeStamp).orEmpty()
}
data class TimeData(val hour:Int,val minute:Int){
    fun getTime():String=Calendar.getInstance().apply {
            set(0,0,0,hour,minute,0)
        }.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.time) }
}
sealed class DateTimeManager {
    class PickDate(val selectedDate:(()->DateData)->Unit):DateTimeManager()
    class PickTime(val selectedTime:(()->TimeData)->Unit):DateTimeManager()
}