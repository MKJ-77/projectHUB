package com.example.projecthub.screens

import AppBackground7
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.projecthub.R
import com.example.projecthub.data.UserProfile
import com.example.projecthub.dummyData
import com.example.projecthub.navigation.routes
import com.example.projecthub.usecases.CreateAssignmentFAB
import com.example.projecthub.usecases.MainAppBar
import com.example.projecthub.usecases.bottomNavigationBar
import com.example.projecthub.usecases.bubbleBackground
import com.example.projecthub.viewModel.ThemeViewModel
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileScreen(navController: NavHostController,authViewModel: authViewModel) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var showDialog by remember { mutableStateOf(false) }


    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(true) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val bio = document.getString("bio") ?: ""
                        val collegeName = document.getString("collegeName") ?: ""
                        val semester = document.getString("semester") ?: ""
                        val collegeLocation = document.getString("collegeLocation") ?: ""
                        val skills = document.get("skills") as? List<String> ?: emptyList()
                        val profilePhotoId = document.getLong("profilePhotoId")?.toInt()
                            ?: R.drawable.profilephoto1
                        val ratingSum = document.getDouble("ratingSum") ?: 0.0
                        val ratingCount = document.getLong("ratingCount")?.toInt() ?: 0
                        val averageRating = document.getDouble("averageRating") ?: 0.0
                        val skillsList = document.get("skills") as? List<String> ?: emptyList()

                        userProfile = UserProfile(
                            name = name,
                            bio = bio,
                            collegeName = collegeName,
                            semester = semester,
                            collegeLocation = collegeLocation,
                            skills = skillsList,
                            profilePhotoId = profilePhotoId,
                            ratingSum = ratingSum,
                            ratingCount = ratingCount,
                            averageRating = averageRating
                        )
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ProfileScreenContent(navController = navController, userProfile = userProfile!!,authViewModel = authViewModel)
    }
}

@Composable
fun ProfileScreenContent(navController: NavHostController, userProfile: UserProfile,authViewModel: authViewModel) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.background
    )
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val themeViewModel: ThemeViewModel = viewModel()


    Scaffold(
        topBar = {
            MainAppBar(title = "Profile",navController = navController)
        },
        bottomBar = {
            bottomNavigationBar(navController = navController, currentRoute = "profile")
        },

        floatingActionButton = {
            CreateAssignmentFAB(onClick = { showDialog = true })
        },
        floatingActionButtonPosition = FabPosition.Center,


    ) { paddingValues ->
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = gradientColors))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AppBackground7(themeViewModel = themeViewModel)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeader(
                    name = userProfile.name,
                    photoId = userProfile.profilePhotoId,
                    averageRating = userProfile.averageRating,
                    ratingCount = userProfile.ratingCount
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        InfoSection(
                            title = "About",
                            icon = Icons.Default.Person,
                            content = userProfile.bio
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        InfoSection(
                            title = "Education",
                            icon = Icons.Default.School,
                            content = null
                        )

                        ProfileDetailRow(
                            label = "College",
                            value = userProfile.collegeName,
                            icon = Icons.Default.AccountBalance
                        )

                        ProfileDetailRow(
                            label = "Semester",
                            value = userProfile.semester,
                            icon = Icons.Default.DateRange
                        )

                        ProfileDetailRow(
                            label = "Location",
                            value = userProfile.collegeLocation,
                            icon = Icons.Default.LocationOn
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        InfoSection(
                            title = "Skills",
                            icon = Icons.Default.Code,
                            content = null
                        )

                        SkillsGrid(skills = userProfile.skills)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                navController.navigate(routes.editProfileScreen.route)

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Edit Profile",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun ProfileHeader(name: String, photoId: Int,averageRating: Double = 0.0,
                  ratingCount: Int = 0) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = photoId),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if(ratingCount > 0){
                val fullStars = averageRating.toInt()
                val hasHalfStar = averageRating - fullStars >= 0.5

                repeat(fullStars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (hasHalfStar) {
                    Icon(
                        imageVector = Icons.Default.StarHalf,
                        contentDescription = "Half Star",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                val remainingStars = 5 - fullStars - (if (hasHalfStar) 1 else 0)
                repeat(remainingStars) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Empty Star",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = String.format("%.1f", averageRating) + " (${ratingCount})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }else{

                Text(
                    text = "Not rated yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoSection(title: String, icon: ImageVector, content: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }

    if (!content.isNullOrBlank()) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SkillsGrid(skills: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.forEach { skill ->
                SuggestionChip(
                    onClick = { },
                    label = { Text(skill) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}