package com.yasser.applycoremanager

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import com.yasser.applycoremanager.ui.theme.ApplyCoreManagerTheme
import com.yasser.coremanager.CoreActivity
import com.yasser.coremanager.manager.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : CoreActivity() {
    
    private val mainViewModel:MainViewModel by viewModels()


    var cameraImageFile:File?=null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {cameraCapture()}
            else {/* SHOW MESSAGE */}
        }
    fun getFile(fileUri:Uri): File {
        val fileData= application.contentResolver.query(fileUri, null, null, null, null)?.use {
            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameColumnIndex)
        }
        val fileName=fileData?.substringBeforeLast(".")
        val fileExt=fileData?.substringAfterLast(".")

        val file: File = File(application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$fileName.$fileExt")

        application.contentResolver.openInputStream(fileUri)?.copyTo(file.outputStream())
        return file
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        ///// PICK_FILES
        if(it.resultCode== RESULT_OK){
            val result=it
            if (result.resultCode == Activity.RESULT_OK){
                result.data?.data?.let {
                    val file: File =getFile(it)
                    mainViewModel.shareFiles(listOf(file))
                }?: run {
                    result.data?.clipData?.let {clipDate->
                        buildList<File> {
                            repeat(clipDate.itemCount){index->
                                val file: File =getFile(clipDate.getItemAt(index).uri)
                                this.add(file)
                            }
                        }.let {files->
                          mainViewModel.shareFiles(files)
                        }
                    }
                }
            }
        }

        ///// CAMERA_CAPTURE
        if (it.resultCode== RESULT_OK){
            cameraImageFile?.let {file->
                mainViewModel.selectCameraImage(file)
            }
        }
    }

    fun shareFiles(files:List<File>){
        val fileMimeList= files
            .map {it.path.substringAfterLast(".")}
            .mapNotNull{ MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)}
            .joinToString("|","","")

        val filesUri:ArrayList<Uri> = arrayListOf()
        files.forEach { file->
            filesUri.add(FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", file))
        }

        val clipData: ClipData = ClipData.newRawUri("Open File", filesUri.first()).apply {
            filesUri.forEach {addItem(ClipData.Item(it))}
        }

        val intent:Intent =Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type=fileMimeList
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            this.clipData = clipData
        }
        startActivity(Intent.createChooser(intent, "Share By"))
    }

    fun pickFiles(
        image:Boolean, audio:Boolean, video:Boolean, pdf:Boolean, excel:Boolean, word:Boolean,
    ){
        val pickFileIntent:Intent= Intent().apply {
            val typeList= buildList {
                if (image){add("image/*")}
                if (audio){add("audio/*")}
                if (video){add("video/*")}
                if (pdf){add("application/pdf")}
                if (excel){
                    addAll(listOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                }
                if (word){
                    addAll(listOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                }
            }.toTypedArray()
            action=Intent.ACTION_OPEN_DOCUMENT
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES,typeList)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        getContent.launch(pickFileIntent)

    }

    private fun checkCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(application, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                    cameraCapture()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }else{cameraCapture()}
    }

    fun cameraCapture(){
        cameraImageFile= File.createTempFile(System.currentTimeMillis().toString(),".jpg")
        cameraImageFile?.let {file->
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoURI: Uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            getContent.launch(takePictureIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                launch {
                    mainViewModel.cameraCapture.filter { it }.onEach { mainViewModel.doneCameraCapture() }.collect{
                        checkCameraPermission()
                    }
                }
                launch {
                    mainViewModel.pickFiles.filter { it }.onEach { mainViewModel.donePickFiles() }.collect{
                        pickFiles(true,true,true,true,true,true)
                    }
                }
                launch {
                    mainViewModel.shareFile.filter { it.isNotEmpty() }.onEach { mainViewModel.doneShareFiles() }.collect{
                        shareFiles(it)
                    }
                }
            }
        }

        coreManagerContent{
            ApplyCoreManagerTheme{
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.systemBarsPadding(),
                            content = { Text(text = "TOOL_BAR")}
                        )},
                    content = {
                        it.calculateBottomPadding()
                        it.calculateTopPadding()
//
                        Surface(
                            modifier = Modifier.imePadding(),
                            color = MaterialTheme.colors.background
                        ) {
                            mainViewModel.coreManager.navigationManager.returnNavHostControllerForCurrentActivity(this@MainActivity.toString())
                                .collectAsState(null).value?.let {navHostController->
                                    NavHost(
                                        navController =navHostController,
                                        startDestination = mainViewModel.coreManager.navigationManager.startDestination.route ){
                                        mainViewModel.coreManager.navigationManager.getNavHostComposableContent(this)
                                    }
                                }
                        }
                    },
                    /*bottomBar = {
                        BottomNavigation(
                            modifier = Modifier,
                            content = {
                                BottomNavigationItem(
                                    selected = true, onClick = { },
                                    icon = { Icon(imageVector = Icons.Default.Create, contentDescription =null ) }
                                )
                                BottomNavigationItem(
                                    selected = true, onClick = { },
                                    icon = { Icon(imageVector = Icons.Default.Create, contentDescription =null ) }
                                )
                                BottomNavigationItem(
                                    selected = true, onClick = { },
                                    icon = { Icon(imageVector = Icons.Default.Create, contentDescription =null ) }
                                )
                            }
                        )
                    }*/
                )

            }
        }

    }
}

@Composable
fun MainCompose(){

//    val context= LocalContext.current
//    val navController = rememberNavController()
//    val localSoftwareKeyboardController= LocalSoftwareKeyboardController.current
//    val localFocusManager= LocalFocusManager.current

    val mainViewModel:MainViewModel= hiltViewModel()
    val mainUIStateReadOnly=mainViewModel.mainUIStateReadOnly
    val textField1=mainUIStateReadOnly.textField1.collectAsState().value
    val textField2=mainUIStateReadOnly.textField2.collectAsState().value
    val selectedDate=mainUIStateReadOnly.selectedDate.collectAsState().value
    val selectedTime=mainUIStateReadOnly.selectedTime.collectAsState().value
    val mainUIEvent=mainUIStateReadOnly.mainUIEvent
    val mainViewModelEvent=mainUIStateReadOnly.mainViewModelEvent

    LazyColumn(
        content = {

            ///TextField
            item { TextField(text =textField1 , onTextChange =mainUIEvent.updateText1 ) }
            item { TextField(text =textField2 , onTextChange =mainUIEvent.updateText2 ) }

            ///// COMPOSE_MANAGER
            item { MainButton(text ="Hide KeyBoard" , onClick = mainViewModelEvent.hideKeyBoard) }
            item { MainButton(text ="Next Focus" , onClick = mainViewModelEvent.nextFocus) }
//            item { MainButton(text ="Down Focus" , onClick = mainViewModelEvent.downFocus) }
            item { MainButton(text ="Show Toast" , onClick = {mainViewModelEvent.showToast("TEST TOAST".asTextManager())}) }
            item { MainButton(text="Navigate To Greeting 1",onClick = {mainViewModelEvent.navigation(ApplyCoreDestinationsManager().greeting1)}) }

            ///// PERMISSION_MANAGER
            item { MainButton(text="Read External Storage Permission", onClick = mainViewModelEvent.readExternalStoragePermission) }
            item { MainButton(text="Record Audio Permission", onClick = mainViewModelEvent.recordAudioPermission) }
            item { MainButton(text="Camera Permission", onClick = mainViewModelEvent.cameraPermission) }
            item { MainButton(text="Call Phone Permission", onClick = mainViewModelEvent.callPhonePermission) }
            item { MainButton(text="Send SMS Permission", onClick = mainViewModelEvent.sendSMSPermission) }
            item { MainButton(text="Read Call Log Permission", onClick = mainViewModelEvent.readCallLogPermission) }
            item { MainButton(text="Location Permission", onClick = mainViewModelEvent.locationPermission) }
            item { MainButton(text="Custom Permission (LOCATION)", onClick = {mainViewModelEvent.customPermission(Manifest.permission.ACCESS_FINE_LOCATION)}) }

            ///// START_ACTIVITY_FOR_RESULT_MANAGER
            item { MainButton(text = "Pick Image From Gallery And Share", onClick = mainViewModelEvent.pickImageFromGalleryAndShare) }
            item { MainButton(text = "Capture Image By Camera And Open", onClick = mainViewModelEvent.captureImageByCameraAndOpen) }
            item { MainButton(text = "Pick Files And Share", onClick = mainViewModelEvent.pickFilesAndShare) }
            item { MainButton(text = "Custom Activity For Result (PICK IMAGE)", onClick = {mainViewModelEvent.customActivityForResult(
                Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_PICK
                }
            )})}

            ///// START_ACTIVITY_MANAGER
            item { MainButton(text = "Go To Settings", onClick = mainViewModelEvent.goToSettings) }
            item { MainButton(text = "Restart App", onClick = mainViewModelEvent.restartApp) }
            item { MainButton(text = "Go To Send Email", onClick = {mainViewModelEvent.goToSendEmail("yasser@Yasser.com","EMAIL_SUBJECT","EMAIL_BODY")}) }
            item { MainButton(text = "Start Call Phone", onClick = {mainViewModelEvent.startCallPhone("+963930345510")}) }
            item { MainButton(text = "Share Text", onClick = mainViewModelEvent.shareText) }
            item { MainButton(text = "Open Web Url", onClick = mainViewModelEvent.openWebUrl) }
            item { MainButton(text = "Custom Intent (CREATE CALL)", onClick = {mainViewModelEvent.customIntent(
                Intent().apply {
                    action = Intent.ACTION_CALL
                    data = Uri.parse("tel:+963930345510")
                }
            )})}

            ///// DATE_TIME_MANAGER
            item { MainButton(text=selectedDate, onClick = mainViewModelEvent.pickDate) }
            item { MainButton(text=selectedTime, onClick = mainViewModelEvent.pickTime) }

            ///// DIALOG_MANAGER
            item { MainButton(text="showDialog", onClick = mainViewModelEvent.showDialog) }

            ///// REQUEST_MANAGER
            item { MainButton(text="Request With State",onClick = {mainViewModelEvent.requestManagerWithResultWithProgress()}) }
            item { MainButton(text ="Request With Result",onClick = {mainViewModelEvent.requestManagerWithResult()})}

        }
    )
}

@Composable
fun TextField(text:String,onTextChange:(String)->Unit){
    OutlinedTextField(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),value =text , onValueChange =onTextChange )
}
@Composable
fun MainButton(text:String,onClick:()->Unit){
    Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),onClick = onClick) { Text(text = text) }
}

@Composable
fun Greeting1(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    val mainViewModelEvent=mainViewModel.mainUIStateReadOnly.mainViewModelEvent
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        MainButton(text ="Popup" , onClick = mainViewModelEvent.popup)
        MainButton(text="Navigate From Greeting 1 To Greeting 2 $name",onClick = {mainViewModelEvent.navigation(ApplyCoreDestinationsManager().greeting2)})
    }
}

@Composable
fun Greeting2(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    val mainViewModelEvent=mainViewModel.mainUIStateReadOnly.mainViewModelEvent
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        MainButton(text ="Popup" , onClick = mainViewModelEvent.popup)
        MainButton(text="Navigate From Greeting 2 To Greeting 3 $name",onClick = {mainViewModelEvent.navigation(ApplyCoreDestinationsManager().greeting3)})
    }
}

@Composable
fun Greeting3(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    val mainViewModelEvent=mainViewModel.mainUIStateReadOnly.mainViewModelEvent
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        MainButton(text ="Popup" , onClick = mainViewModelEvent.popup)
        MainButton(text="Navigate From Greeting 3 To Greeting 4 $name",onClick = {mainViewModelEvent.navigation(ApplyCoreDestinationsManager().greeting4)})
    }
}
@Composable
fun Greeting4(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    val mainViewModelEvent=mainViewModel.mainUIStateReadOnly.mainViewModelEvent
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        MainButton(text ="Popup" , onClick = mainViewModelEvent.popup)
        MainButton(text="Greeting 4 $name",onClick = {})
    }
}
@Composable
fun Greeting5() {
    val mainViewModel:MainViewModel= hiltViewModel()
    val text= remember { mutableStateOf("")}
    Column(modifier = Modifier.fillMaxSize()) {
        
        val listItem= buildList {
            repeat(500){ add("ITEM_ $it _ $it _ $it") }
        }
        val bottomPadding=with(LocalDensity.current) { WindowInsets.ime.getBottom(LocalDensity.current).toDp() }
        val lazyColumnState= rememberLazyListState()

        LazyColumn(
            state =lazyColumnState ,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            content ={ items(listItem){it-> Text(text = it) } }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text.value, onValueChange = {text.value=it}
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Singleton @Provides
    fun provideNavigationManager():NavigationManager= NavigationManager(
        destinationsManagerList =ApplyCoreDestinationsManager().getAllDestination() ,
        startDestination =ApplyCoreDestinationsManager().getStartDestination() ,
        bottomNavigationDestinationList = ApplyCoreDestinationsManager().getBottomNavigationDestination() ,
    )
}

data class ApplyCoreDestinationsManager(
    val mainCompose:DestinationManager=DestinationManager(
        "MainCompose".asTextManager(),"main_compose",R.drawable.icon_android,null,null,
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { MainCompose() } }
    ),
    val greeting1:DestinationManager=DestinationManager(
        "Greeting1".asTextManager(),"greeting_1",R.drawable.icon_android,null,null,
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting1("Greeting1") } }
    ),
    val greeting2:DestinationManager=DestinationManager(
        "Greeting2".asTextManager(),"greeting_2",R.drawable.icon_android,null,null,
        true,true,true,true, {
           val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={ popUpTo("greeting_1"){inclusive=true} }
            navOptionsBuilderExt
        }, { { Greeting2("Greeting2") } }
    ),
    val greeting3:DestinationManager=DestinationManager(
        "Greeting3".asTextManager(),"greeting_3",R.drawable.icon_android,"G_3_1","G_3_2",
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting3("Greeting3") } }
    ),
    val greeting4:DestinationManager=DestinationManager(
        "Greeting4".asTextManager(),"greeting_4",R.drawable.icon_android,null,"G_4_2",
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={ popUpTo("greeting_3"){inclusive=true} }
            navOptionsBuilderExt
        }, { { Greeting4("Greeting4") } }
    ),
    val greeting5:DestinationManager=DestinationManager(
        "Greeting5".asTextManager(),"greeting_5",R.drawable.icon_android,null,null,
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={
                popUpTo("greeting_4"){inclusive=true}
            }
            navOptionsBuilderExt
        }, { { Greeting5()} }
    )
){
    fun getStartDestination()=mainCompose
    fun getAllDestination()= listOf(mainCompose,greeting1,greeting2,greeting3,greeting4,greeting5)
    fun getHomeDestanition()= listOf(greeting1 ,greeting2, greeting3)
    fun getBottomNavigationDestination()= listOf(greeting4 ,greeting5)
}