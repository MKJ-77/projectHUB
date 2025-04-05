package com.example.projecthub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.usecases.CreateAssignmentFAB
import com.example.projecthub.usecases.MainAppBar
import com.example.projecthub.usecases.bottomNavigationBar
import com.example.projecthub.usecases.bubbleBackground
import com.example.projecthub.viewModel.AuthState
import com.example.projecthub.viewModel.authViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: authViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    var showAssignmentDialog by remember { mutableStateOf(false) }


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login_page") {
                popUpTo("home_page") { inclusive = true }
            }

            else -> Unit
        }
    }



    Scaffold(
        topBar = {
            MainAppBar(title = "Home",navController = navController)
        },
        bottomBar = {
            bottomNavigationBar(navController = navController, currentRoute = "home_page")
        },
        floatingActionButton = {
            CreateAssignmentFAB(onClick = { showAssignmentDialog = true })
        },
        floatingActionButtonPosition = FabPosition.Center,



    ) { paddingValues ->
        if (showAssignmentDialog) {
            CreateAssignmentDialog(
                showDialog = showAssignmentDialog,
                onDismiss = { showAssignmentDialog = false },
                authViewModel = authViewModel,
                onAssignmentCreated = {
                    showAssignmentDialog = false
                    // Show success toast if needed
                }
            )
        }

        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

        }
    }
}