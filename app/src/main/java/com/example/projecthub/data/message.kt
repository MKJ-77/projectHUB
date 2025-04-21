package com.example.projecthub.data

import com.google.firebase.Timestamp

data class message(
    val messageId: String = "",           // Unique ID for the message
    val senderId: String = "",            // Who sent the message
    val text: String = "",                // The message content
    val timestamp: Timestamp = Timestamp.now()
)
