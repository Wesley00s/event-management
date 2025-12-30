package com.example.wedding_planner.ui.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.R
import com.example.wedding_planner.data.model.enums.SplashDestination
import com.example.wedding_planner.ui.components.WeddingScreenBackground
import com.example.wedding_planner.ui.theme.WeddingPlannerTheme

@Composable
fun SplashScreenRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOrganization: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoading, uiState.destination) {
        if (!uiState.isLoading && uiState.destination != null) {
            when (uiState.destination) {
                SplashDestination.HOME -> onNavigateToHome()
                SplashDestination.LOGIN -> onNavigateToLogin()
                SplashDestination.ORGANIZATION_SELECTION -> onNavigateToOrganization()
                else -> Unit
            }
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    WeddingScreenBackground(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shadowElevation = 12.dp,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Wedding\nPlanner",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
        }
    }
}