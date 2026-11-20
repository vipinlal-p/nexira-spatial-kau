package com.example.myapplication.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.KAUViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: KAUViewModel) {
    var adminTab by rememberSaveable { mutableStateOf(0) } // 0: GIS & Fields, 1: Users (RBAC), 2: Krishi Bhavans, 3: Audit Logs
    var showAddFieldDialog by remember { mutableStateOf(false) }
    var showAddFarmerDialog by remember { mutableStateOf(false) }
    var showAddKbDialog by remember { mutableStateOf(false) }

    var surveyInput by remember { mutableStateOf("") }
    var selectedFarmerName by remember { mutableStateOf("") }
    var farmerDropdownExpanded by remember { mutableStateOf(false) }

    var areaInput by remember { mutableStateOf("") }
    var geoJsonInput by remember { mutableStateOf("{\"type\": \"Polygon\", \"coordinates\": [[[76.29, 10.54], [76.31, 10.54], [76.31, 10.56], [76.29, 10.56], [76.29, 10.54]]]}") }
    var seasonInput by remember { mutableStateOf("Virippu") }
    var soilInput by remember { mutableStateOf("Clay Loam") }
    var latInput by remember { mutableStateOf("10.548") }
    var lonInput by remember { mutableStateOf("76.292") }

    var farmerNameInput by remember { mutableStateOf("") }
    var farmerPhoneInput by remember { mutableStateOf("") }
    var farmerEmailInput by remember { mutableStateOf("") }

    var kbNameInput by remember { mutableStateOf("") }
    var kbDistrictInput by remember { mutableStateOf("Thrissur") }

    val context = LocalContext.current
    val totalFields = viewModel.paddyFields.size
    val totalArea = viewModel.paddyFields.sumOf { it.areaHa }
    val totalFarmers = viewModel.users.count { it.role == "farmer" }
    val totalOfficers = viewModel.users.count { it.role == "officer" }

    val farmerList = viewModel.users.filter { it.role == "farmer" }
    if (selectedFarmerName.isBlank() && farmerList.isNotEmpty()) {
        selectedFarmerName = farmerList.first().name
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Sub-Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = adminTab,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = adminTab == 0,
                    onClick = { adminTab = 0 },
                    text = { Text("GIS Fields ($totalFields)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    text = { Text("Users & RBAC", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = adminTab == 2,
                    onClick = { adminTab = 2 },
                    text = { Text("Krishi Bhavans", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = adminTab == 3,
                    onClick = { adminTab = 3 },
                    text = { Text("Audit Logs", fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            when (adminTab) {
                0 -> {
                    // GIS Fields & Shapefile Ingestion
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Area", fontSize = 11.sp, color = Color(0xFF555555))
                                        Text("${String.format("%.2f", totalArea)} Ha", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Farmers / Officers", fontSize = 11.sp, color = Color(0xFF555555))
                                        Text("$totalFarmers / $totalOfficers", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1565C0))
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { showAddFieldDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload Shapefile / GeoJSON / Draw Polygon", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        item {
                            Text("Mapped Paddy Field Parcels", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 16.sp)
                        }

                        items(viewModel.paddyFields) { field ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Survey: ${field.surveyNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 15.sp)
                                        Text("Farmer: ${field.farmerName} | Area: ${field.areaHa} Ha", fontSize = 13.sp, color = Color(0xFF333333))
                                        Text("Season: ${field.cropSeason} | Lat/Lon: (${field.latitude}, ${field.longitude})", fontSize = 11.sp, color = Color(0xFF666666))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deletePaddyField(field.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Users & RBAC Management
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddFarmerDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Register New Farmer", fontWeight = FontWeight.Bold)
                            }
                        }

                        item {
                            Text("User Role & Access Control (RBAC)", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 16.sp)
                        }

                        items(viewModel.users) { usr ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(usr.name, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 15.sp)
                                            Text("Email: ${usr.email} | Phone: ${usr.phone}", fontSize = 12.sp, color = Color(0xFF444444))
                                            Text("Role: ${usr.role.uppercase()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        }
                                        IconButton(onClick = { viewModel.deleteUser(usr.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color(0xFFD32F2F))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.updateUserRole(usr.id, "farmer") },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF388E3C))
                                        ) {
                                            Text("Farmer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.updateUserRole(usr.id, "officer") },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1976D2))
                                        ) {
                                            Text("Officer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.updateUserRole(usr.id, "admin") },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                                        ) {
                                            Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Krishi Bhavans Management
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddKbDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.LocationCity, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add New Krishi Bhavan", fontWeight = FontWeight.Bold)
                            }
                        }

                        items(viewModel.krishiBhavans) { kb ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(kb.name, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 16.sp)
                                    Text("District: ${kb.district}", fontSize = 13.sp, color = Color(0xFF444444))
                                    Text("Jurisdiction: PostGIS MultiPolygon Enabled", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Message Audit Logs
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("System Communication Audit Logs", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 16.sp)
                        }

                        items(viewModel.messages) { msg ->
                            val sender = viewModel.users.find { it.id == msg.senderId }?.name ?: "User #${msg.senderId}"
                            val receiver = viewModel.users.find { it.id == msg.receiverId }?.name ?: "User #${msg.receiverId}"

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "$sender ➔ $receiver",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF1B5E20),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = msg.createdAt,
                                            fontSize = 10.sp,
                                            color = Color(0xFF666666)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.body,
                                        fontSize = 13.sp,
                                        color = Color(0xFF222222),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddFieldDialog) {
        AlertDialog(
            onDismissRequest = { showAddFieldDialog = false },
            title = { Text("Upload Shapefile / GeoJSON / Draw Polygon", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Paste GeoJSON Polygon or Shapefile Geometry below:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
                    OutlinedTextField(
                        value = geoJsonInput,
                        onValueChange = { geoJsonInput = it },
                        label = { Text("GeoJSON / Polygon Coordinates") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = surveyInput,
                        onValueChange = { surveyInput = it },
                        label = { Text("Survey Number (e.g. SY-145/3)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = farmerDropdownExpanded,
                        onExpandedChange = { farmerDropdownExpanded = !farmerDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedFarmerName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Associated Farmer") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = farmerDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = farmerDropdownExpanded,
                            onDismissRequest = { farmerDropdownExpanded = false }
                        ) {
                            farmerList.forEach { farmer ->
                                DropdownMenuItem(
                                    text = { Text(farmer.name) },
                                    onClick = {
                                        selectedFarmerName = farmer.name
                                        farmerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = areaInput,
                        onValueChange = { areaInput = it },
                        label = { Text("Area in Hectares (e.g. 1.25)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = latInput,
                            onValueChange = { latInput = it },
                            label = { Text("Latitude") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lonInput,
                            onValueChange = { lonInput = it },
                            label = { Text("Longitude") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = seasonInput,
                        onValueChange = { seasonInput = it },
                        label = { Text("Crop Season (Virippu/Mundakan/Puncha)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = soilInput,
                        onValueChange = { soilInput = it },
                        label = { Text("Soil Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val area = areaInput.toDoubleOrNull() ?: 1.0
                        val lat = latInput.toDoubleOrNull() ?: 10.548
                        val lon = lonInput.toDoubleOrNull() ?: 76.292
                        if (surveyInput.isNotBlank()) {
                            viewModel.addPaddyField(
                                surveyNumber = surveyInput,
                                farmerName = selectedFarmerName.ifBlank { "Rajan P.K." },
                                areaHa = area,
                                cropSeason = seasonInput,
                                soilType = soilInput,
                                lat = lat,
                                lon = lon
                            )
                            surveyInput = ""
                            areaInput = ""
                            showAddFieldDialog = false
                            Toast.makeText(context, "Paddy Field successfully published to GIS map!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Publish Polygon to GIS Map")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFieldDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddFarmerDialog) {
        AlertDialog(
            onDismissRequest = { showAddFarmerDialog = false },
            title = { Text("Register New Farmer") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = farmerNameInput,
                        onValueChange = { farmerNameInput = it },
                        label = { Text("Farmer Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = farmerPhoneInput,
                        onValueChange = { farmerPhoneInput = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = farmerEmailInput,
                        onValueChange = { farmerEmailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (farmerNameInput.isNotBlank() && farmerPhoneInput.isNotBlank()) {
                            viewModel.addFarmer(
                                name = farmerNameInput,
                                phone = farmerPhoneInput,
                                email = farmerEmailInput.ifBlank { "${farmerNameInput.lowercase().replace(" ", "")}@kau.in" },
                                krishiBhavanId = 1
                            )
                            farmerNameInput = ""
                            farmerPhoneInput = ""
                            farmerEmailInput = ""
                            showAddFarmerDialog = false
                            Toast.makeText(context, "New farmer registered successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Register Farmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFarmerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddKbDialog) {
        AlertDialog(
            onDismissRequest = { showAddKbDialog = false },
            title = { Text("Add New Krishi Bhavan") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = kbNameInput,
                        onValueChange = { kbNameInput = it },
                        label = { Text("Krishi Bhavan Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = kbDistrictInput,
                        onValueChange = { kbDistrictInput = it },
                        label = { Text("District") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (kbNameInput.isNotBlank()) {
                            viewModel.addKrishiBhavan(kbNameInput, kbDistrictInput)
                            kbNameInput = ""
                            showAddKbDialog = false
                            Toast.makeText(context, "Krishi Bhavan added successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Add Krishi Bhavan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKbDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
