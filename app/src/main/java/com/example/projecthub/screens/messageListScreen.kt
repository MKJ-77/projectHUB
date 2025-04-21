package com.example.projecthub.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecthub.R
import com.example.projecthub.data.chatChannel
import com.example.projecthub.navigation.routes
import com.example.projecthub.usecases.MainAppBar
import com.example.projecthub.usecases.NoChannelsMessage
import com.example.projecthub.usecases.bottomNavigationBar
import com.example.projecthub.usecases.bubbleBackground
import com.example.projecthub.usecases.formatTimeStamp
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    navController: NavHostController,
    authViewModel: authViewModel
) {
    var chats by remember { mutableStateOf<List<ChatWithUserDetails>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    LaunchedEffect(key1 = true) {
        val db = FirebaseFirestore.getInstance()
        try {
            val query1 = db.collection("chatChannels")
                .whereEqualTo("user1Id", currentUserId)
                .get()
                .await()

            val query2 = db.collection("chatChannels")
                .whereEqualTo("user2Id", currentUserId)
                .get()
                .await()

            val processedChats = mutableListOf<ChatWithUserDetails>()

            for (document in query1.documents) {
                val channel = document.toObject(chatChannel::class.java)
                if (channel != null) {
                    val otherUserId = channel.user2Id

                    val userDoc = db.collection("users").document(otherUserId).get().await()
                    val userName = userDoc.getString("name") ?: "Unknown User"
                    val photoId = userDoc.getLong("profilePhotoId")?.toInt() ?: R.drawable.profilephoto1

                    processedChats.add(
                        ChatWithUserDetails(
                            channelId = document.id,
                            channel = channel,
                            otherUserId = otherUserId,
                            otherUserName = userName,
                            otherUserPhotoId = photoId,
                            lastMessage = channel.lastMessageText
                        )
                    )
                }
            }

            for (document in query2.documents) {
                val channel = document.toObject(chatChannel::class.java)
                if (channel != null) {
                    val otherUserId = channel.user1Id

                    val userDoc = db.collection("users").document(otherUserId).get().await()
                    val userName = userDoc.getString("name") ?: "Unknown User"
                    val photoId = userDoc.getLong("profilePhotoId")?.toInt() ?: R.drawable.profilephoto1

                    processedChats.add(
                        ChatWithUserDetails(
                            channelId = document.id,
                            channel = channel,
                            otherUserId = otherUserId,
                            otherUserName = userName,
                            otherUserPhotoId = photoId,
                            lastMessage = channel.lastMessageText
                        )
                    )
                }
            }

            chats = processedChats.sortedByDescending { it.channel.lastMessageTimestamp.seconds }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { MainAppBar(title = "Messages", navController = navController) },
        bottomBar = { bottomNavigationBar(navController = navController, currentRoute = "messages_list") }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            bubbleBackground(modifier = Modifier.fillMaxSize())

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (chats.isEmpty()) {
                NoChannelsMessage(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(chats) { chatDetails ->
                        ChatItem(
                            chatDetails = chatDetails,
                            onClick = {
                                navController.navigate(routes.chatScreen.route.replace("{chatChannelId}", chatDetails.channelId))
                            }
                        )
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    chatDetails: ChatWithUserDetails,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = chatDetails.otherUserPhotoId),
                contentDescription = "User photo",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chatDetails.otherUserName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chatDetails.lastMessage.ifEmpty { "No messages yet" },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatTimeStamp(chatDetails.channel.lastMessageTimestamp.toDate()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class ChatWithUserDetails(
    val channelId: String,
    val channel: chatChannel,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserPhotoId: Int,
    val lastMessage: String
)