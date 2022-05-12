package com.yasser.coremanager.manager

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager(
    private val destinationsManagerList:List<DestinationManager>,
    private val bottomNavigationDestinationList:List<DestinationManager>,
    private val startDestination:DestinationManager,
    private val navHostController: NavHostController
) {
    private val _currentDestination: MutableStateFlow<DestinationManager> by lazy { MutableStateFlow(startDestination) }
    val currentDestination: StateFlow<DestinationManager> = _currentDestination
    fun setCurrentDestanation(destinationIndex:DestinationManager){_currentDestination.value=destinationIndex}

    fun popup(){navHostController.popBackStack()}
    fun navigate(
        destinationManager: DestinationManager, routeValue:String,
        launchSingleTop:Boolean, restoreState:Boolean,
        popUpToDestination:DestinationManager?, saveState:Boolean, inclusive:Boolean
    ){
        destinationManager.route.let {route->
            navHostController.navigate(if (route.isNotBlank()&&routeValue.isNotBlank())"$route/{$routeValue}" else route){
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
    }
    fun getStartDestination()=startDestination
    fun getbottomNavigationDestanation():List<DestinationManager> = bottomNavigationDestinationList
    fun getNavController()=navHostController
    fun getNavHostComposableContent(navGraphBuilder: NavGraphBuilder)=
        destinationsManagerList.map { it.getNavHostComposableContent(navGraphBuilder) }
}
open class DestinationManager(
    val name: TextManager, val route: String, private val routeKey:String, @DrawableRes val icon: Int?,
    private val haveTopBar: Boolean, private val haveBackButton:Boolean, private val haveBottomNavigation: Boolean,
    private val haveFloatingActionButton: Boolean, private val composeManagerContent: ()-> @Composable ()->Unit,
){
    fun getRouteWithKey()=if (routeKey.isNotBlank())"$route/{$routeKey}" else route
    fun getNavHostComposableContent(navGraphBuilder: NavGraphBuilder){
        navGraphBuilder.composable(
            route =getRouteWithKey(),
            arguments = mutableListOf<NamedNavArgument>().apply { if (routeKey.isNotBlank()) add(navArgument(routeKey){ NavType.StringType })},
            content = {composeManagerContent()()}
        )
    }
}