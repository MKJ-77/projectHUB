package com.example.projecthub.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecthub.data.message
import com.example.projecthub.usecases.sendMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.projecthub.usecases.listenForMessages
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    chatChannelId: String
) {
    var messages by remember { mutableStateOf<List<message>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var otherUserName by remember { mutableStateOf("Chat") }
    var isLoading by remember { mutableStateOf(true) }

    // Single listener with proper error handling
    DisposableEffect(chatChannelId) {
        var listener: ListenerRegistration? = null

        // Get chat channel data and user name
        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("chatChannels")
                .document(chatChannelId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Using the correct field names from the data model
                        val user1Id = document.getString("user1Id") ?: ""
                        val user2Id = document.getString("user2Id") ?: ""

                        val otherUserId = if (currentUserId == user1Id) user2Id else user1Id

                        // Fetch other user's name
                        if (otherUserId.isNotEmpty()) {
                            db.collection("users")
                                .document(otherUserId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    otherUserName = userDoc.getString("name") ?: "Chat"
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ChatScreen", "Error fetching user data: ${e.message}")
                                }
                        }
                    } else {
                        Log.e("ChatScreen", "Chat document doesn't exist")
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Log.e("ChatScreen", "Error loading chat: ${e.message}")
                    isLoading = false
                }

            // Set up the message listener
            listener = listenForMessages(chatChannelId) { fetchedMessages ->
                messages = fetchedMessages
            }
        } catch (e: Exception) {
            Log.e("ChatScreen", "Error in DisposableEffect: ${e.message}", e)
            isLoading = false
        }

        onDispose {
            try {
                listener?.remove()
            } catch (e: Exception) {
                Log.e("ChatScreen", "Error removing listener: ${e.message}", e)
            }
        }
    }

    // Effect to scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(otherUserName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Messages area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (messages.isEmpty()) {
                        // Show empty state
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "No messages yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Send a message to start the conversation",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(messages) { message ->
                                MessageBubble(message = message)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Input field and send button
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Type a message") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            ),
                            singleLine = false,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    sendMessage(
                                        chatChannelId = chatChannelId,
                                        messageText = messageText,
                                        senderId = currentUserId
                                    )
                                    messageText = ""
                                }
                            },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MessageBubble(message: message) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isCurrentUser = message.senderId == currentUserId
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isCurrentUser)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isCurrentUser)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}