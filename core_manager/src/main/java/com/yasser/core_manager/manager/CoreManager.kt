package com.yasser.core_manager.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreManager @Inject constructor(){

    lateinit var composeManagerEvent:(ComposeManager)->Unit
    private set
    internal fun setComposeManagerEvent(newComposeManagerEvent:(ComposeManager)->Unit)
    {composeManagerEvent=newComposeManagerEvent}

    lateinit var permissionManagerEvent:(PermissionManager)->Unit
    private set
    internal fun setPermissionManagerEvent(newPermissionManagerEvent: (PermissionManager)->Unit)
    {permissionManagerEvent=newPermissionManagerEvent}

    lateinit var activityForResultManagerEvent:(ActivityForResultManager)->Unit
    private set
    internal fun setActivityForResultManagerEvent(newActivityForResultManagerEvent:(ActivityForResultManager)->Unit)
    {activityForResultManagerEvent=newActivityForResultManagerEvent}

    lateinit var activityManagerEvent:(StartActivityManager)->Unit
    private set
    internal fun setStartActivity(newStartActivity:(newStartActivityManager:StartActivityManager)->Unit){
        activityManagerEvent=newStartActivity
    }

}