package com.yasser.coremanager

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.yasser.coremanager.manager.*
import com.yasser.coremanager.manager.ComposeManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import java.io.File
import javax.inject.Inject

open class CoreActivity : AppCompatActivity() {

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

    override fun onDestroy() {
        super.onDestroy()

        Log.d("CoreManager", "VIEW_MODEL: CORE_ACTIVITY ON_DESTROY")

        coreManager.setComposeManagerEvent(this.toString()){}
        coreManager.setStartActivityEvent(this.toString()){}
        coreManager.setGetCurrentActivity (this.toString()){ null }
        coreManager.setPermissionManagerEvent(this.toString()){}
        coreManager.setActivityForResultManagerEvent(this.toString()){}
        coreManager.setDateTimePickerEvent(this.toString()){}
        coreManager.setStringFromRes(this.toString()){""}
        coreManager.setDialogManager(this.toString()){}
        coreManager.navigationManager.setNavHostController(null,this.toString())



    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("CoreManager", "VIEW_MODEL: CORE_ACTIVITY ON_CREATE")

        coreManager.setCurrentActivity(this.toString())
        coreManager.setComposeManagerEvent(this.toString()) { coreViewModel.setComposeManager(it) }
        coreManager.setGetCurrentActivity (this.toString()){ this }
        coreManager.setPermissionManagerEvent(this.toString()) {
            when(it){
                is PermissionManager.Camera -> checkPermission(
                    Manifest.permission.CAMERA,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.ReadExternalStorage ->checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.RecordAudio ->checkPermission(
                    Manifest.permission.RECORD_AUDIO,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.SendSMSPermission ->checkPermission(
                    Manifest.permission.SEND_SMS,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.CallPhone -> checkPermission(
                    Manifest.permission.CALL_PHONE,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
                is PermissionManager.ReadPhoneStatePermission -> {
                    checkPermission(
                        Manifest.permission.READ_PHONE_STATE,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                    )
                }
                is PermissionManager.ReadCallLogPermission -> {
                    checkPermission(
                        Manifest.permission.READ_CALL_LOG,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                    )
                }
                is PermissionManager.WriteCallLogPermission -> {
                    checkPermission(
                        Manifest.permission.WRITE_CALL_LOG,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                    )
                }
                is PermissionManager.LocationPermission -> {
                    checkPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                    )
                }
                is PermissionManager.CustomPermission ->checkPermission(
                    it.permission,it.taskToDoWhenPermissionGranted,it.taskToDoWhenPermissionDeclined,it.showRequestPermissionRationale
                )
            }
        }
        coreManager.setActivityForResultManagerEvent(this.toString()) {activityForResultManager->
            when(activityForResultManager){
                is ActivityForResultManager.PickImageFromGallery -> {
                    val intent:Intent = Intent().apply {
                        type = "image/*"
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
                is ActivityForResultManager.CaptureImageByCamera -> {
                    val imageFile= File.createTempFile(System.currentTimeMillis().toString(),".jpg")
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val photoURI: Uri = FileProvider.getUriForFile(this, "${activityForResultManager.packageName}.fileprovider", imageFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    getContent.launch(takePictureIntent)
                    processContentData={
                        val activityResult=it()
                        if (activityResult.resultCode== RESULT_OK){
                            activityForResultManager.dataToReturn{imageFile}
                        }
                    }
                }
                is ActivityForResultManager.CustomActivityForResult -> {
                    getContent.launch(activityForResultManager.intent)
                    processContentData={ activityForResultManager.dataToReturn{it()}}
                }
                is ActivityForResultManager.PickFile -> {
                    fun getFile(fileUri:Uri):File{
                        val fileData= application.contentResolver.query(fileUri, null, null, null, null)?.use {
                            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            it.moveToFirst()
                            it.getString(nameColumnIndex)
                        }
                        val fileName=fileData?.substringBeforeLast(".")
                        val fileExt=fileData?.substringAfterLast(".")

                        val file:File = File(application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$fileName.$fileExt")

                        application.contentResolver.openInputStream(fileUri)?.copyTo(file.outputStream())
                        return file
                    }

                    val pickFileIntent:Intent= Intent().apply {
                        val typeList= arrayOf(
                            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            "application/pdf", "image/*","audio/*"
                        )
                        action=Intent.ACTION_OPEN_DOCUMENT
                        type = "*/*"
                        putExtra(Intent.EXTRA_MIME_TYPES,typeList)
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }

                    getContent.launch(pickFileIntent)
                    processContentData={
                        val result=it()
                        if (result.resultCode == Activity.RESULT_OK){
                            result.data?.data?.let {
                                val file:File =getFile(it)
                                activityForResultManager.dataToReturn{ listOf(file)}
                            }?: run {
                                result.data?.clipData?.let {clipDate->
                                    buildList<File> {
                                        repeat(clipDate.itemCount){index->
                                            val file:File =getFile(clipDate.getItemAt(index).uri)
                                            this.add(file)
                                        }
                                    }.let {
                                        activityForResultManager.dataToReturn{it}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        coreManager.setStartActivityEvent(this.toString()) {
            when(it){
                StartActivityManager.GoToSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
                StartActivityManager.RestartApp -> {
                    val resetIntent:Intent=baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)!!
                    finishAffinity()
                    startActivity(resetIntent)
                }
                is StartActivityManager.StartCallPhone -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_CALL
                        data = Uri.parse("tel: ${it.phoneNumber}")
                    }
                    startActivity(intent)
                }
                is StartActivityManager.GoToSendEmail -> {
                    val intent = Intent().apply {
                        action=Intent.ACTION_VIEW
                        type = "*/*"
                        data=Uri.parse("mailto:"+it.emailAddress+"?subject="+ it.subject +"&body="+ it.body)
                    }
                    startActivity(intent)
                }
                is StartActivityManager.CustomIntent -> startActivity(it.intent)
                is StartActivityManager.ShareFile -> {
                    val fileExt= MimeTypeMap.getFileExtensionFromUrl(it.file.path)
                    val fileMime= MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
                    val fileUri= FileProvider.getUriForFile(this, "${it.packageName}.fileprovider", it.file)
                    val intent:Intent =Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        type=fileMime
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)// or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        clipData = ClipData.newRawUri("Open File", fileUri)
                    }
                    startActivity(Intent.createChooser(intent, "Share By"))
                }
                is StartActivityManager.OpenFile->{
                    val fileExt= MimeTypeMap.getFileExtensionFromUrl(it.file.path)
                    val fileMime= MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
                    val fileUri=FileProvider.getUriForFile(this,"${it.packageName}.fileprovider",it.file)
                    val intent = Intent().apply {
                        action=Intent.ACTION_VIEW
                        type=fileMime
                        data=fileUri
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        clipData = ClipData.newRawUri("Open File", fileUri)
                    }
                    startActivity(Intent.createChooser(intent,"Open By"))
//                    chooserIntent.resolveActivity(packageManager)?.let{startActivity(chooserIntent)}
                }
            }
        }
        coreManager.setDateTimePickerEvent(this.toString()){dateTimeManager->
            when(dateTimeManager){
                is DateTimeManager.PickDate -> {
                    val datePicker =
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select date")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build()
                    datePicker.show(supportFragmentManager, "Pick Date")
                    datePicker.addOnPositiveButtonClickListener { datePicker.dismiss();dateTimeManager.selectedDate {DateData(it)} }
                    datePicker.addOnNegativeButtonClickListener {datePicker.dismiss()}
                    datePicker.addOnCancelListener {datePicker.dismiss()}
                    datePicker.addOnDismissListener {datePicker.dismiss()}
                }
                is DateTimeManager.PickTime -> {
                    val timePicker =
                        MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(12)
                            .setMinute(0)
                            .setTitleText("Select time")
                            .build()
                    timePicker.show(supportFragmentManager,"Time Picker")
                    timePicker.addOnPositiveButtonClickListener {
                        timePicker.dismiss()
                        dateTimeManager.selectedTime {TimeData(timePicker.hour, timePicker.minute)}
                    }
                    timePicker.addOnNegativeButtonClickListener {timePicker.dismiss()}
                    timePicker.addOnCancelListener {timePicker.dismiss()}
                    timePicker.addOnDismissListener {timePicker.dismiss()}
                }
            }
        }
        coreManager.setDialogManager(this.toString()){ coreViewModel.setDialogManager(it) }
        coreManager.setStringFromRes(this.toString()){getString(it)}

    }

    @OptIn(ExperimentalComposeUiApi::class,ExperimentalMaterialApi::class)
    fun coreManagerContent(screenContent:@Composable ()->Unit){

        setContent {

            val coreViewModel:CoreViewModel = viewModel()
            val navHostController= rememberNavController()
            LaunchedEffect(key1 = navHostController, block = {
                coreManager.navigationManager.setNavHostController(navHostController,this@CoreActivity.toString())
            })

            val context= LocalContext.current
            val localSoftwareKeyboardController= LocalSoftwareKeyboardController.current
            val localFocusManager= LocalFocusManager.current
            val dialogManager=coreViewModel.dialogManager.collectAsState().value
            val hideDialog:()->Unit =coreViewModel::setHideDialog


            val currentDestinationState=
                coreManager.navigationManager.returnNavHostControllerForCurrentActivity(this@CoreActivity.toString())
                    .collectAsState(null).value?.currentBackStackEntryAsState()?.value

            currentDestinationState?.let {
                LaunchedEffect(key1 = currentDestinationState, block ={
                    withContext(Dispatchers.Default){
                        coreManager.navigationManager.destinationsManagerList.firstOrNull { destination->
                            val currentRoute=if (currentDestinationState.destination.route?.contains("/") == true)
                                currentDestinationState.destination.route?.substringBefore("/") else currentDestinationState.destination.route
                            destination.route==currentRoute
                        }?.let {
                            currentDestinationState.arguments?.getString(it.arg2Key)?.removePrefix("{")?.removeSuffix("}").let {label->
                                coreManager.navigationManager.setCurrentDestination(it.copy(label=(label?.asTextManager()?:it.label)))
                                }
                            }
                        }
                    }
                )
            }

            val modalBottomSheetState:ModalBottomSheetState= rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true,
                confirmStateChange = {
                    if (it ==ModalBottomSheetValue.Hidden)hideDialog()
                    true
                }
            )

            LaunchedEffect(coreViewModel){
                launch {
                    coreViewModel.composeManager.filterNot { it is ComposeManager.Idle }.collect {
                        when(it){
                            ComposeManager.Idle -> {}
                            ComposeManager.HideKeyBoard -> localSoftwareKeyboardController?.hide()
                            ComposeManager.NextFocus -> localFocusManager.moveFocus(FocusDirection.Next)
                            ComposeManager.DownFocus -> localFocusManager.moveFocus(FocusDirection.Down)
                            is ComposeManager.ShowToast -> Toast.makeText(context,it.textManager.getText(context), Toast.LENGTH_SHORT).show()
                        }
                        delay(200)
                        coreViewModel.setComposeManager(ComposeManager.Idle)
                    }
                }
                launch {
                    coreViewModel.dialogManager.collectLatest{
                        when(it){
                            DialogManager.Hide -> modalBottomSheetState.hide()
                            is DialogManager.Show -> modalBottomSheetState.show()
                        }
                    }
                }
            }

            ModalBottomSheetLayout(
                sheetState =modalBottomSheetState ,
                sheetContent = { dialogManager.dialogManagerContent()() },
                sheetBackgroundColor = Color.Transparent,
                content =screenContent,
                sheetElevation = 0.dp
            )
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CoreCompose(){

}