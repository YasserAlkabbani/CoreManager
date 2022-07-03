package com.yasser.applycoremanager

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
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

    private val mainViewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


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

    val mainUIEvent=mainViewModel.mainUIEvent
    val mainUIState=mainViewModel.mainUIState.collectAsState().value
    val currentDestination=mainUIState.navigationManager.currentDestination.collectAsState().value

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

            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting1,null,null)}) { Text(text = "Navigate To Greeting 1") }}
            item { Button(onClick = {mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting2,null,null)}) { Text(text = "Navigate To Greeting 2") }}
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

        }
    )

}

@Composable
fun Greeting1(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {mainViewModel.mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting2,null,null)}
        ){
            Text(text = "Hello 1 $name!",fontSize = 30.sp)
        }
    }
}

@Composable
fun Greeting2(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {mainViewModel.mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting3,"12345","56698")}
        ){
            Text(text = "Hello 2 $name!",fontSize = 30.sp)
        }
    }
}

@Composable
fun Greeting3(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {mainViewModel.mainUIEvent.navigateTo(ApplyCoreDestinationsManager().greeting4,"12345","56698")}
        ){
            Text(text = "Hello 3 $name!",fontSize = 30.sp)
        }
    }
}
@Composable
fun Greeting4(name: String) {
    val mainViewModel:MainViewModel= hiltViewModel()
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {mainViewModel.mainUIEvent.popUp()}
        ){
            Text(text = "Hello 4 $name!",fontSize = 30.sp)
        }
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
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={
                popUpTo("main_compose"){inclusive=true}
            }
            navOptionsBuilderExt
        }, { { Greeting1("Greeting1") } }
    ),
    val greeting2:DestinationManager=DestinationManager(
        "Greeting2".asTextManager(),"greeting_2",R.drawable.icon_android,null,null,
        true,true,true,true, {
           val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={
               popUpTo("greeting_1"){inclusive=true}
           }
            navOptionsBuilderExt
        }, { { Greeting2("Greeting2") } }
    ),
    val greeting3:DestinationManager=DestinationManager(
        "Greeting3".asTextManager(),"greeting_3",R.drawable.icon_android,"G_3_1","G_3_2",
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={
                popUpTo("greeting_2"){inclusive=false}
            }
            navOptionsBuilderExt
        }, { { Greeting3("Greeting3") } }
    ),
    val greeting4:DestinationManager=DestinationManager(
        "Greeting4".asTextManager(),"greeting_4",R.drawable.icon_android,null,"G_4_2",
        true,true,true,true, {
            val navOptionsBuilderExt:NavOptionsBuilder.()->Unit={
                popUpTo("greeting_3"){inclusive=true}
            }
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