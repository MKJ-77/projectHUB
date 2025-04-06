package com.example.projecthub.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.data.Assignment
import com.example.projecthub.usecases.CreateAssignmentFAB
import com.example.projecthub.usecases.MainAppBar
import com.example.projecthub.usecases.bottomNavigationBar
import com.example.projecthub.usecases.formatTimestamp
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun assignmentsScreen(navController: NavHostController,
                      authViewModel: authViewModel = viewModel()
) {
    var isLoading by remember { mutableStateOf(true) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Assignments", "All Assignments")
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val assignmentsState = remember { mutableStateListOf<Assignment>() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        isLoading = true
        Firebase.firestore.collection("assignments")
            .get()
            .addOnSuccessListener { result ->
                assignmentsState.clear()
                val assignments = result.documents.mapNotNull { doc ->
                    doc.toObject(Assignment::class.java)
                }
                assignmentsState.addAll(assignments)
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch assignments", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            MainAppBar(title = "Assignments", navController = navController)
        },
        bottomBar = {
            bottomNavigationBar(navController = navController, currentRoute = "assignments_page")
        },

        floatingActionButton = {
            CreateAssignmentFAB(onClick = { showDialog = true })
        },
        floatingActionButtonPosition = FabPosition.Center,

        ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab
            ) {
                tabs.forEachIndexed{ index, title ->
                    Tab(
                        text = { Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        }
                    )
                }

            }
            when (selectedTab) {
                0 -> {
                    val myAssignments = if (currentUserId != null) {
                        assignmentsState.filter {
                            it.createdBy == currentUserId
                        }
                    }else emptyList()
                    AvailableAssignmentsList(myAssignments,isLoading)
                }
                1 -> AvailableAssignmentsList(assignments = assignmentsState)
            }

        }
    }
    if (showDialog) {
        CreateAssignmentDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            authViewModel = authViewModel,
            onAssignmentCreated = {
                showDialog = false
                Toast.makeText(
                    context,
                    "Assignment created successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

}

@Composable
fun AvailableAssignmentsList(assignments: List<Assignment>,isLoading: Boolean = false) {
    if(isLoading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }else if (assignments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No assignments available")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assignments) { assignment ->
                AssignmentCard(assignment)
            }
        }
    }
}

//@Composable
//fun PostedAssignmentsList(assignments: List<Assignment>){
//    if(assignments.isEmpty()){
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("You haven't posted any assignments yet")
//        }
//    }else{
//        LazyColumn (
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//
//        ){
//            items(assignments){ assignment ->
//                AssignmentCard(assignment)
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  AssignmentCard(assignment: Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Navigate to assignment details */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = assignment.subject,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = "Posted: ${formatTimestamp(assignment.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(Icons.Default.Timer, "Deadline: ${assignment.deadline}")
                InfoChip(Icons.Default.CurrencyRupee, assignment.budget.toString())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { /* View bids */ },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("View Bids")
                }
                Button(onClick = { /* Manage assignment */ }) {
                    Text("Manage")
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        modifier = Modifier.padding(end = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}