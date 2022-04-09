package com.yasser.coremanager.manager

import android.content.Intent
import androidx.activity.result.ActivityResult
import java.io.File

sealed class ActivityForResultManager {
    class PickImageFromGallery(val dataToReturn: (() -> File) -> Unit):ActivityForResultManager()
    class CustomActivityForResult(val intent:Intent, val dataToReturn: (() -> ActivityResult) -> Unit):ActivityForResultManager()
}