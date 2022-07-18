package com.yasser.coremanager.manager

import android.content.Intent
import androidx.activity.result.ActivityResult
import java.io.File

sealed class StartActivityForResultManager {
    class PickImageFromGallery(val dataToReturn: (() -> File) -> Unit):StartActivityForResultManager()
    class CaptureImageByCamera(val packageName:String, val dataToReturn: (() -> File) -> Unit):StartActivityForResultManager()
    class PickFiles(
        val image:Boolean,val audio:Boolean,val video:Boolean,val pdf:Boolean,val word:Boolean,val excel:Boolean,
        val dataToReturn:(()->List<File>)->Unit
    ):StartActivityForResultManager()
    class CustomIntent(val intent:Intent, val dataToReturn: (() -> ActivityResult) -> Unit):StartActivityForResultManager()
}