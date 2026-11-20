package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.KAUViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(viewModel: KAUViewModel) {
    var messageInput by remember { mutableStateOf("") }
    val currentUser = viewModel.currentUser
    val targetRecipientId = if (currentUser?.role == "farmer") 1 else 2

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentUser?.role == "farmer") "Query Officer" else "Farmer Inquiries", color = Color.White) },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.messages) { msg ->
                    val isMe = msg.senderId == currentUser?.id
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMe) Color(0xFFC8E6C9) else Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = msg.body, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = msg.createdAt, fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Type query or reply...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                viewModel.sendMessage(targetRecipientId, messageInput)
                                messageInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}
