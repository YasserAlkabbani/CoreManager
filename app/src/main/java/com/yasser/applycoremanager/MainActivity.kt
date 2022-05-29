package com.yasser.applycoremanager

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : CoreActivity() {

    val mainViewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreManagerContent{
            ApplyCoreManagerTheme{
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navHostController=mainViewModel.coreManager.navigationManager.navHostController.collectAsState().value
                    navHostController?.let {
                        NavHost(
                            navController =navHostController,
                            startDestination = mainViewModel.coreManager.navigationManager.startDestination.route ){
                            mainViewModel.coreManager.navigationManager.getNavHostComposableContent(this)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainCompose(){

//    val context= LocalContext.current
//    val navController = rememberNavController()
//    val localSoftwareKeyboardController= LocalSoftwareKeyboardController.current
//    val localFocusManager= LocalFocusManager.current

    val mainViewModel:MainViewModel= hiltViewModel()

    val mainUIEvent=mainViewModel.mainUIEvent
    val mainUIState=mainViewModel.mainUIState.collectAsState().value
    val currentDestination=mainUIState.navigationManager.currentDestination.collectAsState().value

    LaunchedEffect(currentDestination){
            Log.d("CoreManager","CurrentDetonation MainActivity ${currentDestination.route}")
    }

    LazyColumn(
        content = {

            ///TextField
            item { OutlinedTextField(value =mainUIState.textField1 , onValueChange =mainUIEvent.setText1 ) }
            item { OutlinedTextField(value =mainUIState.textField2 , onValueChange =mainUIEvent.setText2 ) }
            item { OutlinedTextField(value =mainUIState.textField3 , onValueChange =mainUIEvent.setText3 ) }

            /// Compose Manager
            item { Button(onClick = {mainUIEvent.hideKeyBoard()}) { Text(text = "Hide KeyBoard") }}
            item { Button(onClick = {mainUIEvent.nextFocus()}) { Text(text = "Next Focus") }}
            item { Button(onClick = {mainUIEvent.downFocus()}) { Text(text = "Down Focus") }}
            item { Button(onClick = {mainUIEvent.popUp()}) { Text(text = "Popup") }}
            item { Button(onClick = {mainUIEvent.showToast("Test String Toast".asTextManager())}) { Text(text = "Show String Toast") }}
            item { Button(onClick = {mainUIEvent.showToast(R.string.test_toast_resource.asTextManager())}) { Text(text = "Show Resource Toast") }}

            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting1,"12345","56698")}) { Text(text = "Navigate To Greeting 1") }}
            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting2,"54321","98745")}) { Text(text = "Navigate To Greeting 2") }}
            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting3,"67890","67890")}) { Text(text = "Navigate To Greeting 3") }}
            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting4,"12345","12345")}) { Text(text = "Navigate To Greeting 4") }}
            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting5,null,null)}) { Text(text = "Navigate To Greeting 5") }}

            /// Permission Manager
            item { Button(onClick = {mainUIEvent.requestReadExternalStoragePermission()}) { Text(text = "Read Storage Permission") }}
            item { Button(onClick = {mainUIEvent.requestRecordAudioPermission()}) { Text(text = "Record Audio Permission") }}
            item { Button(onClick = {mainUIEvent.requestCameraPermission()}) { Text(text = "Request Camera Permission") }}
            item { Button(onClick = {mainUIEvent.requestCallPhonePermission()}) { Text(text = "Request Call Phone Permission") }}
            item { Button(onClick = {mainUIEvent.requestLocationPermission()}) { Text(text = "Request Location Permission") }}
            item { Button(onClick = {mainUIEvent.requestCustomPermission(Manifest.permission.ACCESS_COARSE_LOCATION)}) { Text(text = "Request Custom Permission") }}

            /// Start Activity
            item { Button(onClick = {mainUIEvent.goToSendEmail("yasser@Yasser.com","EMAIL_SUBJECT","EMAIL_BODY")}) { Text(text = "Send Email") }}
            item { Button(onClick = {mainUIEvent.startPhoneCall("+963930345510")}) { Text(text = "Start Call Phone") }}
            item { Button(onClick = {mainUIEvent.goToSettings()}) { Text(text = "Go To Settings") }}
            item { Button(onClick = {mainUIEvent.restartApp()}) { Text(text = "Restart App") }}
            item { Button(onClick = {mainUIEvent.getStringFromRes(R.string.test_toast_resource)}) { Text(text = "String From Res") }}
            item { Button(onClick = {mainUIEvent.startCustomIntent(Intent().apply { action = Intent.ACTION_DIAL;data = Uri.parse("tel: +963966994266") })}) { Text(text = "Start Call Phone") }}

            /// Start Activity For Result
            item { Button(onClick = {mainUIEvent.pickImageFromGallery()}) { Text(text = "Pick Image From Gallery") }}
            item { Button(onClick = {mainUIEvent.requestRecordAudioPermission()}) { Text(text = "Test Permission") }}

            item { Button(onClick = {mainUIEvent.requestManagerWithState()}) { Text(text = "Request With State") }}
            item { Button(onClick = {mainUIEvent.requestManagerWithResult()}) { Text(text = "Request With Result") }}

            item { Button(onClick = {mainUIEvent.pickFile()}) { Text(text = "Pick File") }}

            item { Button(onClick = {mainUIEvent.pickDate()}) { Text(text = mainUIState.selectedDate) }}
            item { Button(onClick = {mainUIEvent.pickTime()}) { Text(text = mainUIState.selectedTime) }}

            item {
                Button(onClick = {mainUIEvent.showDialog ()}) { Text(text = "Show Dialog") } }

            item { Button(onClick = {mainUIEvent.imageCaptureAndShare()}) { Text(text = "Image Capture And Share") }}
            item { Button(onClick = {mainUIEvent.imageCaptureAndOpen()}) { Text(text = "Image Capture And Open") }}

    })
}

@Composable
fun Greeting1(name: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Hello 1 $name!",fontSize = 30.sp
        )
    }
}

@Composable
fun Greeting2(name: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Hello 2 $name!",fontSize = 30.sp
        )
    }
}

@Composable
fun Greeting3(name: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Hello 3 $name!",fontSize = 30.sp
        )
    }
}
@Composable
fun Greeting4(name: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Hello 4 $name!",fontSize = 30.sp
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
        bottomNavigationDestinationList = ApplyCoreDestinationsManager().getBottomNavigationDestanition() ,
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
        "Greeting1".asTextManager(),"greeting_1",R.drawable.icon_android,"G_1_1","G_1_2",
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting1("Greeting1") } }
    ),
    val greeting2:DestinationManager=DestinationManager(
        "Greeting2".asTextManager(),"greeting_2",R.drawable.icon_android,"G_2_1","G_2_2",
        true,true,true,true, {
           val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting2("Greeting2") } }
    ),
    val greeting3:DestinationManager=DestinationManager(
        "Greeting3".asTextManager(),"greeting_3",R.drawable.icon_android,"G_3_1",null,
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting3("Greeting3") } }
    ),
    val greeting4:DestinationManager=DestinationManager(
        "Greeting4".asTextManager(),"greeting_4",R.drawable.icon_android,null,"G_4_2",
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting3("Greeting4") } }
    ),
    val greeting5:DestinationManager=DestinationManager(
        "Greeting5".asTextManager(),"greeting_5",R.drawable.icon_android,null,null,
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={}
            navOptionsBuilderExt
        }, { { Greeting3("Greeting5") } }
    )
){
    fun getStartDestination()=mainCompose
    fun getAllDestination()= listOf(mainCompose,greeting1,greeting2,greeting3,greeting4,greeting5)
    fun getHomeDestanition()= listOf(greeting1 ,greeting2, greeting3)
    fun getBottomNavigationDestanition()= listOf(greeting4 ,greeting5)
}

//enum class NavigationManager(){
//    MainCompose, Greeting1, Greeting2, Greeting3
//}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    ApplyCoreManagerTheme {
//        Greeting("Android")
//    }
//}