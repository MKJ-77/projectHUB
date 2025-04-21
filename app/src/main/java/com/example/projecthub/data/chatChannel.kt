package com.example.projecthub.data

import com.google.firebase.Timestamp

data class chatChannel(
    val channelId: String = "",           // Unique ID of the chat
    val assignmentId: String = "",        // Assignment related to the chat
    val posterId: String = "",            // User who posted the assignment
    val bidderId: String = "",            // User who placed the bid
    val timestamp: Timestamp = Timestamp.now()
)
