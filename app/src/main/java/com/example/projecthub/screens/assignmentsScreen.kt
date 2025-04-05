package com.example.projecthub.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun assignmentsScreen(navHostController: NavHostController){
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Assignments", "All Assignments")
//    Scaffold(
//        topBar = {
//            TopAppBar(
//
//            )
//        }
//
//    ) { innerPadding ->
//
//    }

}