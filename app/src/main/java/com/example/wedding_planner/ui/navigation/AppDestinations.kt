package com.example.wedding_planner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        "Início",
        Icons.Filled.Home,
        Icons.Outlined.Home
    ),
    TASKS(
        "Tarefas",
        Icons.Filled.Checklist,
        Icons.Outlined.Checklist
    ),
    GUESTS(
        "Convidados",
        Icons.Filled.PeopleAlt,
        Icons.Outlined.PeopleAlt
    ),
    BUDGET(
        "Financeiro",
        Icons.Filled.AttachMoney,
        Icons.Outlined.AttachMoney
    ),
    PROFILE(
        "Perfil",
        Icons.Filled.AccountCircle,
        Icons.Outlined.AccountCircle
    )
}