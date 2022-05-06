package com.yasser.coremanager.manager

import android.content.Intent
import java.io.File

sealed class StartActivityManager {
    object GoToSettings:StartActivityManager()
    object RestartApp:StartActivityManager()
    class GoToSendEmail(val emailAddress:String,val subject:String,val body:String):StartActivityManager()
    class StartCallPhone(val phoneNumber:String):StartActivityManager()
    class ShareFile(val file:File,val packageName:String):StartActivityManager()
    class OpenFile(val file:File,val packageName:String):StartActivityManager()
    class CustomIntent(val intent:Intent):StartActivityManager()
}