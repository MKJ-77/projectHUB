package com.example.projecthub.screens

import androidx.benchmark.perfetto.Row
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projecthub.data.message
import com.example.projecthub.viewModel.authViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(
    chatChannelId: String,
    authViewModel:  authViewModel = viewModel() // Assuming ViewModel for handling messages
) {
    val messages by authViewModel.messages.observeAsState(emptyList()) // Observing messages
    val messageText = remember { mutableStateOf("") } // Text field state

    // Chat UI layout
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat area displaying messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message)
            }
        }

        // Input field and send button
        Row (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                label = { Text("Type a message") },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    if (messageText.value.isNotBlank()) {
                        authViewModel.sendMessage(
                            chatChannelId,
                            messageText.value
                        )
                        messageText.value = "" // Clear the text field after sending
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Message")
            }
        }
    }

    // Start listening for messages in real-time
    LaunchedEffect(chatChannelId) {
        authViewModel.startListeningMessages(chatChannelId)
    }
}

@Composable
fun MessageBubble(message: message) {
    val isSender = message.senderId == FirebaseAuth.getInstance().currentUser?.uid
    val alignment = if (isSender) Alignment.End else Alignment.Start
    val backgroundColor = if (isSender) Color.Blue else Color.Gray
    val textColor = if (isSender) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(alignment)
            .padding(4.dp)
    ) {
        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


