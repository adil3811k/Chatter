package com.example.chatter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatter.group.screen.GroupCom
import com.example.chatter.home.screen.HomeScreen
import com.example.chatter.profile.screen.Profilecom


private data class TopLevelRoute(val name: String, val icon: ImageVector)
private val topLevelRoutes = listOf(
    TopLevelRoute("Home", Icons.Filled.Home),
    TopLevelRoute("Group" , Icons.Filled.Group),
    TopLevelRoute("Profile" , Icons.Filled.Person)
)

@SuppressLint("RestrictedApi")
@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination
                topLevelRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                topLevelRoute.icon,
                                contentDescription = topLevelRoute.name
                            )
                        },
                        label = { Text(topLevelRoute.name) },
                        selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(
                                topLevelRoute.name,
                                null
                            )
                        } == true,
                        onClick = {
                            navController.navigate(
                                topLevelRoute
                                    .name
                            ){
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerpadding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = modifier.padding(innerpadding)
        ) {
            composable(route = "Home") {
                HomeScreen()
            }
            composable("Group") {
                GroupCom()
            }
            composable("Profile") {
                Profilecom()
            }
        }
    }
}
