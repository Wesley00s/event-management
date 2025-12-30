package com.example.wedding_planner

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wedding_planner.ui.navigation.RootDestinations
import com.example.wedding_planner.ui.screen.main.MainScreen
import com.example.wedding_planner.ui.screen.login.LoginScreenRoute
import com.example.wedding_planner.ui.screen.org.OrgScreenRoute
import com.example.wedding_planner.ui.screen.settings.SettingsScreenRoute
import com.example.wedding_planner.ui.screen.splash.SplashScreenRoute

@Composable
fun MainApplication(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = RootDestinations.SPLASH
    ) {
        composable(RootDestinations.SPLASH) {
            SplashScreenRoute(
                onNavigateToHome = {
                    navController.navigate(RootDestinations.MAIN_APP) {
                        popUpTo(RootDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(RootDestinations.LOGIN) {
                        popUpTo(RootDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToOrganization = {
                    navController.navigate(RootDestinations.ORGANIZATION) {
                        popUpTo(RootDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(RootDestinations.MAIN_APP) {
            MainScreen(
                onSignOutSuccess = {
                    navController.navigate(RootDestinations.LOGIN) {
                        popUpTo(RootDestinations.MAIN_APP) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(RootDestinations.SETTINGS) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(RootDestinations.LOGIN) {
            LoginScreenRoute(
                onNavigateToHome = {
                    navController.navigate(RootDestinations.MAIN_APP) {
                        popUpTo(RootDestinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToOrganization = {
                    navController.navigate(RootDestinations.ORGANIZATION) {
                        popUpTo(RootDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(RootDestinations.ORGANIZATION) {
            OrgScreenRoute(
                onOrganizationSelected = {
                    navController.navigate(RootDestinations.MAIN_APP) {
                        popUpTo(RootDestinations.ORGANIZATION) { inclusive = true }
                    }
                }
            )
        }

        composable(RootDestinations.SETTINGS) {
            SettingsScreenRoute(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}