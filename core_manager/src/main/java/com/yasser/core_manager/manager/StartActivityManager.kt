package com.yasser.core_manager.manager

import android.content.Intent

sealed class StartActivityManager {
    object GoToSettings:StartActivityManager()
    class GoToSendEmail(val emailAddress:String):StartActivityManager()
    class StartCallPhone(val phoneNumber:String):StartActivityManager()
    class CustomIntent(val intent:Intent):StartActivityManager()
}