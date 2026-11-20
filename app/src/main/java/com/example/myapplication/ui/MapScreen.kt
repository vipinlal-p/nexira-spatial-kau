package com.example.myapplication.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapplication.model.PaddyField
import com.example.myapplication.viewmodel.KAUViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

object EsriWorldStreetMapSource : OnlineTileSourceBase(
    "EsriWorldStreetMap",
    0, 19, 256, "",
    arrayOf(
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"
    ),
    "Esri, DeLorme, NAVTEQ"
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: KAUViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedField by remember { mutableStateOf<PaddyField?>(null) }
    var isMapMode by remember { mutableStateOf(true) }
    var mapController by remember { mutableStateOf<IMapController?>(null) }
    val currentUser = viewModel.currentUser

    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val filteredFields = viewModel.paddyFields.filter { field ->
        val matchesSearch = field.surveyNumber.contains(searchQuery, ignoreCase = true) ||
                field.farmerName.contains(searchQuery, ignoreCase = true)
        val matchesRole = if (currentUser?.role == "farmer") {
            field.farmerId == currentUser.id
        } else {
            true
        }
        matchesSearch && matchesRole
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar & View Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Survey No. or Farmer...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1B5E20)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = { isMapMode = !isMapMode },
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF1B5E20), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = if (isMapMode) Icons.Default.List else Icons.Default.Map,
                        contentDescription = "Toggle View",
                        tint = Color(0xFFFFC107)
                    )
                }
            }

            if (isMapMode) {
                // Interactive Map View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            Configuration.getInstance().userAgentValue = "NexiraSpatialKAU/1.0 (Android GIS)"
                            MapView(ctx).apply {
                                setTileSource(EsriWorldStreetMapSource)
                                setMultiTouchControls(true)
                                mapController = controller
                                controller.setZoom(15.0)
                                controller.setCenter(GeoPoint(10.547, 76.290))
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        update = { mapView ->
                            mapView.overlays.clear()

                            filteredFields.forEach { field ->
                                val polygon = Polygon().apply {
                                    points = listOf(
                                        GeoPoint(field.latitude, field.longitude),
                                        GeoPoint(field.latitude + 0.002, field.longitude),
                                        GeoPoint(field.latitude + 0.002, field.longitude + 0.003),
                                        GeoPoint(field.latitude, field.longitude + 0.003),
                                        GeoPoint(field.latitude, field.longitude)
                                    )
                                    val isMyField = currentUser?.role == "farmer" && field.farmerId == currentUser.id
                                    fillColor = if (isMyField) 0x66FFC107.toInt() else 0x442E7D32.toInt()
                                    strokeColor = if (isMyField) 0xFFFF8F00.toInt() else 0xFF1B5E20.toInt()
                                    strokeWidth = 4.0f
                                    title = "Survey: ${field.surveyNumber} (${field.farmerName})"
                                    setOnClickListener { _, _, _ ->
                                        selectedField = field
                                        true
                                    }
                                }
                                mapView.overlays.add(polygon)

                                val marker = Marker(mapView).apply {
                                    position = GeoPoint(field.latitude + 0.001, field.longitude + 0.0015)
                                    title = "Survey: ${field.surveyNumber}"
                                    snippet = "${field.cropSeason} | ${field.areaHa} Ha | ${field.farmerName}"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    setOnMarkerClickListener { _, _ ->
                                        selectedField = field
                                        true
                                    }
                                }
                                mapView.overlays.add(marker)
                            }

                            if (hasLocationPermission) {
                                val locationOverlay = MyLocationNewOverlay(
                                    GpsMyLocationProvider(mapView.context),
                                    mapView
                                ).apply {
                                    enableMyLocation()
                                }
                                mapView.overlays.add(locationOverlay)
                            }

                            mapView.invalidate()
                        }
                    )

                    // Floating Action Button
                    FloatingActionButton(
                        onClick = {
                            val targetField = filteredFields.firstOrNull()
                            if (targetField != null && mapController != null) {
                                mapController?.setZoom(17.0)
                                mapController?.setCenter(GeoPoint(targetField.latitude, targetField.longitude))
                            }
                        },
                        containerColor = Color(0xFF1B5E20),
                        contentColor = Color(0xFFFFC107),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Move to Plot")
                    }
                }
            } else {
                // Fields List View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredFields) { field ->
                        val isMyField = currentUser?.role == "farmer" && field.farmerId == currentUser.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedField = field
                                    isMapMode = true
                                    mapController?.setZoom(17.0)
                                    mapController?.setCenter(GeoPoint(field.latitude, field.longitude))
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMyField) Color(0xFFFFF8E1) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isMyField) Color(0xFFFFB300) else Color(0xFF1B5E20),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Grass, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Survey: ${field.surveyNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF1B5E20)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = "${field.areaHa} Ha",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Farmer: ${field.farmerName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF444444)
                                    )
                                    Text(
                                        text = "Season: ${field.cropSeason} | Soil: ${field.soilType}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF777777)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet for Field Attributes
    if (selectedField != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedField = null },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Survey No: ${selectedField!!.surveyNumber}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = selectedField!!.cropSeason,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                Text("Farmer: ${selectedField!!.farmerName} (${selectedField!!.farmerPhone})", fontSize = 14.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Area: ${selectedField!!.areaHa} Hectares", fontSize = 14.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Soil Type: ${selectedField!!.soilType}", fontSize = 14.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(16.dp))
                Text("GIS Spatial Attributes:", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedField!!.attributes.forEach { (k, v) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$k:", fontSize = 12.sp, color = Color(0xFF666666))
                                Text(v, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
