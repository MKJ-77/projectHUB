package com.example.projecthub.screens

import ChatWallpaperBackground
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projecthub.data.message
import com.example.projecthub.usecases.formatTimeStamp
import com.example.projecthub.usecases.sendMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.projecthub.usecases.listenForMessages
import com.example.projecthub.viewModel.ThemeViewModel
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    chatChannelId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val messageListState = rememberLazyListState()

    var messages by remember { mutableStateOf<List<message>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var otherUserName by remember { mutableStateOf("Chat") }
    var otherUserId by remember { mutableStateOf("") }
    var otherUserPhotoId by remember { mutableStateOf(com.example.projecthub.R.drawable.profilephoto1) }
    var isLoading by remember { mutableStateOf(true) }
    val themeViewModel: ThemeViewModel = viewModel()

    DisposableEffect(chatChannelId) {
        var listener: ListenerRegistration? = null

        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("chatChannels")
                .document(chatChannelId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user1Id = document.getString("user1Id") ?: ""
                        val user2Id = document.getString("user2Id") ?: ""

                        otherUserId = if (currentUserId == user1Id) user2Id else user1Id

                        if (otherUserId.isNotEmpty()) {
                            db.collection("users")
                                .document(otherUserId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    otherUserName = userDoc.getString("name") ?: "Chat"
                                    userDoc.getLong("profilePhotoId")?.toInt()?.let {
                                        otherUserPhotoId = it
                                    }
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }
    val isDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(24.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = otherUserPhotoId),
                                contentDescription = "Profile photo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (otherUserId.isNotEmpty()) {
                                            navController.navigate("user_profile/${otherUserId}")
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = otherUserName,
                                modifier = Modifier.clickable {
                                    if (otherUserId.isNotEmpty()) {
                                        navController.navigate("user_profile/${otherUserId}")
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = if (isDarkTheme)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = if (isDarkTheme)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surface),///
                contentAlignment = Alignment.Center
            ) {

//                chatWallpaperBackground(modifier = Modifier.fillMaxSize())
                ChatWallpaperBackground(themeViewModel = themeViewModel)

                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    ChatWallpaperBackground(themeViewModel = themeViewModel)
                    if (messages.isEmpty()) {
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

                Surface(
                    color = if (isDarkTheme)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = {
                                Text(
                                    "Type your message....",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(24.dp),

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            singleLine = false,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
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
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(48.dp)
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
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)


    val textColor = if (isCurrentUser)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    val timeString = SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(message.timestamp.toDate())

    val isLikelySingleLine = message.text.length < 30

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
            modifier = Modifier
                .widthIn(max = 280.dp)
                .wrapContentWidth(),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (isLikelySingleLine) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = message.text,
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}