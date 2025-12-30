package com.example.wedding_planner.ui.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wedding_planner.ui.components.ProfileNavIcon
import com.example.wedding_planner.ui.navigation.AppDestinations
import com.example.wedding_planner.ui.screen.budget.BudgetScreenRoute
import com.example.wedding_planner.ui.screen.guest.GuestScreenRoute
import com.example.wedding_planner.ui.screen.home.HomeScreenRoute
import com.example.wedding_planner.ui.screen.profile.ProfileScreenRoute
import com.example.wedding_planner.ui.screen.task.TaskScreenRoute

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onSignOutSuccess: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userPhotoUrl = viewModel.photoUrl

    val transparentItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                val isSelected = currentRoute == destination.name

                item(
                    alwaysShowLabel = false,
                    colors = transparentItemColors,
                    icon = {
                        if (destination == AppDestinations.PROFILE && userPhotoUrl != null) {
                            ProfileNavIcon(
                                photoUrl = userPhotoUrl,
                                isSelected = isSelected
                            )
                        } else {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        }
                    },
                    label = {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        nestedNavController.navigate(destination.name) {
                            popUpTo(nestedNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface,
            navigationRailContainerColor = MaterialTheme.colorScheme.surface
        )

    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = nestedNavController,
                startDestination = AppDestinations.HOME.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppDestinations.HOME.name) {
                    HomeScreenRoute(
                        onNavigateToSettings = onNavigateToSettings
                    )
                }
                composable(AppDestinations.TASKS.name) {
                    TaskScreenRoute()
                }
                composable(AppDestinations.GUESTS.name) {
                    GuestScreenRoute()
                }
                composable(AppDestinations.BUDGET.name) {
                    BudgetScreenRoute()
                }
                composable(AppDestinations.PROFILE.name) {
                    ProfileScreenRoute(
                        onLogout = onSignOutSuccess
                    )
                }
            }
        }
    }
}