package com.example.fitlifeapplication.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitlifeapplication.ui.screen.CreateWorkoutScreen
import com.example.fitlifeapplication.ui.screen.DelegateScreen
import com.example.fitlifeapplication.ui.screen.EditProfileScreen
import com.example.fitlifeapplication.ui.screen.EquipmentScreen
import com.example.fitlifeapplication.ui.screen.HomeScreen
import com.example.fitlifeapplication.ui.screen.ProfileScreen
import com.example.fitlifeapplication.ui.screen.RoutinesScreen
import com.example.fitlifeapplication.ui.screen.WorkoutsScreen
import com.example.fitlifeapplication.ui.screens.LoginScreen
import com.example.fitlifeapplication.ui.screens.RegisterScreen
import com.example.fitlifeapplication.ui.screens.SplashScreen
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Workouts : Screen("workouts")
    object Routines : Screen("routines")
    object Create : Screen("create")
    object EditWorkout : Screen("edit_workout/{routineId}") {
        fun createRoute(routineId: Long) = "edit_workout/$routineId"
    }
    object Equipment : Screen("equipment")
    object Delegate : Screen("delegate?routineId={routineId}") {
        fun createRoute(routineId: Long) = "delegate?routineId=$routineId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
}

@Composable
fun FitLifeNavHost(sharedText: String? = null) {
    val navController = rememberNavController()
    // Start with Splash if no deep link, otherwise go to Create or Login
    val splashDestination = if (sharedText != null) "${Screen.Create.route}?text=$sharedText" else Screen.Login.route
    
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController, nextDestination = splashDestination)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            MainScaffold(navController)
        }
        composable(
            route = "${Screen.Create.route}?text={text}",
            arguments = listOf(navArgument("text") { 
                type = NavType.StringType
                nullable = true
            })
        ) {
            CreateWorkoutScreen(
                navController = navController,
                sharedText = it.arguments?.getString("text")
            )
        }
        composable(
            route = Screen.EditWorkout.route,
            arguments = listOf(navArgument("routineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getLong("routineId")
            CreateWorkoutScreen(
                navController = navController,
                routineId = routineId
            )
        }
        composable(Screen.Equipment.route) {
            EquipmentScreen()
        }
    }
}

@Composable
fun MainScaffold(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    androidx.compose.material3.Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) }
    ) { paddingValues ->
        NavHost(navController = bottomNavController, startDestination = Screen.Home.route, modifier = Modifier.padding(paddingValues)) {
            composable(Screen.Home.route) {
                HomeScreen(navController = bottomNavController)
            }
            composable(Screen.Workouts.route) {
                WorkoutsScreen(navController = bottomNavController)
            }
            composable(Screen.Routines.route) {
                RoutinesScreen()
            }
            composable(Screen.Create.route) { CreateWorkoutScreen(navController = bottomNavController) }
            composable(
                route = Screen.EditWorkout.route,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId")
                CreateWorkoutScreen(
                    navController = bottomNavController, 
                    routineId = routineId
                )
            }
            composable(
                route = Screen.Delegate.route,
                arguments = listOf(navArgument("routineId") { 
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId")
                DelegateScreen(
                    navController = bottomNavController,
                    routineId = if (routineId == -1L) null else routineId
                )
            }
            composable(Screen.Profile.route) { 
                ProfileScreen(
                    navController = bottomNavController,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) // Clear back stack
                        }
                    }
                ) 
            }
            composable(Screen.EditProfile.route) {
                val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
                EditProfileScreen(
                    navController = bottomNavController,
                    viewModel = profileViewModel
                )
            }
        }
    }
}
