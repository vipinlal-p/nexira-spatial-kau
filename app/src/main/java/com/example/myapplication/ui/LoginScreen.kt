package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.KAUViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: KAUViewModel) {
    var emailInput by remember { mutableStateOf("rajan.farmer@gmail.com") }
    var passwordInput by remember { mutableStateOf("password123") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F3812), Color(0xFF1B5E20), Color(0xFF2E7D32))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF1B5E20)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Nexira Spatial",
                    style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                )
                Text(
                    text = "KAU Paddy Field Mobile GIS",
                    style = TextStyle(fontSize = 13.sp, color = Color(0xFF666666), fontWeight = FontWeight.Medium),
                )
                Spacer(modifier = Modifier.height(28.dp))
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1B5E20)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1B5E20)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.login(emailInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Secure Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (viewModel.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Quick Demo Logins:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF555555))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AssistChip(
                        onClick = { emailInput = "rajan.farmer@gmail.com" },
                        label = { Text("Farmer", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE8F5E9))
                    )
                    AssistChip(
                        onClick = { emailInput = "officer.mannuthy@kau.in" },
                        label = { Text("Officer", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE3F2FD))
                    )
                    AssistChip(
                        onClick = { emailInput = "admin@kau.in" },
                        label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFFFEBEE))
                    )
                }
            }
        }
    }
}
