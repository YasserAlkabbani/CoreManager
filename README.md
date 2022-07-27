
CoreManager Created To Reduce Blueprint Code And Use ViewModel More Easier


With CoreManager, You Can Request Permission, Pick Images, Or Navigating From ViewModel Without Write Any Thing In The Activity

Note : To Use This Library You Must Use ViewModel ,Hilt For Dependency Injection ,Single Activity App

Some Feature Support Compose Only For Now ...

[![](https://jitpack.io/v/YasserAlkabbani/CoreManager.svg)](https://jitpack.io/#YasserAlkabbani/CoreManager)


How To Use ?

1 - Add This Line To dependencies 
```
implementation 'com.github.YasserAlkabbani:CoreManager:X.X.X'
[![](https://jitpack.io/v/YasserAlkabbani/CoreManager.svg)](https://jitpack.io/#YasserAlkabbani/CoreManager)
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

You Can See This Sample To Know How Work
https://github.com/YasserAlkabbani/CoreManager/tree/master/app/src/main/java/com/yasser/applycoremanager
