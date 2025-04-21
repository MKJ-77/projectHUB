package com.example.projecthub.data

import com.google.firebase.Timestamp


data class chatChannel(
    val channelId: String = "",           // Unique ID of the chat
    val user1Id: String = "",             // First user ID (ordering doesn't matter)
    val user2Id: String = "",             // Second user ID
    val lastMessageText: String = "",     // Last message in chat
    val lastMessageTimestamp: Timestamp = Timestamp.now()
)