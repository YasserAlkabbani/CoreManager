package com.yasser.core_manager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yasser.core_manager.manager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import java.io.File
import javax.inject.Inject


open class CoreActivity : FragmentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) taskToDoWhenGranted() else taskToDoWhenNotDeclined()
        }
    private var taskToDoWhenGranted:()->Unit={}
    private var taskToDoWhenNotDeclined:()->Unit={}
    private var showRequestPermissionRationale:()->Unit={}
    private fun checkPermission(
        permission:String, taskToDoWhenGranted:()->Unit, taskToDoWhenNotDeclined:()->Unit, showRequestPermissionRationale:()->Unit
    ){
        this.taskToDoWhenGranted=taskToDoWhenGranted
        this.taskToDoWhenNotDeclined=taskToDoWhenNotDeclined
        this.showRequestPermissionRationale=showRequestPermissionRationale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(application, permission) == PackageManager.PERMISSION_GRANTED -> {
                    taskToDoWhenGranted()
                }
                shouldShowRequestPermissionRationale(permission) -> {
                  showRequestPermissionRationale()
                }
                else -> {
                    requestPermissionLauncher.launch(permission)
                }
            }
        }else{taskToDoWhenGranted()}
    }

    private var processContentData:(()-> ActivityResult)->Unit ={}
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode== RESULT_OK){
            processContentData { it }
        }
    }

    @Inject
    lateinit var coreManager: CoreManager
    private val coreViewModel:CoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreManager.setComposeManagerEvent {
            coreViewModel.setComposeManager(it)
        }
        coreManager.setPermissionManagerEvent {
            when(it){
                is PermissionManager.Camera -> checkPermission(
                    Manifest.permission.CAMERA,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.ReadExternalStorage ->checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.WriteExternalStorage ->checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.RecordAudio ->checkPermission(
                    Manifest.permission.RECORD_AUDIO,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.CallPhone -> checkPermission(
                    Manifest.permission.CALL_PHONE,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.CustomPermission ->checkPermission(
                    it.permission,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
            }
        }
        coreManager.setActivityForResultManagerEvent {activityForResultManager->
            when(activityForResultManager){
                is ActivityForResultManager.PickImageFromGallery -> {
                    val intent:Intent = Intent().apply {
                        type = "image/*";
                        action = Intent.ACTION_PICK
                    }
                    val intentChooser=Intent.createChooser(intent, "Select Picture")
                    getContent.launch(intentChooser)
                    processContentData={
                        val activityResult=it()
                        if (activityResult.resultCode== RESULT_OK){
                            activityResult.data?.let {
                                it.data?.let {uri->
                                    val imageFile= File.createTempFile(System.currentTimeMillis().toString(),".jpg")
                                    contentResolver.openInputStream(uri)?.copyTo(imageFile.outputStream())
                                    activityForResultManager.dataToReturn { imageFile }
                                }
                            }
                        }
                    }
                }
                is ActivityForResultManager.CustomActivityForResult -> getContent.launch(activityForResultManager.intent)
            }
        }
        coreManager.setStartActivity {
            when(it){
                StartActivityManager.GoToSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
                is StartActivityManager.StartCallPhone -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_CALL
                        data = Uri.parse("tel: ${it.phoneNumber}")
                    }
                    startActivity(intent)
                }
                is StartActivityManager.GoToSendEmail -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_EMAIL, it.emailAddress)
                        putExtra(Intent.EXTRA_SUBJECT, "Email Subject")
                    }
                    startActivity(intent)
                }
                is StartActivityManager.CustomIntent -> startActivity(it.intent)
            }
        }
        
    }
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun CoreManagerContent(navController: NavHostController, content:@Composable ()->Unit){
        val coreViewModel:CoreViewModel = viewModel()
        val context= LocalContext.current
        val localSoftwareKeyboardController= LocalSoftwareKeyboardController.current
        val localFocusManager= LocalFocusManager.current

        LaunchedEffect(coreViewModel){
            coreViewModel.composeManager.filterNot { it is ComposeManager.Idle }.collectLatest {
                when(it){
                    ComposeManager.Idle -> {}
                    ComposeManager.HideKeyBoard -> localSoftwareKeyboardController?.hide()
                    ComposeManager.NextFocus -> localFocusManager.moveFocus(FocusDirection.Next)
                    ComposeManager.Popup -> navController.popBackStack()
                    is ComposeManager.Navigate -> {Log.d("CoreManager","Navigate To ${it.route}");navController.navigate(it.route)}
                    is ComposeManager.ShowToast -> Toast.makeText(context,it.textManager.getText(context), Toast.LENGTH_SHORT).show()
                }
                delay(200)
                coreViewModel.setComposeManager(ComposeManager.Idle)
            }
        }

        content()
    }

    override fun onDestroy() {
        super.onDestroy()
        coreManager.setComposeManagerEvent {  }
        coreManager.setPermissionManagerEvent {  }
        coreManager.setActivityForResultManagerEvent {  }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CoreCompose(){

}
