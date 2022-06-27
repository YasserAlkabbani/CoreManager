package com.yasser.coremanager.manager

import android.content.Intent
import androidx.activity.result.ActivityResult
import java.io.File

sealed class ActivityForResultManager {
    class PickImageFromGallery(val dataToReturn: (() -> File) -> Unit):ActivityForResultManager()
    class CaptureImageByCamera(val packageName:String, val dataToReturn: (() -> File) -> Unit):ActivityForResultManager()
    class PickFile(
        val image:Boolean,val audio:Boolean,val video:Boolean,val pdf:Boolean,val word:Boolean,val excel:Boolean,
        val dataToReturn:(()->List<File>)->Unit
    ):ActivityForResultManager()
    class CustomActivityForResult(val intent:Intent, val dataToReturn: (() -> ActivityResult) -> Unit):ActivityForResultManager()
}