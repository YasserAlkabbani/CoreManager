package com.yasser.coremanager.manager

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.*

data class NavigationManager(
    val destinationsManagerList:List<DestinationManager>,
    val bottomNavigationDestinationList:List<DestinationManager>,
    val startDestination:DestinationManager,
) {
    private var currentActivity:String?=null

    private val navHostController:MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    fun returnNavHostControllerForCurrentActivity(activity:String):Flow<NavHostController?> =
        navHostController.map { if (activity==currentActivity) it else null }

    fun setNavHostController(newNavHostController: NavHostController?,newCurrentActivity:String?){
        if (newNavHostController!=null){
            currentActivity=newCurrentActivity
            navHostController.update { newNavHostController }
        }else if (currentActivity==newCurrentActivity){
            navHostController.update { newNavHostController }
        }
    }

    private val _currentDestination: MutableStateFlow<DestinationManager> by lazy { MutableStateFlow(startDestination) }
    val currentDestination: StateFlow<DestinationManager> = _currentDestination
    fun setCurrentDestination(destinationManager:DestinationManager){ _currentDestination.value=destinationManager }

    fun getNavHostComposableContent(navGraphBuilder: NavGraphBuilder)=
        destinationsManagerList.map { it.getNavHostComposableContent(navGraphBuilder) }
}
data class DestinationManager(
    val label: TextManager, val route: String, @DrawableRes val icon: Int?,
    val arg1Key:String?, val arg2Key:String?,
    val haveTopBar: Boolean, val haveBackButton:Boolean, val haveBottomNavigation: Boolean, val haveFloatingActionButton: Boolean,
    val navOptionBuilder:(DestinationManager) -> NavOptionsBuilder.()->Unit,
    private val composeManagerContent: ()-> @Composable ()->Unit,
){
    private fun getRouteWithKey(): String {
        val arg1=if (!arg1Key.isNullOrBlank())"/{${arg1Key}}" else ""
        val arg2=if (!arg2Key.isNullOrBlank())"/{${arg2Key}}" else ""
        return route+arg1+arg2
    }
    fun getNavHostComposableContent(navGraphBuilder: NavGraphBuilder){
        navGraphBuilder.composable(
            route =getRouteWithKey(),
            arguments = mutableListOf<NamedNavArgument>().apply {
                if (arg1Key!=null) add(navArgument(arg1Key){ NavType.StringType })
                if (arg2Key!=null) add(navArgument(arg2Key){ NavType.StringType })
            },
            content = {composeManagerContent()()}
        )
    }
}