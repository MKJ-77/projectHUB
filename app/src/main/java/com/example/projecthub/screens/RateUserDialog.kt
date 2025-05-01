@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.projecthub.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RateUserDialog(
    userIdToRate: String,
    onDismiss: () -> Unit,
    onRatingSubmitted: (Int) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var userName by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Fetch user name
    LaunchedEffect(userIdToRate) {
        FirebaseFirestore.getInstance().collection("users")
            .document(userIdToRate)
            .get()
            .addOnSuccessListener { document ->
                userName = document.getString("name") ?: "User"
            }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Rate $userName",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "How would you rate your experience?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { rating = i },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star $i",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Text(
                    text = when(rating) {
                        0 -> "Select a rating"
                        1 -> "Poor"
                        2 -> "Fair"
                        3 -> "Good"
                        4 -> "Very Good"
                        else -> "Excellent"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rating > 0) {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users")
                                .document(userIdToRate)
                                .get()
                                .addOnSuccessListener { document ->
                                    val currentRating = document.getDouble("averageRating") ?: 0.0
                                    val ratingCount = document.getLong("ratingCount")?.toInt() ?: 0
                                    val currentRatingSum = document.getDouble("ratingSum") ?: 0.0

                                    val newRatingCount = ratingCount + 1
                                    val newRatingSum = currentRatingSum + rating
                                    val newRatingAvg = newRatingSum / newRatingCount

                                    val userData = mapOf(
                                        "averageRating" to newRatingAvg,
                                        "ratingCount" to newRatingCount,
                                        "ratingSum" to newRatingSum
                                    )

                                    db.collection("users")
                                        .document(userIdToRate)
                                        .update(userData)
                                        .addOnSuccessListener {
                                            onRatingSubmitted(rating)
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to update rating", Toast.LENGTH_SHORT).show()
                                        }
                                }
                    } else {
                        Toast.makeText(context, "Please select a rating", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = rating > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Rating")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}