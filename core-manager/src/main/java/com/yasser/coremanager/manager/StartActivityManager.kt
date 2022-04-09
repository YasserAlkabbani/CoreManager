package com.yasser.coremanager.manager

import android.content.Intent

sealed class StartActivityManager {
    object GoToSettings:StartActivityManager()
    class GoToSendEmail(val emailAddress:String,val subject:String,val body:String):StartActivityManager()
    class StartCallPhone(val phoneNumber:String):StartActivityManager()
    class CustomIntent(val intent:Intent):StartActivityManager()
}