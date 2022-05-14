package com.yasser.coremanager.manager

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager(
    val destinationsManagerList:List<DestinationManager>,
    val bottomNavigationDestinationList:List<DestinationManager>,
    val startDestination:DestinationManager,
    val navHostController: NavHostController
) {
    private val _currentDestination: MutableStateFlow<DestinationManager> by lazy { MutableStateFlow(startDestination) }
    val currentDestination: StateFlow<DestinationManager> = _currentDestination
    fun setCurrentDestination(destinationManager:DestinationManager){_currentDestination.value=destinationManager}

    fun popup(){navHostController.popBackStack()}
    fun navigate(
        destinationManager: DestinationManager,arg1Value:String?,arg2Value:String?,
        launchSingleTop:Boolean=false, restoreState:Boolean=false,
        popUpToDestination:DestinationManager?=null, saveState:Boolean=false, inclusive:Boolean=false
    ){
        val route= destinationManager.route
        val arg1=if (!destinationManager.arg1Key.isNullOrBlank())"/${arg1Value}" else ""
        val arg2=if (!destinationManager.arg2Key.isNullOrBlank())"/${arg2Value}" else ""
        val fullRoute=route+arg1+arg2
        navHostController.navigate(fullRoute){
            this.launchSingleTop=launchSingleTop
            this.restoreState=restoreState
            popUpToDestination?.let {
                this.popUpTo(it.route){
                    this.saveState=saveState
                    this.inclusive=inclusive
                }
            }
        }
    }
    fun getNavHostComposableContent(navGraphBuilder: NavGraphBuilder)=
        destinationsManagerList.map { it.getNavHostComposableContent(navGraphBuilder) }
}
data class DestinationManager(
    val label: TextManager, val route: String, @DrawableRes val icon: Int?,
    val arg1Key:String?, val arg2Key:String?,
    val haveTopBar: Boolean, val haveBackButton:Boolean, val haveBottomNavigation: Boolean, val haveFloatingActionButton: Boolean,
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