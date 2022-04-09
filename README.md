
CoreManager Created To Reduce Blueprint Code And Use ViewModel More Easier


With CoreManager, You Can Request Permission, Pick Images, Or Navigating From ViewModel Without Write Any Thing In The Activity

Note : To Use This Library You Must Use ViewModel ,Hilt For Dependency Injection ,Single Activity App

Some Feature Support Compose Only For Now ...

[![](https://jitpack.io/v/YasserAlkabbani/CoreManager.svg)](https://jitpack.io/#YasserAlkabbani/CoreManager)


How To Use ?

1 - Add This Line To dependencies 
```
implementation 'com.github.YasserAlkabbani:CoreManager:0.1.3-alpha'
```

2 - repository In settings.gradle
```
repositories {
  google()
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
```
3 - Make Your Activity Inherit From CoreActivity (Instead Of Activity Or FragmentActivity)
```
@AndroidEntryPoint
class ExampleActivity : CoreActivity() 
```

4 - Inject CoreManager In ViewModel Constructor
```
@HiltViewModel
class ExampleViewModel @Inject constructor(private val coreManager:CoreManager):ViewModel()
```
5 - To Use With JitPack Compose Put Your Code Inside CoreManagerContent

```
setContent {
  val navController:NavHostController= rememberNavController()

  CoreManagerContent(navController) {
     PUT YOUR CODE HERE
  }
  
}
```

And That's it ... Now You Can Use CoreManager In Your ViewModel

CoreManager Have Four Type For Now ...

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
```

4- ComposeManager : Helper For Compose System
Examples:
```
coreManager.composeManagerEvent(ComposeManager.Popup)
coreManager.composeManagerEvent(ComposeManager.HideKeyBoard)
coreManager.composeManagerEvent(ComposeManager.NextFocus)
coreManager.composeManagerEvent(ComposeManager.ShowToast(it))
coreManager.composeManagerEvent(ComposeManager.Navigate { navigate(route) })
```
