
CoreManager Created To Reduce Blueprint Code And Use ViewModel More Easier


With CoreManager, You Can Request Permission, Pick Images, Or Navigating From ViewModel Without Write Any Thing In The Activity

Note : To Use This Library You Must Use ViewModel ,Hilt For Dependency Injection ,Single Activity App

Some Feature Support Compose Only For Now ...

[![](https://jitpack.io/v/YasserAlkabbani/CoreManager.svg)](https://jitpack.io/#YasserAlkabbani/CoreManager)


How To Use ?

1 - Add This Line To dependencies 
```
implementation 'com.github.YasserAlkabbani:CoreManager:0.1.6-alpha'
```

2 - repository
```
repositories {
  google()
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
```

3 - In Manifist File
```
  <application
      .....
      <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${packageName}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_path" />
      </provider>
      .....
  </application>
```

in res -> xml -> Add file_path -> In file_path Add This
```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="my_images" path="." />
    <files-path name="my_file" path="."/>
    <external-path name="external" path="." />
    <cache-path name="cache" path="." />
</paths>
```

4 - Make Your Activity Inherit From CoreActivity (Instead Of Activity Or FragmentActivity)
```
@AndroidEntryPoint
class ExampleActivity : CoreActivity() 
```

5 - Inject CoreManager In ViewModel Constructor
```
@HiltViewModel
class ExampleViewModel @Inject constructor(private val coreManager:CoreManager):ViewModel()
```
6 - To Use With JitPack Compose Put Your Code Inside CoreManagerContent

```
sealed class ApplyCoreManagerDestinationManager{
    object MainCompose:DestinationManager("MainCompose".asTextManager(),"main_compose","",R.drawable.icon_android,true,true,true,true, { { MainCompose()}})
    object Greeting1:DestinationManager("Greeting1".asTextManager(),"greeting_1","",R.drawable.icon_android,true,true,true,true,{{ Greeting1("Greeting1")}})
    object Greeting2:DestinationManager("Greeting2".asTextManager(),"greeting_2","",R.drawable.icon_android,true,true,true,true,{{ Greeting2("Greeting2")}})
    object Greeting3:DestinationManager("Greeting3".asTextManager(),"greeting_3","",R.drawable.icon_android, true,true,true,true,{{ Greeting3("Greeting3")}})
}
setContent {
  
  val navigationManager:NavigationManager=NavigationManager(
      destinationsManagerList =listOf(
          ApplyCoreManagerDestinationManager.MainCompose,ApplyCoreManagerDestinationManager.Greeting1,
          ApplyCoreManagerDestinationManager.Greeting2,ApplyCoreManagerDestinationManager.Greeting3
      ) ,
      startDestination =ApplyCoreManagerDestinationManager.MainCompose ,
      bottomNavigationDestinationList = listOf(ApplyCoreManagerDestinationManager.Greeting1,ApplyCoreManagerDestinationManager.Greeting2) ,
      navHostController =navController
  )

  CoreManagerContent(navigationManager) {
     PUT YOUR CODE HERE
  }
  
}
```

And That's it ... Now You Can Use CoreManager In Your ViewModel

1- PermissionManager : For Request Permission
Example:
 ```
coreManager.permissionManagerEvent(
     PermissionManager.Camera(
          taskToDoWhenPermissionGranted = {},
          showRequestPermissionRationale = {},
          taskToDoWhenPermissionDeclined = {}
      )
)
```

2 - ActivityForResultManager : For StartActivityForResult And Return Result
```
coreManager.permissionManagerEvent(
    PermissionManager.CustomPermission(
        permission = Manifest.permission.CALL_PHONE,
        taskToDoWhenPermissionGranted = {}, showRequestPermissionRationale = {}, taskToDoWhenPermissionDeclined = {}
    )
)
```


3 - StartActivityManager : For StartActivity
Example:
```
coreManager.activityManagerEvent(StartActivityManager.GoToSettings)
coreManager.activityManagerEvent(StartActivityManager.GoToSendEmail(emailAddress))
coreManager.activityManagerEvent(StartActivityManager.CustomIntent(intent)
coreManager.activityManagerEvent(StartActivityManager.ShareFile(file))
coreManager.activityManagerEvent(StartActivityManager.RestartApp)
```

4 - ComposeManager : Helper For Compose System
Examples:
```
coreManager.composeManagerEvent(ComposeManager.Popup)
coreManager.composeManagerEvent(ComposeManager.HideKeyBoard)
coreManager.composeManagerEvent(ComposeManager.NextFocus)
coreManager.composeManagerEvent(ComposeManager.ShowToast(it))
coreManager.composeManagerEvent(ComposeManager.Navigate { navigate(route) })
```

5 - RequestManager
Examples:

One Shot
```
val result1=requestProcessWithResult (
    forceRefreshData = true,
    taskForRefreshData = {
        delay(1000)
        "TASK RESULT WITH STATE REFRESH AS STATE"
    },
    taskForReturnData = {
        delay(1000)
        "TASK RESULT WITH STATE RETURN CACHING AS STATE"
    }
)
when(result2){
    is ResultManager.Success -> Log.d("CoreManager",result2.result.orEmpty())
    is ResultManager.Failed -> Log.d("CoreManager",result2.throwable.message.orEmpty())
}
```
With State
```
requestProcessWithState(
      forceRefreshData = false,
      taskForRefreshData = {
          delay(1000)
          null
      },
      taskForReturnData = {
          delay(1000)
          "TASK RESULT WITH STATE RETURN CACHING"
      }
  ).collect{
      when(it){
          is ResultManagerWithState.Loading -> Log.d("CoreManager","Loading")
          is ResultManagerWithState.Success -> Log.d("CoreManager",it.result.orEmpty())
          is ResultManagerWithState.Failed -> Log.d("CoreManager",it.throwable.message.orEmpty())
      }
  }
```

6 - Other

Get String By Res
```
val string:String = coreManager.stringByRes(res)
```
