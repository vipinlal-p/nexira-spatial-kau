package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.KAUViewModel
import com.example.myapplication.viewmodel.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: KAUViewModel) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val currentUser = viewModel.currentUser
    val isAdmin = currentUser?.role == "admin"
    val isMalayalam = viewModel.isMalayalam

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (selectedTab) {
                            0 -> Strings.get("app_title", isMalayalam)
                            1 -> Strings.get("announcements", isMalayalam)
                            2 -> Strings.get("messages", isMalayalam)
                            else -> Strings.get("admin", isMalayalam)
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                actions = {
                    // Language Switch Button
                    TextButton(
                        onClick = { viewModel.toggleLanguage() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text(
                            text = if (isMalayalam) "English" else "മലയാളം",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFFFC107)
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile & Menu",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(currentUser?.name ?: "User", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B5E20))
                                        Text(currentUser?.email ?: "", fontSize = 11.sp, color = Color(0xFF555555))
                                        Text("Role: ${currentUser?.role?.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                                        Text("Krishi Bhavan ID: ${currentUser?.krishiBhavanId}", fontSize = 11.sp, color = Color(0xFF555555))
                                    }
                                },
                                onClick = {}
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text(Strings.get("logout", isMalayalam), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold) },
                                onClick = {
                                    showMenu = false
                                    viewModel.logout()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Logout,
                                        contentDescription = "Logout",
                                        tint = Color(0xFFD32F2F)
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1B5E20)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    label = { Text(Strings.get("gis_map", isMalayalam)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFFC107),
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color(0xFFFFC107),
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2E7D32)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) },
                    label = { Text(Strings.get("announcements", isMalayalam)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFFC107),
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color(0xFFFFC107),
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2E7D32)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text(Strings.get("messages", isMalayalam)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFFC107),
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color(0xFFFFC107),
                        unselectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2E7D32)
                    )
                )
                if (isAdmin) {
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                        label = { Text(Strings.get("admin", isMalayalam)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFC107),
                            unselectedIconColor = Color.White,
                            selectedTextColor = Color(0xFFFFC107),
                            unselectedTextColor = Color.White,
                            indicatorColor = Color(0xFF2E7D32)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> MapScreen(viewModel)
                1 -> AnnouncementsScreen(viewModel)
                2 -> MessagesScreen(viewModel)
                3 -> AdminScreen(viewModel)
            }
        }
    }
}
