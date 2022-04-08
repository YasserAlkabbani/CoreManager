package com.yasser.applycoremanager

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yasser.applycoremanager.ui.theme.ApplyCoreManagerTheme
import com.yasser.core_manager.CoreActivity
import com.yasser.core_manager.manager.TextManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : CoreActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val navController:NavHostController= rememberNavController()

            CoreManagerContent(navController) {
                ApplyCoreManagerTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        NavHost(navController = navController, startDestination = NavigationManager.MainCompose.name ){
                            composable(NavigationManager.MainCompose.name){
                                MainCompose()
                            }
                            composable(NavigationManager.Greeting1.name){
                                Greeting1("11111111111")
                            }
                            composable(NavigationManager.Greeting2.name){
                                Greeting2("22222222222")
                            }
                            composable(NavigationManager.Greeting3.name){
                                Greeting3("33333333333")
                            }
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

    LazyColumn(content = {

        item { Button(onClick = {mainUIEvent.navigateTo(NavigationManager.Greeting1.name)}) { Text(text = "Navigate To Greeting 1") }}
        item { Button(onClick = {mainUIEvent.navigateTo(NavigationManager.Greeting2.name)}) { Text(text = "Navigate To Greeting 2") }}
        item { Button(onClick = {mainUIEvent.navigateTo(NavigationManager.Greeting3.name)}) { Text(text = "Navigate To Greeting 3") }}


        item { Button(onClick = {mainUIEvent.startPhoneCall("+963930345510")}) { Text(text = "Start Call Phone") }}
        item { Button(onClick = {mainUIEvent.pickImageFromGallery()}) { Text(text = "Pick Image From Gallery") }}
        item { Button(onClick = {mainUIEvent.requestRecordAudioPermission()}) { Text(text = "Test Permission") }}

        item { Button(onClick = {mainUIEvent.hideKeyBoard()}) { Text(text = "Hide KeyBoard") }}
        item { Button(onClick = {mainUIEvent.nextFocus()}) { Text(text = "Next Focus") }}
        item { Button(onClick = {mainUIEvent.popUp()}) { Text(text = "Popup") }}
        item { Button(onClick = {mainUIEvent.showToast(TextManager.StringText("Test String Toast"))}) { Text(text = "Show String Toast") }}
        item { Button(onClick = {mainUIEvent.showToast(TextManager.ResourceText(R.string.test_toast_resource))}) { Text(text = "Show Resource Toast") }}


        item { OutlinedTextField(value =mainUIState.textField1 , onValueChange =mainUIEvent.setText1 ) }
        item { OutlinedTextField(value =mainUIState.textField2 , onValueChange =mainUIEvent.setText2 ) }
        item { OutlinedTextField(value =mainUIState.textField3 , onValueChange =mainUIEvent.setText3 ) }

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

enum class NavigationManager(){
    MainCompose, Greeting1, Greeting2, Greeting3
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    ApplyCoreManagerTheme {
//        Greeting("Android")
//    }
//}