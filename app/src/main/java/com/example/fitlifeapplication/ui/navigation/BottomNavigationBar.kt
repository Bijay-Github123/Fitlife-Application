package com.example.fitlifeapplication.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitlifeapplication.ui.theme.bottomNavSelected
import com.example.fitlifeapplication.ui.theme.bottomNavUnselected

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workouts,
        BottomNavItem.Create,
        BottomNavItem.Delegate,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                if (item == BottomNavItem.Delegate) {
                   it.route?.startsWith("delegate") == true
                } else {
                   it.route == item.screenRoute
                }
            } == true
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(22.dp) // Slightly smaller than default 24dp and uniform
                    )
                },
                label = { Text(text = item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = bottomNavSelected,
                    selectedTextColor = bottomNavSelected,
                    unselectedIconColor = bottomNavUnselected,
                    unselectedTextColor = bottomNavUnselected
                )
            )
        }
    }
}
