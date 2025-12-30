package com.example.wedding_planner.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wedding_planner.R
import com.example.wedding_planner.data.model.enums.LoginDestination
import com.example.wedding_planner.ui.components.ErrorMessage
import com.example.wedding_planner.ui.components.GoogleLoginButton
import com.example.wedding_planner.ui.components.WeddingScreenBackground

@Composable
fun LoginScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToOrganization: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            Toast.makeText(context, "ERRO: $errorMessage", Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }
    LaunchedEffect(state.navigationDestination) {
        when (state.navigationDestination) {
            LoginDestination.HOME -> {
                onNavigateToHome()
                viewModel.onNavigated()
            }
            LoginDestination.ORGANIZATION_SELECTION -> {
                onNavigateToOrganization()
                viewModel.onNavigated()
            }
            null -> Unit
        }
    }

    LoginScreen(
        modifier = modifier,
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        WeddingScreenBackground(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shadowElevation = 12.dp,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painterResource(R.drawable.ic_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Vamos começar?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Entre para organizar convidados, tarefas e seu orçamento dos sonhos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            GoogleLoginButton(
                                onClick = { onEvent(LoginUiEvent.PerformLogin(context)) }
                            )
                        }
                        if (uiState.error != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            ErrorMessage(error = uiState.error)
                        }
                    }
                }
            }
        }
    }
}
